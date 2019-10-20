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
    public static final String REG_EXP_DOMAIN = "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";

    /**
     * Returns true if the supplied string is a valid IP string literal, false otherwise.
     * 
     * @param str the string need to check
     * @return is IP or not
     */
    public static boolean isIP(String str) {
        return InetAddresses.isInetAddress(str);
    }

    public static boolean isDomain(String ipOrDomain) {
        if ("localhost".equalsIgnoreCase(ipOrDomain)) {
            return true;
        }

        if (ipOrDomain.matches(REG_EXP_DOMAIN)) {
            return true;
        }
        return false;
    }



    /**
     * IP address check with IP-V4 regular express:
     * {@link cn.devkits.client.util.DKStringUtil#REG_EXP_IPV4}}
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



}
