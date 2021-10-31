/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.eventbus.EventBus;
import cn.devkits.client.asyn.AppStarter;
import cn.devkits.client.asyn.CliStarter;
import cn.devkits.client.camera.CameraFrame;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.awt.AWTException;
import java.awt.SplashScreen;
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
        if (args.length > 0) {
            dispatchArgs(args);
        } else {
            context = new AnnotationConfigApplicationContext(AppSpringContext.class);
            if (SystemTray.isSupported()) {
                trayIcon = createTrayIcon();
                SwingUtilities.invokeLater(new AppStarter(trayIcon));
                closeSplashScreen();
            } else {
                LOGGER.error("This system can not support tray function！");
            }
        }
    }

    private static void dispatchArgs(String[] args) {
        SwingUtilities.invokeLater(new CliStarter(args));
    }

    private static void closeSplashScreen() {
        final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null) {
            splash.close();
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
