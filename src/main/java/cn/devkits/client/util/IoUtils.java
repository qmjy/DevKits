package cn.devkits.client.util;

import java.io.Closeable;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * IoUtis
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年11月25日 下午11:03:35
 */
public final class IoUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IoUtils.class);

    public static void closeQuietly(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            IOUtils.closeQuietly(closeable);
        }
    }

    public static void closeQuietly(XMLWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                LOGGER.error("Close XMLWriter failed: {}", e.getMessage());
            }
        }
    }

}
