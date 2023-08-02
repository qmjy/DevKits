/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.eventbus.EventBus;
import cn.devkits.client.async.AppStarter;
import cn.devkits.client.util.DKSysUIUtil;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;


/**
 * Development Kits
 *
 * @author Fengshao Liu
 * @version 1.0.0
 * @datetime 2019年9月5日 下午10:51:07
 */
public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static AnnotationConfigApplicationContext context;
    private static EventBus eventBus = new EventBus("DevkitsEvents");
    private static TrayIcon trayIcon;

    public static void main(String[] args) {
        initLookAndFeel();
        context = new AnnotationConfigApplicationContext(AppSpringContext.class);
        if (SystemTray.isSupported()) {
            trayIcon = createTrayIcon();
            SwingUtilities.invokeLater(new AppStarter(trayIcon, args));
        } else {
            LOGGER.error("This system can not support tray function！");
        }
    }


    private static TrayIcon createTrayIcon() {
        try {
            TrayIcon trayIcon = new TrayIcon(ImageIO.read(App.class.getClassLoader().getResource("logo.png")));
            trayIcon.setImageAutoSize(true);
            // 添加工具提示文本
            trayIcon.setToolTip("开发工具包" + System.lineSeparator() + "官网：www.devkits.cn");

            SystemTray.getSystemTray().add(trayIcon);
            trayIcon.displayMessage("感谢您的使用", "简单高效是我的责任...", TrayIcon.MessageType.INFO);
            LOGGER.info("Init system tray success!");
            return trayIcon;
        } catch (AWTException e) {
            LOGGER.error("Init System tray function failed!");
        } catch (IOException e) {
            LOGGER.error("Load system tray icon failed!");
        }
        return null;
    }

    /**
     * more look and feel:<br>
     * 1. http://www.javasoft.de/synthetica/screenshots/plain/ <br>
     * 2. https://www.cnblogs.com/clarino/p/8668160.html
     */
    private static void initLookAndFeel() {
        // UIManager.getSystemLookAndFeelClassName() get system defualt;
        String lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        DKSysUIUtil.setLookAndFeel(lookAndFeel);
    }

    public static AnnotationConfigApplicationContext getContext() {
        return context;
    }

    public static TrayIcon getTrayIcon() {
        return trayIcon;
    }


    public static EventBus getEventBus() {
        return eventBus;
    }
}
