package cn.devkits.client;

import java.awt.AWTException;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.devkits.client.task.WinNoticeTask;
import cn.devkits.client.tray.MenuItemEnum;
import cn.devkits.client.tray.MenuItemFactory;

/**
 * theme:http://www.javasoft.de/synthetica/screenshots/plain/
 * @author www.yudeshui.club
 * @datetime 2019年8月14日 下午11:59:05
 */
public class App
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args)
    {
        if (SystemTray.isSupported())
        {
            try
            {
                TrayIcon trayIcon = new TrayIcon(ImageIO.read(App.class.getClassLoader().getResource("20.png")));
                trayIcon.setImageAutoSize(true);
                // 添加工具提示文本
                trayIcon.setToolTip("开发工具包\r\n官网：www.devkits.cn");

                initContextMenu(trayIcon);
                initDbClick(trayIcon);
                initNotice(trayIcon);

                SystemTray.getSystemTray().add(trayIcon);
                logger.info("初始化托盘功能成功！");
            } catch (AWTException e)
            {
                logger.error("初始化托盘功能失败！");
            } catch (IOException e)
            {
                logger.error("托盘图标加载失败！");
            }
        } else
        {
            logger.error("系统不支持托盘菜单！");
        }
    }

    private static void initContextMenu(TrayIcon trayIcon)
    {
        PopupMenu popupMenu = new PopupMenu();

        Menu networkMenu = new Menu("Network Tools");
        MenuItemFactory.createWindowItem(networkMenu, MenuItemEnum.SERVER_PORT);
        popupMenu.add(networkMenu);

        Menu devMenu = new Menu("Development Tools");

        // MenuItemFactory.createComputeItem(devMenu, MenuItemEnum.MD5);
        MenuItemFactory.createComputeItem(devMenu, MenuItemEnum.CODE_FORMAT);

        Menu myComputerItem = new Menu("Computer", true);

        MenuItemFactory.createClipboardItem(myComputerItem, MenuItemEnum.USER_NAME);
        MenuItemFactory.createClipboardItem(myComputerItem, MenuItemEnum.OS_NAME);
        MenuItemFactory.createClipboardItem(myComputerItem, MenuItemEnum.OS_ARCH);
        MenuItemFactory.createClipboardItem(myComputerItem, MenuItemEnum.CPU_ENDIAN);
        MenuItemFactory.createClipboardItem(myComputerItem, MenuItemEnum.SCREEN_SIZE);
        MenuItemFactory.createClipboardItem(myComputerItem, MenuItemEnum.IP);
        MenuItemFactory.createClipboardItem(myComputerItem, MenuItemEnum.MAC);

        popupMenu.add(devMenu);
        popupMenu.add(myComputerItem);

        popupMenu.addSeparator();
        popupMenu.add("About...");
        popupMenu.addSeparator();
        MenuItem quit = new MenuItem("Exit");
        quit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        popupMenu.add(quit);

        // 为托盘图标加弹出菜弹
        trayIcon.setPopupMenu(popupMenu);
    }

    private static void initDbClick(TrayIcon trayIcon)
    {
        trayIcon.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                // 判断是否双击了鼠标
                if (e.getClickCount() == 2)
                {
                    JOptionPane.showMessageDialog(null, "待开放");
                }
            }
        });
    }

    private static void initNotice(TrayIcon trayIcon)
    {
        Timer timer = new Timer();
        long time = 1000 * 60 * 30;// 半小时执行一次
        timer.scheduleAtFixedRate(new WinNoticeTask(trayIcon), time, time);
    }
}
