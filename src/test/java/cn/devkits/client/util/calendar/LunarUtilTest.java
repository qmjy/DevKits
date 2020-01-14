package cn.devkits.client.util.calendar;

import static org.junit.Assert.*;
import org.junit.Test;

public class LunarUtilTest {

    @Test
    public void testSolarToLunar() {
        int[] lunar = LunarUtil.solarToLunar(2019, 11, 24);
        assertEquals(2019, lunar[0]);
        assertEquals(10, lunar[1]);
        assertEquals(28, lunar[2]);
    }

}
