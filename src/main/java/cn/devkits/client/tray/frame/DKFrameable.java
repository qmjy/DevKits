/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import java.awt.Toolkit;

public interface DKFrameable {
    int WINDOW_SIZE_WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.5);
    int WINDOW_SIZE_HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.6);
}
