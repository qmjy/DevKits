/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.devkits.client.tray.window.ScreenCaptureWindow;

/**
 * 退出截屏对话框
 * 
 * @author devkits.cn
 *
 */
public class CaptureEscKeyListener extends KeyAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureEscKeyListener.class);

    private ScreenCaptureWindow window;

    public CaptureEscKeyListener(ScreenCaptureWindow window) {
        this.window = window;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        LOGGER.info("ESC key event:" + e.getKeyCode());

        if (KeyEvent.VK_ESCAPE == e.getKeyCode()) {
            window.dispose();
        }
    }
}
