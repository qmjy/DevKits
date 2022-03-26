/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray;

import cn.devkits.client.tray.listener.TrayItemWindowListener;
import cn.devkits.client.util.DKNetworkUtil;
import cn.devkits.client.util.DKSysUIUtil;
import cn.devkits.client.util.DKSysUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Optional;

public class MenuItemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuItemFactory.class);

    public static void createWindowItem(Menu parentItem, MenuItemEnum itemType) {
        MenuItem menuItem = null;
        switch (itemType) {
            case SERVER_PORT:
                menuItem = new MenuItem(DKSysUIUtil.getLocaleStringWithEllipsis("SERVER_PORT_DETECT"));
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.SERVER_PORT));
                break;
            case WIFI:
                menuItem = new MenuItem(DKSysUIUtil.getLocaleStringWithEllipsis("SSID_MANAGEMENT"));
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.WIFI));
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
                menuItem = new MenuItem(DKSysUIUtil.getLocaleStringWithColon("USER_NAME") + userName);
                menuItem.addActionListener(e -> {
                    DKSysUIUtil.setSystemClipboard(userName);
                });
                break;
            case PC_NAME:
                String pcName = System.getenv().get("COMPUTERNAME");
                menuItem = new MenuItem(DKSysUIUtil.getLocaleStringWithColon("PC_NAME") + pcName);
                menuItem.addActionListener(e -> {
                    DKSysUIUtil.setSystemClipboard(pcName);
                });
                break;
            case OS_NAME:
                menuItem = new MenuItem(DKSysUIUtil.getLocaleStringWithColon("OS") + DKSysUtil.getOsInfo());
                menuItem.addActionListener(e -> {
                    DKSysUIUtil.setSystemClipboard(DKSysUtil.getOsInfo());
                });
                break;
            case OS_ARCH:
                String osArch = System.getProperty("os.arch");

                menuItem = new MenuItem(DKSysUIUtil.getLocaleStringWithColon("OS_ARCH") + osArch);
                menuItem.addActionListener(e -> {
                    DKSysUIUtil.setSystemClipboard(osArch);
                });
                break;
            case CPU_INF:
                menuItem = new MenuItem("CPU: " + DKSysUtil.getCpuInfo());
                menuItem.addActionListener(e -> {
                    DKSysUIUtil.setSystemClipboard(DKSysUtil.getCpuInfo());
                });
                break;
            case CPU_ENDIAN:
                String endian = System.getProperty("sun.cpu.endian");

                menuItem = new MenuItem(DKSysUIUtil.getLocaleStringWithColon("CPU_ENDIAN") + endian);
                menuItem.addActionListener(e -> {
                    DKSysUIUtil.setSystemClipboard(endian);
                });
                break;
            case SCREEN_SIZE:
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                String screenLabel = (int) screenSize.getWidth() + "*" + (int) screenSize.getHeight() + "(" + Toolkit.getDefaultToolkit().getScreenResolution() + "dpi)";

                menuItem = new MenuItem(DKSysUIUtil.getLocaleStringWithColon("SCREEN_SIZE") + screenLabel);
                menuItem.addActionListener(e -> {
                    DKSysUIUtil.setSystemClipboard(screenLabel);
                });
                break;
            case IP:
                Optional<String> ip = DKNetworkUtil.getIp();
                if (ip.isPresent()) {
                    String internetIp = ip.get();
                    menuItem = new MenuItem(itemType.toString() + ": " + internetIp);
                    menuItem.addActionListener(e -> {
                        DKSysUIUtil.setSystemClipboard(internetIp);
                    });
                    break;
                }
                return;
            case MAC:
                Optional<String> macAddress = DKNetworkUtil.getMacAddress();
                if (macAddress.isPresent()) {
                    String mac = macAddress.get();

                    menuItem = new MenuItem(itemType.toString() + ": " + mac);
                    menuItem.addActionListener(e -> {
                        DKSysUIUtil.setSystemClipboard(mac);
                    });
                    break;
                }
                return;
            default:
                break;
        }
        parentItem.add(menuItem);
    }

    public static void createComputeItem(Menu parentItem, MenuItemEnum itemType) {
        MenuItem menuItem = null;
        switch (itemType) {

            case OS_INFO_MORE:
                menuItem = new MenuItem(DKSysUIUtil.getLocaleStringWithEllipsis("MORE_DETAILS"));
                menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.OS_INFO_MORE));
                break;
            default:
                break;
        }

        parentItem.add(menuItem);
    }
}
