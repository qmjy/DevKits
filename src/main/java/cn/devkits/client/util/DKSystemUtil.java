/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.bridj.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import cn.devkits.client.tray.listener.TrayItemWindowListener;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

/**
 * System Util<br>
 * http://webcam-capture.sarxos.pl/
 *
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年10月20日 下午9:37:03
 */
public abstract class DKSystemUtil {

    /**
     * windows 安全命令
     */
    private static final Set<String> WIN_WHITE_LIST_CMDS = Sets.newHashSet(new String[]{"msinfo32", "dxdiag"});

    /**
     * 声音类型：扫描声音
     */
    public static final int SOUND_TYPE_SCAN = 1;

    private static final Logger LOGGER = LoggerFactory.getLogger(DKSystemUtil.class);
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();

    /**
     * default constructor
     */
    public DKSystemUtil() {
    }

    /**
     * 获取当前可用SSID列表
     *
     * @return 当前可用SSID列表
     */
    public abstract Map<String, Map<String, String>> getAvailableSsids();

    /**
     * 获取当前连接的wifi详情
     *
     * @return wifi 详情
     */
    public abstract Map<String, String> getCurrentSsid();

    /**
     * 获取当前连接的WIFI信息
     *
     * @return 当前连接的wifi详情
     */
    public abstract List<String> getSsidNamesOfConnected();

    /**
     * 获取指定wifi名称的密码
     *
     * @param wifiName wifi name
     * @return wifi 密码
     */
    public abstract String getPwdOfSsid(String wifiName);

    /**
     * 获取系统信息对象
     *
     * @return 系统信息对象
     */
    public static SystemInfo getSystemInfo() {
        return SYSTEM_INFO;
    }

    /**
     * 获取当前操作系统对应的系统助手
     *
     * @return 适合当前操作系统的助手接口
     */
    public static DKSystemUtil getCurrentSystemUtil() {
        if (isWindows()) {
            return new WinSystemUtil();
        }
        //TODO
        return null;
    }


