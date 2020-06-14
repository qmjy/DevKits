package cn.devkits.client.asyn;

import cn.devkits.client.App;
import cn.devkits.client.DKConstant;
import cn.devkits.client.task.WinNoticeTask;
import cn.devkits.client.tray.MenuItemEnum;
import cn.devkits.client.tray.MenuItemFactory;
import cn.devkits.client.tray.frame.AboutFrame;
import cn.devkits.client.tray.listener.TrayItemWindowListener;
import cn.devkits.client.util.DKSystemUIUtil;
import cn.devkits.client.util.DKSystemUtil;
import com.github.lgooddatepicker.components.CalendarPanel;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Timer;

public class AppStarter implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    @Override
    public void run() {
        initLookAndFeel();
        initSystemTrayIcon();

        initSystemHotKey();
        DKSystemUIUtil.regIcon();
    }

    private static void initSystemHotKey() {
        JIntellitype.getInstance().registerHotKey(DKConstant.DK_HOTKEY_SCR_CAP, JIntellitype.MOD_ALT + JIntellitype.MOD_CONTROL, (int) 'A');

        JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
            @Override
            public void onHotKey(int identifier) {
                switch (identifier) {
                    case DKConstant.DK_HOTKEY_SCR_CAP:
                        DKSystemUtil.invokeLocalApp("QQSnapShot.exe");
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private static void initSystemTrayIcon() {
        if (SystemTray.isSupported()) {
            try {
                TrayIcon trayIcon = new TrayIcon(ImageIO.read(App.class.getClassLoader().getResource("logo.png")));
                trayIcon.setImageAutoSize(true);
                // 添加工具提示文本
                trayIcon.setToolTip("开发工具包" + System.lineSeparator() + "官网：www.devkits.cn");
                trayIcon.setPopupMenu(createTrayMenu(trayIcon));

                initDbClick(trayIcon);
                initNotice(trayIcon);

                SystemTray.getSystemTray().add(trayIcon);
                trayIcon.displayMessage("感谢您的使用", "简单高效是我的责任...", MessageType.INFO);
                LOGGER.info("Init system tray success!");
            } catch (AWTException e) {
                LOGGER.error("Init System tray function failed!");
            } catch (IOException e) {
                LOGGER.error("Load system tray icon failed!");
            }
        } else {
            LOGGER.error("This system can not support tray function！");
        }
    }

    /**
     * more look and feel:<br>
     * 1.http://www.javasoft.de/synthetica/screenshots/plain/ <br>
     * 2.https://www.cnblogs.com/clarino/p/8668160.html
     */
    private static void initLookAndFeel() {

        // UIManager.getSystemLookAndFeelClassName() get system defualt;
        String lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e1) {
            LOGGER.error("Init Look And Feel error:" + e1.getMessage());
        } catch (UnsupportedLookAndFeelException e) {
            LOGGER.error("UnsupportedLookAndFeelException:" + e.getMessage());
        }
    }

    private static PopupMenu createTrayMenu(TrayIcon trayIcon) {
        PopupMenu popupMenu = new PopupMenu();

        popupMenu.add(initNetworkMenu());// 网络工具
        popupMenu.add(initDevMenu()); // 开发工具
        popupMenu.add(initComputerMenu());// 系统

        popupMenu.addSeparator();
        popupMenu.add(DKSystemUIUtil.getLocaleStringWithEllipsis("SETTINGS"));

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

    private static MenuItem initNetworkMenu() {
        Menu networkMenu = new Menu(DKSystemUIUtil.getLocaleString("NETWORK_TOOLS"));
        MenuItemFactory.createWindowItem(networkMenu, MenuItemEnum.SERVER_PORT);
        return networkMenu;
    }

    private static Menu initDevMenu() {
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

    private static Menu initComputerMenu() {
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

        menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("LARGE_DUPLICATE_FILES"));
        menuItem.addActionListener(new TrayItemWindowListener(MenuItemEnum.LDF));
        toolsItem.add(menuItem);

        menuItem = new MenuItem(DKSystemUIUtil.getLocaleStringWithEllipsis("TODO_LIST"));
        menuItem.setEnabled(false);
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

    private static void initDbClick(TrayIcon trayIcon) {
        trayIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // 判断是否双击了鼠标
                if (e.getClickCount() == 2) {
                    showCalendarDialog();
                }
            }
        });
    }

    private static void showCalendarDialog() {
        Container calendarPane = createCalendarPane();

        JDialog jDialog = new JDialog();
        jDialog.setTitle(DKSystemUIUtil.getLocaleString("CALENDAR_DIALOG_TITLE"));
        jDialog.setResizable(false);
        jDialog.setContentPane(calendarPane);
        jDialog.pack();
        jDialog.setLocationRelativeTo(null);
        jDialog.setVisible(true);
    }

    private static Container createCalendarPane() {
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

    private static void initNotice(TrayIcon trayIcon) {
        Timer timer = new Timer();
        long time = 1000 * 60 * 30;// 半小时执行一次
        timer.scheduleAtFixedRate(new WinNoticeTask(trayIcon), time, time);
    }
}
