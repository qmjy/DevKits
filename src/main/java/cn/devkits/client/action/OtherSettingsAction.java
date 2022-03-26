/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.action;

import cn.devkits.client.util.DKSysUIUtil;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Frame;


public class OtherSettingsAction extends BaseAction {

    public OtherSettingsAction(Frame frame, JPanel cardLayoutRootPanel) {
        super(frame, cardLayoutRootPanel);

        putValue(Action.NAME, DKSysUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_OTHERS"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.COG, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSysUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_OTHERS_DESC"));

        registerPane();
    }
}
