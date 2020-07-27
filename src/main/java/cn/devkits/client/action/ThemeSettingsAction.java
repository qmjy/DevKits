/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.action;

import cn.devkits.client.util.DKSystemUIUtil;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.*;

/**
 * 主题设置面板
 */
public class ThemeSettingsAction extends BaseAction {

    public ThemeSettingsAction(JPanel cardLayoutRootPanel) {
        super(cardLayoutRootPanel);

        putValue(Action.NAME, DKSystemUIUtil.getLocaleString("SETTINGS_COMMON_SETTINGS_THEME"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.THEMEISLE, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSystemUIUtil.getLocaleString("SETTINGS_COMMON_SETTINGS_THEME_DESC"));

        registerPane();
    }
}
