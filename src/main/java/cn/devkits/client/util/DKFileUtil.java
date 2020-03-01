package cn.devkits.client.util;

import cn.devkits.client.App;
import oshi.util.FormatUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

/**
 * 
 * 文件类工具类
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月17日 下午10:44:09
 */
public final class DKFileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    /**
     * 获取文件属性
     * 
     * @param file 文件
     * @return 文件属性
     */
    public static Optional<BasicFileAttributes> getFileAttr(File file) {
        Path p = Paths.get(file.getAbsolutePath());
        try {
            return Optional.of(Files.readAttributes(p, BasicFileAttributes.class));
        } catch (IOException e1) {
            LOGGER.error("Get File Attributes Failed: " + e1.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 获取文件夹下的所有文件大小和
     * @param file 待检查的文件夹
     * @return folder total size
     */
    public static long getFolderSize(File file) {
        if (file == null) {
            return 0;
        }
        return FileUtils.sizeOfDirectory(file);
    }


    /**
     * judge a file is image or not
     * @param f the file need to check
     * @return is file or not
     */
    public static boolean isImg(File f) {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(f));
            String mimeType = URLConnection.guessContentTypeFromStream(is);
            if (mimeType == null) {
                return false;
            }

            String type = mimeType.split("/")[0];
            return type.equals("image");
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found [{}]", f.getAbsoluteFile());
        } catch (IOException e) {
            LOGGER.error("IOException while judge file type: [{}]", f.getAbsolutePath());
        } finally {
            IoUtils.closeQuietly(is);
        }
        return false;
    }


    /**
     * open a file
     * @param file the file need to open
     * @return open success or not
     */
    public static boolean openFile(File file) {
        try {
            Desktop.getDesktop().open(file);
            return true;
        } catch (IOException e1) {
            LOGGER.error("Open file failed: " + file.getAbsolutePath());
        }
        return false;
    }

    /**
     * empty folder
     * @param ouputFolder the folder need to empty
     */
    public static void clearFolder(File ouputFolder) {
        File[] listFiles = ouputFolder.listFiles();
        for (File file : listFiles) {
            if (file.isDirectory()) {
                clearFolder(file);
            } else {
                boolean delete = file.delete();
                if (!delete) {
                    LOGGER.error("Clear file '{}' from folder '{}' failed!", file.getAbsoluteFile(), ouputFolder.getAbsoluteFile());
                }
            }
        }
    }

    /**
     * open text file with system text editor
     * @param f the file need to open with system editor
     */
    public static void openTextFile(File f) {
        if (DKSystemUtil.isWindows()) {
            try {
                ProcessBuilder pb = new ProcessBuilder("notepad", f.getAbsolutePath());
                pb.start();
            } catch (IOException e) {
                LOGGER.error("Open file with text editor failed: {}", f.getAbsolutePath());
            }
        } else {
            // TODO
            LOGGER.info("Can't implement open text file yet！");
        }
    }

    /**
     * 格式化容量格式
     * @param bytes 原始long格式
     * @return 可读的格式
     */
    public static String formatBytes(long bytes) {
        String formatBytes = FormatUtil.formatBytes(bytes);
        if (DKSystemUtil.isWindows()) {
            return formatBytes.replace("i", "");
        }
        return formatBytes;
    }


    /**
     * convert file unit to long byte
     * @param fileUnit file unit of string format
     * @return long size of the input unit
     */
    public static long convertUnti2Val(String fileUnit) {
        switch (fileUnit) {
            case "Byte":
                return 1L;
            case "KB":
                return 1L * 1024;
            case "MB":
                return 1L * 1024 * 1024;
            case "GB":
                return 1L * 1024 * 1024 * 1024;
            case "TB":
                return 1L * 1024 * 1024 * 1024 * 1024;
            case "PB":
                return 1L * 1024 * 1024 * 1024 * 1024 * 1024;
            default:
                return 1L;
        }
    }

    /**
     * 获取操作系统文件ICON
     * @param f 待获取icon的file
     * @return 操作系统文件ICON
     */
    public static Icon getFileIcon(File f) {

        // Another way to get system file icon
        // FileSystemView fsv = new JFileChooser().getFileSystemView();
        // Icon icon = fsv.getSystemIcon(f);
        // ImageIcon icon1 = (ImageIcon) icon;
        // Image image = icon1.getImage();

        return FileSystemView.getFileSystemView().getSystemIcon(f);
    }



}
