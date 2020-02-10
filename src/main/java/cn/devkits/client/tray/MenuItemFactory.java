package cn.devkits.client.tray;

import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.Toolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.devkits.client.tray.listener.TrayItemClipboardListener;
import cn.devkits.client.tray.listener.TrayItemWindowListener;
import cn.devkits.client.util.DKNetworkUtil;
import cn.devkits.client.util.DKSystemUtil;

public class MenuItemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuItemFactory.class);

    public static void createWindowItem(Menu parentItem, MenuItemEnum itemType) {
        MenuItem menuItem = null;
        switch (itemType) {
            case SERVER_PORT:
                menuItem = new MenuItem("Server Ports Detect...");
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
     * @param itemType 当前待创建菜单枚举
     */
    public static void createClipboardItem(Menu parentItem, MenuItemEnum itemType) {
        MenuItem menuItem = null;
        switch (itemType) {
            case USER_NAME:
                String userName = System.getProperty("user.name");

                menuItem = new MenuItem("User Name: " + userName);
                menuItem.addActionListener(new TrayItemClipboardListener(userName));
                break;
            case OS_NAME:

                menuItem = new MenuItem("OS: " + DKSystemUtil.getOsInfo());
                menuItem.addActionListener(new TrayItemClipboardListener(DKSystemUtil.getOsInfo()));
                break;
            case OS_ARCH:
                String osArch = System.getProperty("os.arch");

                menuItem = new MenuItem("OS ARCH: " + osArch);
                menuItem.addActionListener(new TrayItemClipboardListener(osArch));
                break;
            case CPU_INF:
                menuItem = new MenuItem("CPU: " + DKSystemUtil.getCpuInfo());
                menuItem.addActionListener(new TrayItemClipboardListener(DKSystemUtil.getCpuInfo()));
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

            default:
                break;
        }
        parentItem.add(menuItem);
    }

    public static void createComputeItem(Menu parentItem, MenuItemEnum itemType) {
        MenuItem menuItem = null;
        switch (itemType) {
            case CODEC:
                menuItem = new MenuItem("Codec...");
                menuItem.setEnabled(false);
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.CODEC));
                break;
            case CODE_FORMAT:
                menuItem = new MenuItem("Code Format...");
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.CODE_FORMAT));
                break;
            case ENV:
                menuItem = new MenuItem("Environment Variable...");
                menuItem.setEnabled(false);
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.CODE_FORMAT));
                break;
            case SCRCAPTURE:
                menuItem = new MenuItem("Screenshots");
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.SCRCAPTURE));
                break;
            case CLEAN:
                menuItem = new MenuItem("Waste Cleaning...");
                menuItem.setEnabled(false);
                break;
            case LDF:
                menuItem = new MenuItem("Large Duplicate Files...");
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.LDF));
                break;
            case TODOS:
                menuItem = new MenuItem("To-do List...");
                menuItem.setEnabled(false);
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.TODOS));
                break;
            case LOGONUI:
                menuItem = new MenuItem("Logon Image...");
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.LOGONUI));
                break;
            case FILEEXPLORER:
                menuItem = new MenuItem("File Explorers...");
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.FILEEXPLORER));
                break;
            case OS_INFO_MORE:
                menuItem = new MenuItem("More Details...");
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.OS_INFO_MORE));
                break;
            case QR:
                menuItem = new MenuItem("QR Code...");
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.QR));
                break;
            case HOSTS:
                menuItem = new MenuItem("Hosts");
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.HOSTS));
                break;  
            case FILESPLITER:
                menuItem = new MenuItem("File Spliter");
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.FILESPLITER));
                break;  
            default:
                break;
        }

        parentItem.add(menuItem);
    }
}
