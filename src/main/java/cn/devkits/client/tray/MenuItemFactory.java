package cn.devkits.client.tray;

import cn.devkits.client.tray.listener.TrayItemWindowListener;
import cn.devkits.client.util.DKNetworkUtil;
import cn.devkits.client.util.DKSystemUIUtil;
import cn.devkits.client.util.DKSystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class MenuItemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuItemFactory.class);

    public static void createWindowItem(Menu parentItem, MenuItemEnum itemType) {
        MenuItem menuItem = null;
        switch (itemType) {
            case SERVER_PORT:
                menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("SERVER_PORT_DETECT"));
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.SERVER_PORT));
                break;

            default:
                break;
        }
        parentItem.add(menuItem);
    }

    /**
     * 创建剪贴板菜单，单机该菜单以后，该菜单标识的关键信息就会被复制到剪贴板
     *
     * @param parentItem 父菜单
     * @param itemType   当前待创建菜单枚举
     */
    public static void createClipboardItem(Menu parentItem, MenuItemEnum itemType) {
        MenuItem menuItem = null;
        switch (itemType) {
            case USER_NAME:
                String userName = System.getProperty("user.name");
                menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithColon("USER_NAME") + userName);
                menuItem.addActionListener(e -> {
                    DKSystemUIUtil.setSystemClipboard(userName);
                });
                break;
            case OS_NAME:
                menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithColon("OS") + DKSystemUtil.getOsInfo());
                menuItem.addActionListener(e -> {
                    DKSystemUIUtil.setSystemClipboard(DKSystemUtil.getOsInfo());
                });
                break;
            case OS_ARCH:
                String osArch = System.getProperty("os.arch");

                menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithColon("OS_ARCH") + osArch);
                menuItem.addActionListener(e -> {
                    DKSystemUIUtil.setSystemClipboard(osArch);
                });
                break;
            case CPU_INF:
                menuItem = new MenuItem("CPU: " + DKSystemUtil.getCpuInfo());
                menuItem.addActionListener(e -> {
                    DKSystemUIUtil.setSystemClipboard(DKSystemUtil.getCpuInfo());
                });
                break;
            case CPU_ENDIAN:
                String endian = System.getProperty("sun.cpu.endian");

                menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithColon("CPU_ENDIAN") + endian);
                menuItem.addActionListener(e -> {
                    DKSystemUIUtil.setSystemClipboard(endian);
                });
                break;
            case SCREEN_SIZE:
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                String screenLabel = (int) screenSize.getWidth() + "*" + (int) screenSize.getHeight() + "(" + Toolkit.getDefaultToolkit().getScreenResolution() + "dpi)";

                menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithColon("SCREEN_SIZE") + screenLabel);
                menuItem.addActionListener(e -> {
                    DKSystemUIUtil.setSystemClipboard(screenLabel);
                });
                break;
            case IP:
                String internetIp = DKNetworkUtil.getIp().get();

                menuItem = new MenuItem(itemType.toString() + ": " + internetIp);
                menuItem.addActionListener(e -> {
                    DKSystemUIUtil.setSystemClipboard(internetIp);
                });
                break;
            case MAC:
                String mac = DKNetworkUtil.getMacAddress().get();

                menuItem = new MenuItem(itemType.toString() + ": " + mac);
                menuItem.addActionListener(e -> {
                    DKSystemUIUtil.setSystemClipboard(mac);
                });
                break;
            default:
                break;
        }
        parentItem.add(menuItem);
    }

    public static void createComputeItem(Menu parentItem, MenuItemEnum itemType) {
        MenuItem menuItem = null;
        switch (itemType) {

            case OS_INFO_MORE:
                menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("MORE_DETAILS"));
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.OS_INFO_MORE));
                break;
            default:
                break;
        }

        parentItem.add(menuItem);
    }
}
