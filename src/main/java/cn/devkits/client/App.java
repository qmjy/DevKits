/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client;

import cn.devkits.client.asyn.AppStarter;
import cn.devkits.client.util.DKSystemUIUtil;
import com.github.lgooddatepicker.components.CalendarPanel;
import com.github.lgooddatepicker.components.DatePickerSettings;
import org.jdesktop.swingx.JXTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;


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
    private static TrayIcon trayIcon;

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        context = new AnnotationConfigApplicationContext(AppSpringContext.class);
        if (SystemTray.isSupported()) {
            trayIcon = createTrayIcon();
            SwingUtilities.invokeLater(new AppStarter(trayIcon));
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


    public static AnnotationConfigApplicationContext getContext() {
        return context;
    }

    public static TrayIcon getTrayIcon() {
        return trayIcon;
    }
}
