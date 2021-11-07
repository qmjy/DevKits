/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.asyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lgooddatepicker.components.CalendarPanel;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import cn.devkits.client.DKConstants;
import cn.devkits.client.camera.WebCameraFrame;
import cn.devkits.client.tray.MenuItemEnum;
import cn.devkits.client.tray.MenuItemFactory;
import cn.devkits.client.tray.frame.AboutFrame;
import cn.devkits.client.tray.frame.NewTodoTaskFrame;
import cn.devkits.client.tray.frame.SettingsFrame;
import cn.devkits.client.tray.listener.TrayItemWindowListener;
import cn.devkits.client.util.DKSystemUIUtil;
import cn.devkits.client.util.DKSystemUtil;

import javax.swing.JDialog;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Container;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

public class AppStarter implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppStarter.class);
    private final TrayIcon trayIcon;
    private final String[] args;

    public AppStarter(TrayIcon trayIcon, String[] args) {
        this.trayIcon = trayIcon;
        this.args = args;
    }

    @Override
    public void run() {
        DKSystemUIUtil.regIcon();

        if (args.length > 0) {
            WebCameraFrame webCameraFrame = new WebCameraFrame();
            webCameraFrame.setVisible(true);
        } else {
            initSystemTrayIcon();
            initSystemHotKey();
        }
    }

    private void initSystemHotKey() {
        JIntellitype.getInstance().registerHotKey(DKConstants.DK_HOTKEY_SCR_CAP, JIntellitype.MOD_ALT + JIntellitype.MOD_CONTROL, (int) 'A');
        JIntellitype.getInstance().registerHotKey(DKConstants.DK_HOTKEY_NEW_TODO, JIntellitype.MOD_CONTROL, (int) 'T');

        JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
            @Override
            public void onHotKey(int identifier) {
                switch (identifier) {
                    case DKConstants.DK_HOTKEY_SCR_CAP:
                        DKSystemUtil.invokeLocalApp("QQSnapShot.exe");
                        break;
                    case DKConstants.DK_HOTKEY_NEW_TODO:
                        new NewTodoTaskFrame(null).setVisible(true);
                    default:
                        break;
                }
            }
        });
    }

    private void initSystemTrayIcon() {
        trayIcon.setPopupMenu(createTrayMenu(trayIcon));
        initDbClick(trayIcon);
    }

    private void initDbClick(TrayIcon trayIcon) {
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 判断是否双击了鼠标
                if (e.getClickCount() == 2) {
                    showCalendarDialog();
                }
            }
        });
    }

    private void showCalendarDialog() {
        Container calendarPane = createCalendarPane();

        JDialog jDialog = new JDialog();
        jDialog.setTitle(DKSystemUIUtil.getLocaleString("CALENDAR_DIALOG_TITLE"));
        jDialog.setResizable(false);
        jDialog.setContentPane(calendarPane);
        jDialog.pack();
        jDialog.setLocationRelativeTo(null);
        jDialog.setVisible(true);
    }

    private Container createCalendarPane() {
        DatePickerSettings datePickerSettings = new DatePickerSettings();
        datePickerSettings.setWeekNumbersDisplayed(true, true);

//        int newHeight = (int) (datePickerSettings.getSizeDatePanelMinimumHeight() * 1.6);
//        int newWidth = (int) (datePickerSettings.getSizeDatePanelMinimumWidth() * 1.6);
//        datePickerSettings.setSizeDatePanelMinimumHeight(newHeight);
//        datePickerSettings.setSizeDatePanelMinimumWidth(newWidth);

        CalendarPanel calendarPanel = new CalendarPanel(datePickerSettings);
        calendarPanel.setSelectedDate(LocalDate.now());
        calendarPanel.setBorder(new LineBorder(Color.lightGray));

        return calendarPanel;
    }


    private PopupMenu createTrayMenu(TrayIcon trayIcon) {
        PopupMenu popupMenu = new PopupMenu();

        popupMenu.add(initNetworkMenu());// 网络工具
        popupMenu.add(initDevMenu()); // 开发工具
        popupMenu.add(initComputerMenu());// 系统

        popupMenu.addSeparator();
        MenuItem settings = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("SETTINGS"));
        settings.addActionListener(e -> {
            new SettingsFrame().setVisible(true);
        });
        popupMenu.add(settings);

        MenuItem mi = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("ABOUT"));
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AboutFrame().setVisible(true);
            }
        });
        popupMenu.add(mi);
        popupMenu.addSeparator();
        MenuItem quit = new MenuItem(DKSystemUIUtil.getLocaleString("EXIT"));
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        popupMenu.add(quit);

        return popupMenu;
    }

    private MenuItem initNetworkMenu() {
        Menu networkMenu = new Menu(DKSystemUIUtil.getLocaleString("NETWORK_TOOLS"));
        MenuItemFactory.createWindowItem(networkMenu, MenuItemEnum.SERVER_PORT);
        return networkMenu;
    }

    private Menu initDevMenu() {
        Menu devMenu = new Menu(DKSystemUIUtil.getLocaleString("DEV_TOOLS"));

        MenuItem menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("CODEC"));
        menuItem.setEnabled(false);
        menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.CODEC));
        devMenu.add(menuItem);

        menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("CODE_FORMAT"));
        menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.CODE_FORMAT));
        devMenu.add(menuItem);

        menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("ENV_VAR"));
        menuItem.setEnabled(false);
        menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.CODE_FORMAT));
        devMenu.add(menuItem);

        return devMenu;
    }

    private Menu initComputerMenu() {
        Menu computerItem = new Menu(DKSystemUIUtil.getLocaleString("COMPUTER"), false);

        Menu sysInfoItem = new Menu(DKSystemUIUtil.getLocaleString("SYS_INFO"), false);

        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.USER_NAME);
        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.OS_NAME);
        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.OS_ARCH);
        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.CPU_INF);
        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.CPU_ENDIAN);
        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.SCREEN_SIZE);
        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.IP);
        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.MAC);
        MenuItemFactory.createComputeItem(sysInfoItem, MenuItemEnum.OS_INFO_MORE);

        computerItem.add(sysInfoItem);

        Menu toolsItem = new Menu(DKSystemUIUtil.getLocaleString("TOOLS"), false);

        MenuItem menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("WASTE_CLEAN"));
        menuItem.setEnabled(false);
        toolsItem.add(menuItem);

        menuItem = new MenuItem(DKSystemUIUtil.getLocaleString("SCREENSHOTS"));
        menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.SCRCAPTURE));
        toolsItem.add(menuItem);

        menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("DUPLICATE_FILES"));
        menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.LDF));
        toolsItem.add(menuItem);

        menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("TODO_LIST"));
        menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.TODOS));
        toolsItem.add(menuItem);

        menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("LOGON_IMAGE"));
        menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.LOGONUI));
        toolsItem.add(menuItem);

        menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("FILE_EXPLORERS"));
        menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.FILEEXPLORER));
        toolsItem.add(menuItem);

        menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("QR_CODE"));
        menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.QR));
        toolsItem.add(menuItem);

        menuItem = new MenuItem(DKSystemUIUtil.getLocaleString("HOSTS"));
        menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.HOSTS));
        toolsItem.add(menuItem);

        menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("FILE_SPLITER"));
        menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.FILESPLITER));
        toolsItem.add(menuItem);

        computerItem.add(toolsItem);
        return computerItem;
    }
}
