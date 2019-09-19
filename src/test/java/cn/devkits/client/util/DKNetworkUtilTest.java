/**
 * 
 */
package cn.devkits.client.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Administrator
 *
 */
public class DKNetworkUtilTest {

    /**
     * Test method for {@link cn.devkits.client.util.DKNetworkUtil#getMac()}.
     */
    @Test
    public void testGetMac() {
        String mac = DKNetworkUtil.getMac();

        assertNotNull(mac);
        assertTrue(mac.matches(DKStringUtil.REG_EXP_MAC));
    }

    @Test
    public void testHostReachable() {
        assertTrue(DKNetworkUtil.hostReachable("127.0.0.1"));
        assertTrue(DKNetworkUtil.hostReachable("localhost"));
        assertTrue(DKNetworkUtil.hostReachable("www.huawei.com"));
        assertFalse(DKNetworkUtil.hostReachable("ww.test.cmvvv"));
    }

}
