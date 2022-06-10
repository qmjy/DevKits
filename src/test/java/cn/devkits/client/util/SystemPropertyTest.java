package cn.devkits.client.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class SystemPropertyTest {

    public static void main(String[] args) {
        Properties properties = System.getProperties();
        Iterator<Map.Entry<Object, Object>> iterator = properties.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, Object> next = iterator.next();
            System.out.println(next.getKey() + "=" + next.getValue());
        }
    }
}