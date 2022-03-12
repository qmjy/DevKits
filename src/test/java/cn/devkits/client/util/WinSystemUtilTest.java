package cn.devkits.client.util;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class WinSystemUtilTest {

    private WinSystemUtil winSystemUtil;

    @Before
    public void prepare() {
        this.winSystemUtil = new WinSystemUtil();
    }

    @Test
    public void testGetWifiNamesOfConnected() {
        List<String> wifiNamesOfConnected = winSystemUtil.getSsidNamesOfConnected();
        assertTrue(wifiNamesOfConnected.size() >= 0);
    }

    @Test
    public void testGetPwdOfWifi() {
        String dd = winSystemUtil.getPwdOfSsid("dd");
        if (dd != null) {
            assertTrue("liu891231".equals(dd));
        }
    }

    @Test
    public void testExecuteWinCmd() {
    }
}