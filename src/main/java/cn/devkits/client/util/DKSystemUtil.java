package cn.devkits.client.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
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
 * System Util
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年10月20日 下午9:37:03
 */
public final class DKSystemUtil {
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
        DKSystemUtil.invokeLocalApp("test.exe");
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
