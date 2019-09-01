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
	
	
	public static void main(String[] args) {
		System.out.println("0.2.3.5".matches("\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z"));
	}

}
