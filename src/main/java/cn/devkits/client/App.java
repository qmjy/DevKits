/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client;

import cn.devkits.client.async.AppStarter;
import cn.devkits.client.rmi.server.ContextMenuHandler;
import cn.devkits.client.util.DKSysUIUtil;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Objects;


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
    private static final EventBus eventBus = new EventBus("DevkitsEvents");
    private static TrayIcon trayIcon;

    public static void main(String[] args) {
        if (isRmiCmd(args)) {
            System.exit(0);
        } else {
            initLookAndFeel();
            context = new AnnotationConfigApplicationContext(AppSpringContext.class);
            startRmiServer();
            if (SystemTray.isSupported()) {
                trayIcon = createTrayIcon();
                SwingUtilities.invokeLater(new AppStarter(trayIcon, args));
            } else {
                LOGGER.error("This system can not support tray function！");
            }
        }
    }

    private static boolean isRmiCmd(String[] args) {
        try {
            ContextMenuHandler obj = (ContextMenuHandler) Naming.lookup("//localhost/ContextMenuHandler");
            System.out.println(obj.execute("", ""));
            return true;
        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            LOGGER.error("Execute command failed: {}", Arrays.toString(args));
        }
        return false;
    }

    private static void startRmiServer() {
        try {
            ContextMenuHandler mouseEventService = new ContextMenuHandler();
            Naming.rebind("//localhost/ContextMenuHandler", mouseEventService);
            checkOrInstallContextMenu();
            System.out.println("RMI server is ready.");
        } catch (RemoteException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkOrInstallContextMenu() {

    }


    private static TrayIcon createTrayIcon() {
        try {
            TrayIcon trayIcon = new TrayIcon(ImageIO.read(Objects.requireNonNull(App.class.getClassLoader().getResource("logo.png"))));
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
        // UIManager.getSystemLookAndFeelClassName() get system default;
        DKSysUIUtil.setLookAndFeel(new NimbusLookAndFeel());
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
