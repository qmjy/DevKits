package cn.devkits.client.util.calendar;

import static org.junit.Assert.*;
import org.junit.Test;

public class LunarCalendarTest {

    @Test
    public void testGetLunarTextCalendar() {
        assertEquals("植树节", LunarCalendar.getLunarText(2019, 3, 12));
        assertEquals("廿八", LunarCalendar.getLunarText(2019, 11, 24));
        assertEquals("冬月", LunarCalendar.getLunarText(2019, 11, 26));
        assertEquals("圣诞节", LunarCalendar.getLunarText(2019, 12, 25));
        assertEquals("除夕", LunarCalendar.getLunarText(2020, 1, 24));
    }

}
