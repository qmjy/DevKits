package cn.devkits.client;

import java.awt.AWTException;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Timer;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import cn.devkits.client.task.ClipboardTask;
import cn.devkits.client.task.WinNoticeTask;
import cn.devkits.client.tray.MenuItemEnum;
import cn.devkits.client.tray.MenuItemFactory;
import cn.devkits.client.tray.frame.AboutFrame;
import cn.devkits.client.util.DKSystemUtil;


/**
 * 
 * Development Kits
 * 
 * @author fengshao
 * @version 1.0.0
 * @datetime 2019年9月5日 下午10:51:07
 */
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initLookAndFeel();
                initSystemHotKey();
                initSystemTrayIcon();
                registryAsynTask();
            }
        });
    }

    private static void registryAsynTask() {
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Timer timer = new Timer();
        long time = 1000;// 一秒钟执行一次
        timer.scheduleAtFixedRate(new ClipboardTask(sysClip), time, time);
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
                trayIcon.setToolTip("开发工具包\r\n官网：www.devkits.cn");

                initContextMenu(trayIcon);
                initDbClick(trayIcon);
                initNotice(trayIcon);

                SystemTray.getSystemTray().add(trayIcon);
                LOGGER.info("初始化托盘功能成功！");
            } catch (AWTException e) {
                LOGGER.error("初始化托盘功能失败！");
            } catch (IOException e) {
                LOGGER.error("托盘图标加载失败！");
            }
        } else {
            LOGGER.error("系统不支持托盘菜单！");
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

    private static void initContextMenu(TrayIcon trayIcon) {
        PopupMenu popupMenu = new PopupMenu();

        popupMenu.add(initNetworkMenu());// 网络工具
        popupMenu.add(initDevMenu()); // 开发工具
        popupMenu.add(initComputerMenu());// 系统

        popupMenu.addSeparator();
        popupMenu.add("Settings...");

        MenuItem mi = new MenuItem("About...");
        mi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new AboutFrame().setVisible(true);;
            }
        });
        popupMenu.add(mi);
        popupMenu.addSeparator();
        MenuItem quit = new MenuItem("Exit");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        popupMenu.add(quit);

        // 为托盘图标加弹出菜弹
        trayIcon.setPopupMenu(popupMenu);
    }

    private static MenuItem initNetworkMenu() {
        Menu networkMenu = new Menu("Network Tools");
        MenuItemFactory.createWindowItem(networkMenu, MenuItemEnum.SERVER_PORT);
        return networkMenu;
    }

    private static Menu initDevMenu() {
        Menu devMenu = new Menu("Development Tools");

        MenuItemFactory.createComputeItem(devMenu, MenuItemEnum.CODEC);
        MenuItemFactory.createComputeItem(devMenu, MenuItemEnum.CODE_FORMAT);
        MenuItemFactory.createComputeItem(devMenu, MenuItemEnum.ENV);

        return devMenu;
    }

    private static Menu initComputerMenu() {
        Menu computerItem = new Menu("Computer", true);

        Menu sysInfoItem = new Menu("System Information", true);

        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.USER_NAME);
        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.OS_NAME);
        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.OS_ARCH);
        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.CPU_INF);
        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.CPU_ENDIAN);
        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.SCREEN_SIZE);
        MenuItemFactory.createClipboardItem(sysInfoItem, MenuItemEnum.IP);
        MenuItemFactory.createComputeItem(sysInfoItem, MenuItemEnum.OS_INFO_MORE);

        computerItem.add(sysInfoItem);

        Menu toolsItem = new Menu("Tools", true);
        MenuItemFactory.createComputeItem(toolsItem, MenuItemEnum.CLEAN);
        MenuItemFactory.createComputeItem(toolsItem, MenuItemEnum.SCRCAPTURE);
        MenuItemFactory.createComputeItem(toolsItem, MenuItemEnum.LDF);
        MenuItemFactory.createComputeItem(toolsItem, MenuItemEnum.TODOS);
        MenuItemFactory.createComputeItem(toolsItem, MenuItemEnum.LOGONUI);
        MenuItemFactory.createComputeItem(toolsItem, MenuItemEnum.FILEEXPLORER);
        computerItem.add(toolsItem);

        return computerItem;
    }

    private static void initDbClick(TrayIcon trayIcon) {
        trayIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // 判断是否双击了鼠标
                if (e.getClickCount() == 2) {
                    JOptionPane.showMessageDialog(null, "待开放");
                }
            }
        });
    }

    private static void initNotice(TrayIcon trayIcon) {
        Timer timer = new Timer();
        long time = 1000 * 60 * 30;// 半小时执行一次
        timer.scheduleAtFixedRate(new WinNoticeTask(trayIcon), time, time);
    }
}
