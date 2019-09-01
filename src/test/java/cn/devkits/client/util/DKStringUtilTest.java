package cn.devkits.client.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class DKStringUtilTest {

	@Test
	public void testIpCheck() {
		assertTrue(DKStringUtil.ipCheck("192.168.1.1"));
		assertFalse(DKStringUtil.ipCheck("2.3.5"));
	}

}
