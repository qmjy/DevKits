/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.action;

import cn.devkits.client.util.DKSystemUIUtil;
import cn.devkits.client.util.SpringUtilities;
import com.privatejgoodies.forms.layout.CellConstraints;
import com.privatejgoodies.forms.layout.FormLayout;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;

/**
 * <p>
 *
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2020/8/25
 */
public class EmailSettingsAction extends BaseAction {

    public EmailSettingsAction(Frame frame, JPanel cardLayoutRootPanel) {
        super(frame, cardLayoutRootPanel);

        putValue(Action.NAME, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.ENVELOPE_O, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_DESC"));

        registerPane();
    }

    /**
     * formlayout:
     * 1. https://ptolemy.berkeley.edu/ptolemyII/ptII8.1/ptII/doc/codeDoc/com/jgoodies/forms/layout/FormLayout.html
     * 2. https://www.formdev.com/jformdesigner/doc/layouts/formlayout/
     *
     * @return main content
     */
    @Override
    protected Component drawCenterPanel() {
        FormLayout layout = new FormLayout(
                "right:pref, 6dlu, pref",
                "pref, 3dlu, pref, 3dlu, pref");

        CellConstraints cc = new CellConstraints();
        JPanel panel = new JPanel(layout);
        panel.add(new JLabel("服务器："), cc.xy(1, 1));
        panel.add(new JTextField(50), cc.xy(3, 1));
        panel.add(new JLabel("账户："), cc.xy(1, 3));
        panel.add(new JTextField(50), cc.xy(3, 3));
        panel.add(new JLabel("密码："), cc.xy(1, 5));
        panel.add(new JTextField(50), cc.xy(3, 5));
        return panel;
    }
}