    /**
     * 生成二维码
     *
     * @param text   二维码内容
     * @param width  二维码图片宽度
     * @param height 二维码图片高度
     * @return 二维码图片缓冲流
     */
    public static BufferedImage generateQrImg(String text, int width, int height) {
        HashMap hashMap = new HashMap();
        // 设置二维码字符编码
        hashMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 设置二维码纠错等级
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        // 设置二维码边距
        hashMap.put(EncodeHintType.MARGIN, 2);

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hashMap);
            MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig);
            return bufferedImage;
        } catch (WriterException e) {
            LOGGER.error("Generate QR file failed...");
        }
        return null;
    }


    /**
     * open local application
     *
     * @param appName the application name need to open
     */
    public static void invokeLocalApp(String appName) {
        if (appName == null || appName.trim().isEmpty()) {
            return;
        }

        if (Desktop.isDesktopSupported()) {
            try {
                if (!DKSystemUtil.isDevelopMode()) {
                    Desktop.getDesktop().open(new File("" + appName));
                } else {
                    String filePath = TrayItemWindowListener.class.getClassLoader().getResource("").getPath() + appName;
                    Desktop.getDesktop().open(new File(filePath));
                }
            } catch (IllegalArgumentException e) {
                LOGGER.error("Can't find the file '{}'", appName);
            } catch (IOException e) {
                LOGGER.error("Invoke file '{}' failed: {}", appName, e.getMessage());
            }
        }
    }


    /**
     * 获取程序运行根目录
     *
     * @return 文件运行更目录
     */
    public static String getRootFolder() {
        if (!DKSystemUtil.isDevelopMode()) {
            return new File("").getAbsolutePath();
        } else {
            return TrayItemWindowListener.class.getClassLoader().getResource("").getPath();
        }
    }


    /**
     * 判断当前程序是否是以jar的方式运行
     *
     * @return 是否是以jar运行
     */
    public static boolean isDevelopMode() {
        String protocol = DKSystemUtil.class.getResource("").getProtocol();
        return !"jar".equals(protocol);
    }

    /**
     * 搜索指定对象在数组的索引位置
     *
     * @param array 待搜索的数组
     * @param key   待搜索的对象
     * @return 索引
     */
    public static int arraysSearch(String[] array, String key) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(key)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * 锁屏
     */
    public static void lockScreen() {
        if (isWindows()) {
            try {
                Runtime.getRuntime().exec("RunDll32.exe user32.dll,LockWorkStation");
            } catch (IOException e) {
                LOGGER.error("Can't lock the system screen！");
            }
        } else {
            // TODO
            LOGGER.info("Can't implement the system screen lock yet！");
        }
    }


    /**
     * 获取屏幕宽度和高度
     *
     * @return 屏幕宽度和高度
     */
    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }


    /**
     * 播放执行类型的声音
     *
     * @param soundType sound type
     */
    public static void playSound(int soundType) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(DKSystemUtil.class.getClassLoader().getResourceAsStream("scan.wav"));
            clip.open(inputStream);
            clip.start();
        } catch (Exception e) {
            LOGGER.error("Can't play the sound file 'scan.wav'");
        }
    }

    /**
     * get system startup time
     *
     * @return Formats the up time in seconds as days, hh:mm:ss.
     */
    public static String getSystemUpTime() {
        long systemUptime = SYSTEM_INFO.getOperatingSystem().getSystemUptime();
        return FormatUtil.formatElapsedSecs(systemUptime);
    }


    public static String getSystemBootedTime(String country) {
        long systemBootTime = SYSTEM_INFO.getOperatingSystem().getSystemBootTime();
        return Instant.ofEpochSecond(systemBootTime).toString();
    }

    /**
     * get OS Name & Version
     *
     * @return OS name and Version
     */
    public static String getOsInfo() {
        OperatingSystem os = SYSTEM_INFO.getOperatingSystem();
        return String.valueOf(os);
    }

    /**
     * 获取系统临时文件目录<br>
     * Windows: "C:\Users\ADMINI~1\AppData\Local\Temp\"
     *
     * @return 临时文件目录
     */
    public static String getSystemTempDir() {
        return System.getProperty("java.io.tmpdir");
    }


    /**
     * 获取操作系统名称
     *
     * @return 操作系统名称
     */
    public static String getOsName() {
        return System.getProperty("os.name");
    }


    /**
     * 判断当前操作系统是否是windows
     *
     * @return 当前系统是否是windows
     */
    public static boolean isWindows() {
        // return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
        return Platform.isWindows();
    }


    public static boolean isWindow7() {
        return Platform.isWindows7();
    }

    /**
     * get CPU process info.
     *
     * @return CPU information
     */
    public static String getCpuInfo() {
        CentralProcessor processor = SYSTEM_INFO.getHardware().getProcessor();
        return processor.getProcessorIdentifier().getName();
    }

    public static void sleep(int i) {
        try {
            TimeUnit.MILLISECONDS.sleep(i);
        } catch (InterruptedException e) {
            LOGGER.error("System sleep Failed: {}", i);
        }
    }

    public static boolean openSystemInfoClient(String cmd) {
        if (isWindows() && WIN_WHITE_LIST_CMDS.contains(cmd)) {
            try {
                Runtime.getRuntime().exec(cmd);
                return true;
            } catch (IOException e) {
                LOGGER.error("Open windows cmd ‘{}’ failed!", cmd);
            }
        }
        return false;
    }

    /**
     * https://github.com/oshi/oshi/blob/master/oshi-demo/src/main/java/oshi/demo/ComputerID.java
     * Generates a Computer Identifier, which may be part of a strategy to construct
     * a licence key. (The identifier may not be unique as in one case hashcode
     * could be same for multiple values, and the result may differ based on whether
     * the program is running with sudo/root permission.) The identifier string is
     * based upon the processor serial number, vendor, processor identifier, and
     * total processor count.
     *
     * @return A string containing four hyphen-delimited fields representing the
     * processor; the first 3 are 32-bit hexadecimal values and the last one
     * is an integer value.
     */
    public static String getComputerIdentifier() {
        OperatingSystem operatingSystem = SYSTEM_INFO.getOperatingSystem();
        HardwareAbstractionLayer hardwareAbstractionLayer = SYSTEM_INFO.getHardware();
        CentralProcessor centralProcessor = hardwareAbstractionLayer.getProcessor();
        ComputerSystem computerSystem = hardwareAbstractionLayer.getComputerSystem();

        String vendor = operatingSystem.getManufacturer();
        String processorSerialNumber = computerSystem.getSerialNumber();
        String uuid = computerSystem.getHardwareUUID();
        String processorIdentifier = centralProcessor.getProcessorIdentifier().getIdentifier();
        int processors = centralProcessor.getLogicalProcessorCount();

        String delimiter = "-";

        return String.format("%08x", vendor.hashCode()) + delimiter
                + String.format("%08x", processorSerialNumber.hashCode()) + delimiter
                + String.format("%08x", uuid.hashCode()) + delimiter
                + String.format("%08x", processorIdentifier.hashCode()) + delimiter + processors;
    }
}
