package cn.devkits.client.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class DKStringUtilTest {

    @Test
    public void testIpCheck() {
        assertTrue(DKStringUtil.isIP("192.168.1.1"));
        assertFalse(DKStringUtil.isIP("2.3.5"));
    }


    @Test
    public void testIsReachable() {}

}
