/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.util;

import cn.devkits.client.App;
import cn.devkits.client.DKConstants;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oshi.util.FormatUtil;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.List;

/**
 * 文件类工具类
 *
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月17日 下午10:44:09
 */
public final class DKFileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    /**
     * 常见图片的字节码头
     */
    private static final Map<String, byte[]> STANDARD_OF_IMG_HEADER = ImmutableMap.<String, byte[]>builder()
            .put(".jpg", new byte[]{(byte) 0xFF, (byte) 0xD8})
            .put(".png", new byte[]{(byte) 0x89, (byte) 0x50})
            .put(".gif", new byte[]{(byte) 0x47, (byte) 0x49})
            .put(".bmp", new byte[]{(byte) 0x42, (byte) 0x4D})
            .put(".webp", new byte[]{(byte) 0x52, (byte) 0x49})
            .build();

    /**
     * 获取文件系统
     *
     * @return FileSystemView
     */
    public static FileSystemView getFileSysView() {
        return FileSystemView.getFileSystemView();
    }

    /**
     * 解码微信聊天图片文件
     *
     * @param file 待解析文件
     * @return 解析后的文件内容
     */
    public static byte[] decodeImgOfWechat(File file) {
        Optional<Map<String, String>> code = getCode(file);
        if (code.isPresent()) {
            Map.Entry<String, String> next = code.get().entrySet().iterator().next();
            try {
                byte[] tempBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                return xor(tempBytes, next.getValue());
            } catch (IOException e) {
                LOGGER.error("IOException: {0}", e.getMessage());
            }
        } else {
            LOGGER.error("not jpg,png,gif,webp,bmp!");
        }
        return new byte[0];
    }

    /**
     * 解码微信聊天图片文件到指定文件
     *
     * @param inFile    待解析的文件
     * @param outputDir 解析后的输出目录
     * @return 解析结果：是否成功
     */
    public static boolean decodeImgOfWechat(File inFile, String outputDir) {
        Optional<Map<String, String>> code = getCode(inFile);
        if (code.isPresent()) {
            Map.Entry<String, String> next = code.get().entrySet().iterator().next();
            String decodeFile = outputDir + File.separator + inFile.getName() + next.getKey();

            InputStream in = null;
            BufferedOutputStream out = null;
            try {
                in = new BufferedInputStream(new FileInputStream(inFile));
                out = new BufferedOutputStream(new FileOutputStream(decodeFile, true));
                byte[] tempBytes = new byte[in.available()];
                for (int i = 0; (i = in.read(tempBytes)) != -1; ) {
                    byte[] xorBytes = xor(tempBytes, next.getValue());
                    out.write(xorBytes, 0, i);
                }
                return true;
            } catch (FileNotFoundException e) {
                LOGGER.error("File not found: {0}", e.getMessage());
            } catch (IOException e) {
                LOGGER.error("IOException: {0}", e.getMessage());
            } finally {
                IoUtils.closeQuietly(in, out);
            }
        } else {
            LOGGER.error("not jpg,png,gif,webp,bmp!");
        }
        return false;
    }

    //异或算法： A ^ B ^ A = B
    private static Optional<Map<String, String>> getCode(File f) {
        byte[] readHeaders = loadFileHeader(f);

        Iterator<Map.Entry<String, byte[]>> iterator = STANDARD_OF_IMG_HEADER.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, byte[]> next = iterator.next();
            String extension = next.getKey();
            byte[] fileHeaders = next.getValue();

            int c = readHeaders[0] ^ fileHeaders[0];
            int idfCode = readHeaders[1] ^ c;
            if (idfCode == fileHeaders[1]) {
                Map<String, String> map = new HashMap<String, String>() {{
                    String code = Integer.toHexString(c);
                    put(extension, code);
                }};
                return Optional.of(map);
            }
        }
        return Optional.empty();
    }

    private static byte[] xor(byte[] tempBytes, String code) {
        byte[] result = new byte[tempBytes.length];
        for (int i = 0; i < tempBytes.length; i++) {
            result[i] = (byte) (tempBytes[i] ^ hexToByte(code));
        }
        return result;
    }

    private static byte[] loadFileHeader(File f) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(f);
            byte[] fileHeader = new byte[2];
            fileInputStream.read(fileHeader);
            fileInputStream.close();
