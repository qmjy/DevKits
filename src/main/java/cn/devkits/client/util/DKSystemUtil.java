package cn.devkits.client.util;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.devkits.client.tray.listener.TrayItemWindowListener;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.platform.windows.WindowsCentralProcessor;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

/**
 * 
 * System Util<br>
 * http://webcam-capture.sarxos.pl/
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年10月20日 下午9:37:03
 */
public final class DKSystemUtil {

    /**
     * 声音类型：扫描声音
     */
    public static final int SOUND_TYPE_SCAN = 1;

    private static final Logger LOGGER = LoggerFactory.getLogger(DKSystemUtil.class);
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();

    private DKSystemUtil() {}

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
                if (DKSystemUtil.isRunWithJar()) {
                    Desktop.getDesktop().open(new File("./" + appName));
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

    public static void main(String[] args) {
        System.out.println(DKSystemUtil.getSystemTempDir());
    }


    /**
     * 判断当前程序是否是以jar的方式运行
     * 
     * @return 是否是以jar运行
     */
    public static boolean isRunWithJar() {
        String protocol = DKSystemUtil.class.getResource("").getProtocol();
        return "jar".equals(protocol);
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
     * @return 操作系统名称
     */
    public static String getOsName() {
        return System.getProperty("os.name");
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
     * get CPU process info.
     * 
     * @return CPU information
     */
    public static String getCpuInfo() {
        CentralProcessor processor = SYSTEM_INFO.getHardware().getProcessor();
        if (processor instanceof WindowsCentralProcessor) {
            WindowsCentralProcessor winPro = (WindowsCentralProcessor) processor;
            return winPro.getName();
        }
        return processor.toString();
    }
}
