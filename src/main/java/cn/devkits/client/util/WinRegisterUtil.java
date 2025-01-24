package cn.devkits.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

/**
 * <p>
 * Windows 操作系统注册表管理工具类
 * 相关实现参考：<a href="https://blog.csdn.net/yang382197207/article/details/80079052">Windows特定文件添加右键菜单</a>、<a href="https://blog.csdn.net/amdd9582/article/details/83025287">注册表添加桌面右键二级菜单</a>
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2021/11/13
 */
public final class WinRegisterUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(WinRegisterUtil.class);
    private static final WinRegisterUtil INSTANCE = new WinRegisterUtil();

    private static final String SHELL_BASE_PATH = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CommandStore\\shell";
    private static final String SHELL_COMMAND_DK_QR = "Devkits.QR";
    private static final String SHELL_COMMAND_DK_QR_CMD = "Devkits.QR\\command";
    private static final String SHELL_COMMAND_DK_BAR_CMD = "Devkits.BAR\\command";

    private WinRegisterUtil() {
    }

    public static WinRegisterUtil getInstance() {
        return INSTANCE;
    }


    public void regQrCodeSubCommand() {
        if (createRegistryItem(WinReg.HKEY_LOCAL_MACHINE, SHELL_BASE_PATH, SHELL_COMMAND_DK_QR_CMD)) {
            Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, SHELL_BASE_PATH + "\\" + SHELL_COMMAND_DK_QR, "", "二维码识别");

            String rootFolder = DKSysUtil.getRootFolder();
            String version = DKConfigUtil.getInstance().getVersion();
            String javaCmd = "java -jar " + rootFolder + "\\devkits-" + version + ".jar -decode --qr %1";
            Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, SHELL_BASE_PATH + "\\" + SHELL_COMMAND_DK_QR_CMD, "", javaCmd);
        } else {
            LOGGER.error("Create registry sub item failed of {}", "Devkits.QR");
        }
    }

    private void regBarCodeSubCommand() {
        boolean result = createRegistryItem(WinReg.HKEY_LOCAL_MACHINE, SHELL_BASE_PATH, SHELL_COMMAND_DK_BAR_CMD);
        if (result) {
            System.out.println();
        } else {
            LOGGER.error("Create registry sub item failed of {}", "Devkits.BAR");
        }
    }

    /**
     * 创建注册表项
     *
     * @param root       注册表ROOT, WinReg#HKEY
     * @param parentPath 注册表中的已经存在的路径地址
     * @param itemName   新增注册表“项”的名称
     * @return 创建是否成功
     */
    public boolean createRegistryItem(WinReg.HKEY root, String parentPath, String itemName) {
        return Advapi32Util.registryCreateKey(root, parentPath, itemName);
    }
}
