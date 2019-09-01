package cn.devkits.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.InetAddresses;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class DKStringUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(DKStringUtil.class);

	public static final String REG_EXP_MAC = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
	public static final String REG_EXP_IPV4 = "\"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$\"";

	/**
	 * IP address check
	 * 
	 * @param str the string need to check
	 * @return is IP or not
	 */
	public static boolean ipCheck(String str) {
		return InetAddresses.isInetAddress(str);
	}

	/**
	 * IP address check with IP-V4 regular express: {@link cn.devkits.client.util.DKStringUtil#REG_EXP_IPV4}}
	 * 
	 * @param str the string need to check with IP-V4 regular express
	 * @return is IP or not
	 */
	public static boolean ipCheckWithRegExp(String str) {
		return str.matches(REG_EXP_IPV4);
	}

	/**
	 * format ugly JSON string to pretty JSON string
	 * 
	 * @param uglyJSONString ugly JSON String
	 * @return pretty JSON string
	 */
	public static String jsonFormat(String uglyJSONString) {
		if (uglyJSONString == null || uglyJSONString.isEmpty()) {
			throw new IllegalArgumentException("The input json string cannot be set to null or empty.");
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();

		try {
			JsonElement je = jp.parse(uglyJSONString);
			return gson.toJson(je);
		} catch (JsonSyntaxException e) {
			String errorMsg = "Iinvalid json string: " + uglyJSONString;
			LOGGER.error(errorMsg);

			return errorMsg;
		}
	}

	public static void main(String[] args) {
		// System.out.println(ipCheck("2.1.2.3"));
		String json = "{\"data1\":[{\"name\":\"aa\",\"age\":\"12\"},{\"name\":\"bb\",\"age\":\"13\"}],\"data2\":{\"nowpage\":1,\"pagesize\":2}}";
		System.out.println(jsonFormat(json));
	}
}
