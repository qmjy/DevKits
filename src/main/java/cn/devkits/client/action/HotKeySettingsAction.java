/*
 * Copyright (c) 2019-2021 QMJY.CN All rights reserved.
 */

package cn.devkits.client.action;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.privatejgoodies.forms.factories.CC;
import cn.devkits.client.util.DKSystemUIUtil;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

/**
 * <p>
 *
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2021/2/4
 */
public class HotKeySettingsAction extends BaseAction {
    public HotKeySettingsAction(Frame frame, JPanel cardLayoutRootPanel) {
        super(frame, cardLayoutRootPanel);

        putValue(Action.NAME, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_HOTKEY"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.KEYBOARD_O, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_HOTKEY_DESC"));

        registerPane();
    }

    @Override
    protected Component drawCenterPanel() {
        FormLayout layout = new FormLayout(
                "right:pref, 6dlu, 50dlu:grow, 6dlu, 30dlu"); // 5 columns; add rows later

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.append(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_HOTKEY_NEW_TASK_LAB"), new JTextField("Ctrl + T"), new JButton("清空"));
        builder.append(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_HOTKEY_SCREENSHOT"), new JTextField("Ctrl + Alt + A"), new JButton("清空"));
        return builder.getPanel();
    }
}
