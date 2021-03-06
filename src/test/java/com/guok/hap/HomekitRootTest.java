package com.guok.hap;

import com.google.common.util.concurrent.Futures;

import com.guok.hap.impl.HomekitWebHandler;
import com.guok.hap.impl.advertiser.AbstractAdvertiser;
import com.guok.hap.impl.http.HomekitClientConnectionFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HomekitRootTest {

    private HomekitAccessory accessory;
    private HomeKitRoot root;
    private HomekitWebHandler webHandler;
    private AbstractAdvertiser advertiser;
    private BridgeAuthInfo authInfo;

    private final static int PORT = 12345;
    private final static String LABEL = "Test Label";


    AccessoryDisplayInfo displayInfo ;

    @Before
    public void setup() throws Exception {
        accessory = mock(HomekitAccessory.class);
        when(accessory.getId()).thenReturn(2);
        webHandler = mock(HomekitWebHandler.class);
        when(webHandler.start(any(HomekitClientConnectionFactory.class))).thenReturn(Futures.immediateFuture(PORT));
        advertiser = mock(AbstractAdvertiser.class);
        authInfo = mock(BridgeAuthInfo.class);
        displayInfo = mock(AccessoryDisplayInfo.class);
        root = new HomeKitRoot(displayInfo, webHandler, authInfo, advertiser);
    }

    @Test
    public void verifyRegistryAdded() throws Exception {
        root.addAccessory(accessory);
        Assert.assertTrue("Registry does not contain accessory", root.getRegistry().getAccessories().contains(accessory));
    }

    @Test
    public void verifyRegistryRemoved() throws Exception {
        root.addAccessory(accessory);
        root.removeAccessory(accessory);
        Assert.assertFalse("Registry still contains accessory", root.getRegistry().getAccessories().contains(accessory));
    }

    @Test
    public void testWebHandlerStarts() throws Exception {
        root.start();
        verify(webHandler).start(any(HomekitClientConnectionFactory.class));
    }

    @Test
    public void testWebHandlerStops() throws Exception {
        root.start();
        root.stop();
        verify(webHandler).stop();
    }

    @Test
    public void testAdvertiserStarts() throws Exception {
        String mac = "00:00:00:00:00:00";
        when(authInfo.getMac()).thenReturn(mac);
        when(displayInfo.getLabel()).thenReturn(LABEL);
        root.start();
        verify(advertiser).advertise(eq(LABEL), eq(mac), eq(PORT), eq(1));
    }

    @Test
    public void testAdvertiserStops() throws Exception {
        root.start();
        root.stop();
        verify(advertiser).stop();
    }

    @Test
    public void testAddAccessoryResetsWeb() {
        root.start();
        verify(webHandler, never()).resetConnections();
        root.addAccessory(accessory);
        verify(webHandler).resetConnections();
    }

    @Test
    public void testRemoveAccessoryResetsWeb() {
        root.addAccessory(accessory);
        root.start();
        verify(webHandler, never()).resetConnections();
        root.removeAccessory(accessory);
        verify(webHandler).resetConnections();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testAddIndexOneAccessory() throws Exception {
        when(accessory.getId()).thenReturn(1);
        root.addAccessory(accessory);
    }

}
