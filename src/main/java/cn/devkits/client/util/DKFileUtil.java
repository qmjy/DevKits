package cn.devkits.client.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.devkits.client.App;

public final class DKFileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    /**
     * 获取文件属性
     * 
     * @param file 文件
     * @return 文件属性
     */
    public static BasicFileAttributes getFileAttr(File file) {
        Path p = Paths.get(file.getAbsolutePath());
        try {
            return Files.readAttributes(p, BasicFileAttributes.class);
        } catch (IOException e1) {
            LOGGER.error("Get File Attributes Failed: " + e1.getMessage());
            return null;
        }
    }
}
