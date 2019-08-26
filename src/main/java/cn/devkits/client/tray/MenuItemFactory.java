package cn.devkits.client.tray;

import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.Toolkit;

import cn.devkits.client.tray.listener.TrayItemClipboardListener;
import cn.devkits.client.tray.listener.TrayItemWindowListener;
import cn.devkits.client.util.DKNetworkUtil;

public class MenuItemFactory
{
    public static void createWindowItem(Menu parentItem, MenuItemEnum itemType)
    {
        MenuItem menuItem = null;
        switch (itemType)
        {
            case SERVER_PORT:
                menuItem = new MenuItem("Server Ports Detect");
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.SERVER_PORT));
                break;

            default:
                break;
        }
        parentItem.add(menuItem);
    }

    /**
     * 创建剪贴板菜单，单机该菜单以后，该菜单标识的关键信息就会被复制到剪贴板
     * @param parentItem 父菜单
     * @param itemType 当前待创建菜单枚举
     */
    public static void createClipboardItem(Menu parentItem, MenuItemEnum itemType)
    {
        MenuItem menuItem = null;
        switch (itemType)
        {
            case USER_NAME:
                String userName = System.getProperty("user.name");

                menuItem = new MenuItem("User Name: " + userName);
                menuItem.addActionListener(new TrayItemClipboardListener(userName));
                break;
            case OS_NAME:
                String osName = System.getProperty("os.name") + " (" + System.getProperty("os.version") + ")";

                menuItem = new MenuItem("OS Name: " + osName);
                menuItem.addActionListener(new TrayItemClipboardListener(osName));
                break;
            case OS_ARCH:
                String osArch = System.getProperty("os.arch");

                menuItem = new MenuItem("OS ARCH: " + osArch);
                menuItem.addActionListener(new TrayItemClipboardListener(osArch));
                break;
            case CPU_ENDIAN:
                String endian = System.getProperty("sun.cpu.endian");

                menuItem = new MenuItem("CPU Endian: " + endian);
                menuItem.addActionListener(new TrayItemClipboardListener(endian));
                break;
            case SCREEN_SIZE:
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                String screenLabel = (int) screenSize.getWidth() + "*" + (int) screenSize.getHeight() + "(" + Toolkit.getDefaultToolkit().getScreenResolution() + "dpi)";

                menuItem = new MenuItem("Screen Size: " + screenLabel);
                menuItem.addActionListener(new TrayItemClipboardListener(screenLabel));
                break;
            case IP:
                String internetIp = DKNetworkUtil.getInternetIp();

                menuItem = new MenuItem(itemType.toString() + ": " + internetIp);
                menuItem.addActionListener(new TrayItemClipboardListener(internetIp));
                break;
            case MAC:
                String mac = DKNetworkUtil.getMac();

                menuItem = new MenuItem(itemType.toString() + ": " + mac);
                menuItem.addActionListener(new TrayItemClipboardListener(mac));
                break;

            default:
                break;
        }
        parentItem.add(menuItem);
    }

    public static void createComputeItem(Menu parentItem, MenuItemEnum itemType)
    {
        MenuItem menuItem = null;
        switch (itemType)
        {
            case CODEC:
                menuItem = new MenuItem("Codec");
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.CODEC));
                break;
            case CODE_FORMAT:
                menuItem = new MenuItem("Code Format");
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.CODE_FORMAT));
                break;

            default:
                break;
        }
        parentItem.add(menuItem);
    }

}
