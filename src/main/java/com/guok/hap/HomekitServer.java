package com.guok.hap;

import com.guok.hap.impl.HomekitBridge;
import com.guok.hap.impl.HomekitUtils;
import com.guok.hap.impl.advertiser.IAdvertiser;
import com.guok.hap.impl.http.impl.HomekitHttpServer;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.InvalidAlgorithmParameterException;

/**
 * The main entry point for hap-java. Creating an instance of this class will listen for Homekit
 * connections on the supplied port. Only a single root accessory can be added for each unique
 * instance and port, however, that accessory may be a {@link #createBridge(BridgeAuthInfo, String,
 * String, String, String) bridge accessory} containing child accessories.
 *
 * The {@link BridgeAuthInfo AndroidBridge} argument when creating accessories should be an
 * implementation supplied by your application. Several of the values needed for your implementation
 * are provided by this class, specifically {@link #generateKey() generateKey}, {@link
 * #generateMac() generateMac}, and {@link #generateSalt()}. It is important that you provide these
 * same values on each start of your application or Homekit will fail to recognize your device as
 * being the same.
 *
 * @author Andy Lintner
 */
public class HomekitServer {

    private final HomekitHttpServer http;


    private final InetAddress localAddress;

    /**
     * Constructor. Contains an argument indicating the number of threads to use in the http server.
     * The other constructors default this to the number of available processors, however you may
     * increase this in an environment with many users and/or blocking accessory implementations.
     *
     * @param localAddress local address to bind to.
     * @param port         local port to bind to.
     * @param nThreads     number of threads to use in the http server
     * @throws IOException when the server cannot bind to the supplied port
     */
    public HomekitServer(InetAddress localAddress, int port, int nThreads) throws IOException {
        this.localAddress = localAddress;
        http = new HomekitHttpServer(port, nThreads);
    }

    /**
     * Constructor
     *
     * @param localAddress local address to bind to
     * @param port         local port to bind to
     * @throws IOException when the server cannot bind to the supplied port
     */
    public HomekitServer(InetAddress localAddress, int port) throws IOException {
        this(localAddress, port, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Constructor
     *
     * @param port local port to bind to.
     * @throws IOException when the server cannot bind to the supplied port
     */
    public HomekitServer(int port) throws IOException {
        this(InetAddress.getLocalHost(), port);
    }

    /**
     * Stops the service, closing down existing connections and preventing new ones.
     */
    public void stop() {
        http.stop();
    }

    /**
     * Creates a single (non-bridge) accessory
     *
     * @param authInfo  authentication information for this accessory. These values should be
     *                  persisted and re-supplied on re-start of your application.
     * @param accessory accessory implementation. This will usually be an implementation of an
     *                  interface in {#link com.guok.hap.accessories com.guok.hap.accessories}.
     * @return the newly created server. Call {@link HomekitStandaloneAccessoryServer#start start}
     * on this to begin.
     * @throws IOException when mDNS cannot connect to the network
     */
    public HomekitStandaloneAccessoryServer createStandaloneAccessory(BridgeAuthInfo authInfo,
                                                                      HomekitAccessory accessory) throws IOException {
        return new HomekitStandaloneAccessoryServer(accessory, http,
                localAddress, authInfo);
    }

    /**
     * Creates a bridge accessory, capable of holding multiple child accessories. This has the
     * advantage over multiple standalone accessories of only requiring a single pairing from iOS
     * for the bridge.
     *
     * <p>The Operation System with avahi-deamon server , like Ubuntu, Debian, could use this method to create bridge
     *  accessory using default advertiser implement with JMDNS</p>
     *
     * @param authInfo     authentication information for this accessory. These values should be
     *                     persisted and re-supplied on re-start of your application.
     * @param label        label for the bridge. This will show in iOS during pairing.
     * @param manufacturer manufacturer of the bridge. This information is exposed to iOS for
     *                     unknown purposes.
     * @param model        model of the bridge. This is also exposed to iOS for unknown purposes.
     * @param serialNumber serial number of the bridge. Also exposed. Purposes also unknown.
     * @return the bridge, from which you can {@link HomekitRoot#addAccessory add accessories} and
     * then {@link HomekitRoot#start start} handling requests.
     * @throws IOException when mDNS cannot connect to the network
     */
    public HomekitRoot createBridge(BridgeAuthInfo authInfo,
                                    String label,
                                    String manufacturer,
                                    String model,
                                    String serialNumber) throws IOException {
        HomekitRoot root = new HomekitRoot(label, http, authInfo, localAddress);
        root.addAccessory(new HomekitBridge(label, serialNumber, model, manufacturer));
        return root;
    }

    /**
     * Creates a bridge accessory, capable of holding multiple child accessories. This has the
     * advantage over multiple standalone accessories of only requiring a single pairing from iOS
     * for the bridge.
     * <p>you could use other Zeroconf implement to advertise the service, like Bonjour</p>
     * @param authInfo          authentication information for this accessory. These values should be
     *                          persisted and re-supplied on re-start of your application.
     * @param label             label for the bridge. This will show in iOS during pairing.
     * @param manufacturer      manufacturer of the bridge. This information is exposed to iOS for
     *                          unknown purposes.
     * @param model             model of the bridge. This is also exposed to iOS for unknown purposes.
     * @param serialNumber      serial number of the bridge. Also exposed. Purposes also unknown.
     * @param advertiser        Zeroconf implement.
     * @return  the bridge, from which you can {@link HomekitRoot#addAccessory add accessories} and
     * then {@link HomekitRoot#start start} handling requests.
     * @throws IOException when advertiser cannot connect to the network
     */
    public HomekitRoot createBridge(BridgeAuthInfo authInfo,
                                    String label,
                                    String manufacturer,
                                    String model,
                                    String serialNumber,
                                    IAdvertiser advertiser) throws IOException {
        HomekitRoot root = new HomekitRoot(label, http, authInfo, advertiser);
        root.addAccessory(new HomekitBridge(label, serialNumber, model, manufacturer));
        return root;
    }

    /**
     * Generates a value to supply in {@link BridgeAuthInfo#getSalt() AndroidBridge.getSalt()}.
     * This is used to salt the pin-code. You don't need to worry about that though - the salting is
     * done on the plaintext pin. (Yes, plaintext passwords are bad. Please don't secure your
     * nuclear storage facility with this implementation)
     *
     * @return the generated salt
     */
    static public BigInteger generateSalt() {
        return HomekitUtils.generateSalt();
    }

    /**
     * Generates a value to supply in {@link BridgeAuthInfo#getPrivateKey()
     * AndroidBridge.getPrivKey()}. This is used as the private key during pairing and connection
     * setup.
     *
     * @return the generated key
     * @throws InvalidAlgorithmParameterException if the JVM does not contain the necessary
     *                                            encryption algorithms.
     */
    static public byte[] generateKey() throws InvalidAlgorithmParameterException {
        return HomekitUtils.generateKey();
    }

    /**
     * Generates a value to supply in {@link BridgeAuthInfo#getMac() AndroidBridge.getMac()}.
     * This is used as the unique identifier of the accessory during mDNS advertising. It is a valid
     * MAC address generated in the locally administered range so as not to conflict with any
     * commercial devices.
     *
     * @return the generated MAC
     */
    static public String generateMac() {
        return HomekitUtils.generateMac();
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }
}