//            printHexString(fileHeader);
            return fileHeader;
        } catch (IOException e) {
            LOGGER.error("File not found: {0}", e.getMessage());
        } finally {
            IoUtils.closeQuietly(fileInputStream);
        }
        return new byte[0];
    }

    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    //将指定byte数组以16进制的形式打印到控制台
    public static void printHexString(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
        }
    }


    /**
     * 等比缩放以适配父容器尺寸。如果新对象尺寸小于父容器，则直接返回对象尺寸
     *
     * @param parentWidth  父容器宽度
     * @param parentHeight 父容器高度
     * @param width        对象宽度
     * @param height       对象高度
     * @return 等比缩放后尺寸
     */
    public static Dimension getSizeWithAspectRatio(int parentWidth, int parentHeight, int width, int height) {
        //对象尺寸小于父容器
        if (width <= parentWidth && height <= parentHeight) {
            return new Dimension(width, height);
        } else {
            //横图或正方形图
            if (width >= height) {
                for (int i = width - 1; i > 0; i--) {
                    if (i <= parentWidth) {
                        double scaleRatio = width * 1.0 / i;
                        return new Dimension(i, (int) (height / scaleRatio));
                    }
                }
            } else {
                //竖图
                for (int i = height - 1; i > 0; i--) {
                    if (i <= parentHeight) {
                        double scaleRatio = height * 1.0 / i;
                        return new Dimension((int) (width / scaleRatio), i);
                    }
                }
            }
            return new Dimension(parentWidth, parentHeight);
        }
    }


    /**
     * 计算文件的MD5
     *
     * @param file 待计算的文件
     * @return 文件的MD5
     */
    public static Optional<String> getMd5OfFile(File file) {
        FileInputStream data = null;
        try {
            data = new FileInputStream(file);
            return Optional.of(DigestUtils.md5Hex(data));
        } catch (IOException e) {
            return Optional.empty();
        } finally {
            if (data != null) {
                IOUtils.closeQuietly(data);
            }
        }
    }

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
     *
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
     * 判断一个文件是否是文本文件【方法待测验】
     *
     * @param f 待判断的文件
     * @return 是否是文本文件
     */
    @Deprecated
    public static boolean isTextFile(File f) {
        Path path = FileSystems.getDefault().getPath(f.getParent(), f.getName());
        try {
            String mimeType = Files.probeContentType(path);
        } catch (IOException e) {
            LOGGER.error("File type detected exception [{}]", f.getAbsoluteFile());
        }
        return true;
    }

    /**
     * judge a file is image or not
     *
     * @param f the file need to check
     * @return is file or not
     */
    public static boolean isRealImg(File f) {
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
     * 通过扩展名判断文件是否是图片
     *
     * @param fileName 文件名或文件路径
     * @return 是否是图片
     */
    public static boolean isImgFromExtension(String fileName) {
        String lowerCaseName = fileName.toLowerCase(Locale.getDefault());
        String suffix = lowerCaseName.substring(lowerCaseName.lastIndexOf("."));
        return DKConstants.FILE_TYPE_IMG.contains(suffix);
    }

    /**
     * 通过扩展名判断文件是否是图片
     *
     * @param file 文件名
     * @return 是否是图片
     */
    public static boolean isImgFromExtension(File file) {
        return isImgFromExtension(file.getName());
    }

    /**
     * 从文件中过滤出图片文件
     *
     * @param files 待过滤的文件集
     * @return 过滤后的图片文件
     */
    public static List<File> filterImgsFromFiles(File[] files) {
        ArrayList<File> objects = new ArrayList<>();
        for (File file : files) {
            if (isImgFromExtension(file)) {
                objects.add(file);
            }
        }
        return objects;
    }

    /**
     * 过滤文件夹下的所有图片文件
     *
     * @param dirFile 待过滤的文件夹
     * @return 所有图片文件
     */
    public static List<File> filterImgsFromDir(File dirFile) {
        List<File> objects = new ArrayList<File>();
        if (dirFile.isDirectory()) {
            File[] files = dirFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return isImgFromExtension(name);
                }
            });
            for (File file : files) {
                objects.add(file);
            }
        }
        return objects;
    }

    /**
     * 过滤文件夹下的所有图片文件，并存放到指定的容器中去
     *
     * @param dirFile 待过滤的文件夹
     * @param objects 存放结果的容器
     */
    public static void filterImgsFromDir(File dirFile, List<File> objects) {
        if (objects != null) {
            if (dirFile.isDirectory()) {
                File[] files = dirFile.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return isImgFromExtension(name);
                    }
                });
                for (File file : files) {
                    objects.add(file);
                }
            }
        }
    }

    /**
     * 获取指定文件的元数据
     *
     * @param file 待获取元数据的文件
     * @return 文件元数据
     */
    public static Metadata getMetadataOfFile(File file) {
        try {
            return ImageMetadataReader.readMetadata(file);
        } catch (ImageProcessingException e) {
            LOGGER.error("Load meta data failed: " + file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Load meta data failed: " + file.getAbsolutePath());
        }
        return null;
    }

    /**
     * 用资源管理器打开系统目录
     *
     * @param filePath 待打开的目录
     * @return 打开成功与否
     */
    public static boolean openFolder(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isDirectory()) {
                return openFile(file);
            } else {
                return openFile(file.getParentFile());
            }
        } else {
            return false;
        }
    }

    /**
     * open a file
     *
     * @param filePath the file path need to be opened
     * @return opened success or not
     */
    public static boolean openFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return openFile(file);
        } else {
            return false;
        }
    }

    /**
     * open a file
     *
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
     * 输出指定字符串内容到指定文件
     *
     * @param file     待写入的文件
     * @param dataList 待写入的数据
     * @return 文件写入是否成功
     */
    public static boolean writeFile(File file, List<String> dataList) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            IOUtils.writeLines(dataList, null, writer);
            return true;
        } catch (IOException e) {
        } finally {
            IoUtils.closeQuietly(writer);
        }
        return false;
    }

    /**
     * empty folder
     *
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
     *
     * @param f the file need to open with system editor
     */
    public static void openTextFile(File f) {
        if (DKSysUtil.isWindows()) {
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
     *
     * @param bytes 原始long格式
     * @return 可读的格式
     */
    public static String formatBytes(long bytes) {
        String formatBytes = FormatUtil.formatBytes(bytes);
        if (DKSysUtil.isWindows()) {
            return formatBytes.replace("i", "");
        }
        return formatBytes;
    }


    /**
     * convert file unit to long byte
     *
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
     *
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
