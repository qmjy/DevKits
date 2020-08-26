/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.action;

import cn.devkits.client.util.DKSystemUIUtil;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;

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
        FormLayout layout = new FormLayout("right:max(50dlu;p), 4dlu, 150dlu, 50dlu",
                "p, 2dlu, p, 3dlu, p, 3dlu, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        builder.addSeparator(DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_SEG_SEP"), cc.xyw(1, 1, 4));
        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_SERVER"), cc.xy(1, 3));
        builder.add(createServerListComponet(), cc.xy(3, 3));
        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_ACCOUNT"), cc.xy(1, 5));
        builder.add(new JTextField(), cc.xy(3, 5));
        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_PWD"), cc.xy(1, 7));
        builder.add(new JPasswordField(), cc.xy(3, 7));
        return builder.getPanel();
    }

    private Component createServerListComponet() {
        JComboBox<Object> cmb = new JComboBox<>();
        cmb.addItem("身份证");
        cmb.addItem("驾驶证");
        cmb.addItem("军官证");
        return cmb;
    }
}
