package com.guok.hap;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.guok.hap.impl.HomekitBridge;
import com.guok.hap.impl.HomekitRegistry;
import com.guok.hap.impl.HomekitWebHandler;
import com.guok.hap.impl.accessories.BaseAccessory;
import com.guok.hap.impl.accessories.Bridge;
import com.guok.hap.impl.advertiser.IAdvertiser;
import com.guok.hap.impl.connections.HomekitClientConnectionFactoryImpl;
import com.guok.hap.impl.connections.SubscriptionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Provides advertising and handling for Homekit accessories. This class handles the advertising of
 * Homekit accessories and contains one or more accessories. When implementing a bridge accessory,
 * you will interact with this class directly. Instantiate it via {@link
 * HomekitServer#createBridge(BridgeAuthInfo, AccessoryDisplayInfo)}. For single
 * accessories, this is composed by {@link HomekitStandaloneAccessoryServer}.
 *
 * @author Andy Lintner
 */
public class HomeKitRoot {

    private final static Logger logger = LoggerFactory.getLogger(HomeKitRoot.class);

    private final IAdvertiser advertiser;
    private final HomekitWebHandler webHandler;
    private final BridgeAuthInfo authInfo;
    private final HomekitRegistry registry;
    private final SubscriptionManager subscriptions = new SubscriptionManager();

    private volatile boolean started = false;
    private int configurationIndex = 1;
    private final AccessoryDisplayInfo mDisplayInfo;

    public HomeKitRoot(AccessoryDisplayInfo displayInfo,
                       HomekitWebHandler webHandler,
                       BridgeAuthInfo authInfo,
                       IAdvertiser advertiser) throws IOException {
        this.advertiser = advertiser;
        this.webHandler = webHandler;
        this.authInfo = authInfo;
        this.mDisplayInfo = displayInfo;
        this.registry = new HomekitRegistry(displayInfo.getLabel());

        addAccessory(new HomekitBridge(displayInfo));
    }

    /**
     * Add an accessory to be handled and advertised by this root. Any existing Homekit connections
     * will be terminated to allow the clients to reconnect and see the updated accessory list. When
     * using this for a bridge, the ID of the accessory must be greater than 1, as that ID is
     * reserved for the Bridge itself.
     *
     * @param accessory to IAdvertiser and handle.
     */
    public <T extends HomekitAccessory> T addAccessory(T accessory) {
        if (accessory.getId() <= 1 && !(accessory instanceof Bridge)) {
            throw new IndexOutOfBoundsException("The ID of an accessory used in a bridge must be greater than 1");
        }
        addAccessorySkipRangeCheck(accessory);
        return accessory;
    }

    /**
     * Skips the range check. Used by {@link HomekitStandaloneAccessoryServer} as well as {@link
     * #addAccessory(HomekitAccessory)};
     *
     * @param accessory to IAdvertiser and handle.
     */
    void addAccessorySkipRangeCheck(HomekitAccessory accessory) {
        this.registry.add(accessory);
        logger.info("Added accessory " + accessory.getLabel());
        reLoadAccessory();
    }

    public BaseAccessory getSpecificAccessory(int id) {
        return this.registry.getSpecificAccessory(id);
    }

    /**
     * Removes an accessory from being handled or advertised by this root. Any existing Homekit
     * connections will be terminated to allow the clients to reconnect and see the updated
     * accessory list.
     *
     * @param accessory accessory to cease advertising and handling
     */
    public void removeAccessory(HomekitAccessory accessory) {
        this.registry.remove(accessory);
        logger.info("Removed accessory " + accessory.getLabel());
        reLoadAccessory();
    }

    /**
     * Starts advertising and handling the previously added Homekit accessories. You should try to
     * call this after you have used the {@link #addAccessory(HomekitAccessory)} method to add all
     * the initial accessories you plan on advertising, as any later additions will cause the
     * Homekit clients to reconnect.
     */
    public void start() {
        if (!started) {
            started = true;
            registry.reset();

            Futures.transform(webHandler.start(new HomekitClientConnectionFactoryImpl(authInfo, registry, subscriptions, advertiser))
                    , new Function<Integer, Object>() {
                        @Override
                        public Object apply(Integer port) {
                            try {
                                refreshAuthInfo();
                                advertiser.advertise(mDisplayInfo.getLabel(), authInfo.getMac(), port, configurationIndex);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            return null;
                        }
                    });
        } else
            logger.error("Bridge accessory already running!");
    }

    /**
     * Stops advertising and handling the Homekit accessories.
     */
    public void stop() {
        advertiser.stop();
        webHandler.stop();
        started = false;
    }

    /**
     * restart all service
     */
    public void reStart() {
        stop();
        authInfo.initPairParams();
        advertiser.setReStart(true);
        webHandler.setPort(authInfo.getPort());
        start();
    }

    /**
     * Refreshes auth info after it has been changed outside this library
     *
     * @throws IOException if there is an error in the underlying protocol, such as a TCP error
     */
    public void refreshAuthInfo() throws IOException {
//        advertiser.setDiscoverable(!authInfo.hasUser());
//        advertiser.setDiscoverable(true); //gk
    }

    /**
     * By default, most homekit requests require that the client be paired. Allowing unauthenticated
     * requests can be useful for debugging, but should not be used in production.
     *
     * @param allow whether to allow unauthenticated requests
     */
    public void allowUnauthenticatedRequests(boolean allow) {
        registry.setAllowUnauthenticatedRequests(allow);
    }


    /**
     * By default, the bridge advertises itself at revision 1. If you make changes to the
     * accessories you're including in the bridge after your first call to {@link #start()}, you
     * should increment this number. The behavior of the client if the configuration index were to
     * decrement is undefined, so this implementation will not manage the configuration index by
     * automatically incrementing - preserving this state across invocations should be handled
     * externally.
     *
     * @param revision an integer, greater than or equal to one, indicating the revision of the
     *                 accessory information
     * @throws IOException if there is an error in the underlying protocol, such as a TCP error
     */
    public void setConfigurationIndex(int revision) throws IOException {
        if (revision < 1) {
            throw new IllegalArgumentException("revision must be greater than or equal to 1");
        }
        this.configurationIndex = revision;
        if (this.started) {
            advertiser.setConfigurationIndex(revision);
        }
    }

    HomekitRegistry getRegistry() {
        return registry;
    }


    public void reLoadAccessory() {
        if (started) {
            registry.reset();
            webHandler.resetConnections();
        }
    }
}
