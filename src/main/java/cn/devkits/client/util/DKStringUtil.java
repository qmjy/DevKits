/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.util;

import com.drew.lang.annotations.NotNull;
import com.google.common.net.InetAddresses;
import com.google.gson.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * 字符串格式化工具
 *
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年11月25日 下午11:18:58
 */
public class DKStringUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DKStringUtil.class);

    public static final String REG_EXP_MAC = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
    public static final String REG_EXP_IPV4 = "\"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$\"";
    public static final String REG_EXP_DOMAIN = "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";
    /**
     * 正整数
     */
    public static final String REG_NUM_INT_POSITIVE = "^[1-9]\\d*";

    /**
     * 判断一个子串在某个字符串中出现了多少次
     *
     * @param full   待检查的字符串
     * @param subStr 计数的子串
     * @return 统计子串在父串中出现的次数
     */
    public static int countSubStr(@NotNull String full, String subStr) {
        int count = 0;
        while (full.contains(subStr)) {
            full = full.substring(full.indexOf(subStr) + subStr.length());
            count++;
        }
        return count;
    }

    /**
     * Returns true if the supplied string is a valid IP string literal, false otherwise.
     *
     * @param str the string need to check
     * @return is IP or not
     */
    public static boolean isIP(String str) {
        return InetAddresses.isInetAddress(str);
    }

    /**
     * 是否是域名
     *
     * @param ipOrDomain 待校验的字符串
     * @return 是否是域名
     */
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
     * 校验一个字符串是否为正整数
     *
     * @param input 待校验的字符串
     * @return 是否是正整数
     */
    public static boolean isPositiveInt(String input) {
        if (input == null) {
            return false;
        }
        return input.matches(REG_NUM_INT_POSITIVE);
    }

    /**
     * 判断输入字符串是否是正单浮点数
     *
     * @param input 待校验的输入
     * @return 是否是正浮点数
     */
    public static boolean isPositiveFloat(String input) {
        try {
            float valueOf2 = Float.parseFloat(input);
            return valueOf2 > 0;
        } catch (NumberFormatException e) {
            LOGGER.debug("Format float value failed: {}", input);
        }
        return false;
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

        try {
            JsonElement je = JsonParser.parseString(uglyJSONString);
            return gson.toJson(je);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Parse Json Failed: {}", uglyJSONString);
            return "Invalid json string: " + uglyJSONString;
        }
    }

    /**
     * XML 格式化接口
     *
     * @param uglyXmlStr 待格式化的XML
     * @return 格式化以后的XML
     */
    public static String xmlFormat(String uglyXmlStr) {
        SAXReader reader = new SAXReader();
        // 注释：创建一个串的字符输入流
        StringReader in = new StringReader(uglyXmlStr);
        XMLWriter writer = null;
        StringWriter out = null;
        try {
            Document doc = reader.read(in);
            // 注释：创建输出格式
            OutputFormat formater = new OutputFormat("    ", true, "utf-8");
            formater.setTrimText(true);
            formater.setPadText(true);
            // OutputFormat formater = OutputFormat.createPrettyPrint();
            // formater=OutputFormat.createCompactFormat();
            // 注释：设置xml的输出编码
            // 注释：创建输出(目标)
            out = new StringWriter();
            // 注释：创建输出流
            writer = new XMLWriter(out, formater);
            // 注释：输出格式化的串到目标中，执行后。格式化后的串保存在out中。
            writer.write(doc);
        } catch (DocumentException e) {
            LOGGER.error("Parse XML Failed: {}", uglyXmlStr);
            return "Invalid xml string: " + uglyXmlStr;
        } catch (IOException e) {
            LOGGER.error("Parse XML Failed: {}", uglyXmlStr);
            return "Invalid xml string: " + uglyXmlStr;
        } finally {
            IoUtils.closeQuietly(out, in);
            IoUtils.closeQuietly(writer);
        }
        return out.toString();
    }
}
