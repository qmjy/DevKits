package cn.devkits.client.util;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class WinSystemUtilTest {

    private WinSystemUtil winSystemUtil;

    @Before
    public void prepare() {
        this.winSystemUtil = new WinSystemUtil();
    }


    @Test
    public void testGetPwdOfWifi() {
        String dd = winSystemUtil.getPwdOfSsid("dd");
        if (dd != null) {
            assertTrue("lwx166646".equals(dd));
        }
    }

    @Test
    public void testExecuteWinCmd() {
    }
}