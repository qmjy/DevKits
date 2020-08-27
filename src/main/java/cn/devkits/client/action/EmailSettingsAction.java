/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.action;

import cn.devkits.client.DKConstants;
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
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
        FormLayout layout = new FormLayout("right:max(50dlu;p), 4dlu, 90dlu, 4dlu, right:max(20dlu;p), 4dlu, 40dlu, 4dlu, right:max(20dlu;p), 4dlu, 30dlu",
                "p, 2dlu, p, 3dlu, p, 3dlu, p, 10dlu, p, 2dlu, p");

        layout.setRowGroups(new int[][]{{1, 9}});

        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();

        builder.addSeparator(DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_SEG_NEW"), cc.xyw(1, 1, 11));

        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_SERVER"), cc.xy(1, 3));
        builder.add(createServerListComponet(), cc.xy(3, 3));
        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_PORT"), cc.xy(5, 3));
        builder.add(new JTextField(), cc.xy(7, 3));
        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_SSL"), cc.xy(9, 3));
        builder.add(new JRadioButton(), cc.xy(11, 3));

        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_ACCOUNT"), cc.xy(1, 5));
        builder.add(new JTextField(), cc.xy(3, 5));

        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_PWD"), cc.xy(1, 7));
        builder.add(new JPasswordField(), cc.xy(3, 7));

        builder.addSeparator(DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_SEG_ALL"), cc.xyw(1, 9, 11));

        builder.add(loadServersTable(), cc.xyw(1, 11, 11));

        return builder.getPanel();
    }

    private JScrollPane loadServersTable() {
        JTable jTable = new JTable() {
            @Override
            public Dimension getPreferredScrollableViewportSize() {
                return DKSystemUIUtil.updatePreferredScrollableViewportSize(this);
            }
        };
        String[][] emailData = new String[1][6];
        emailData[0][0] = "1";
        emailData[0][1] = "smtp.huawei.com";
        emailData[0][2] = "456";
        emailData[0][3] = "false";
        emailData[0][4] = "lwx166646";
        emailData[0][5] = "false";
        DefaultTableModel emailTableModel = new DefaultTableModel(emailData, new String[]{"编号", "SMTP", "端口", "SSL", "账户","默认"});
        jTable.setModel(emailTableModel);
        DKSystemUIUtil.fitTableColumns(jTable);

        return new JScrollPane(jTable);
    }

    private Component createServerListComponet() {
        JComboBox<Object> cmb = new JComboBox<>();
        cmb.addItem("smtpscn.huawei.com");
        cmb.addItem("smtp.qq.com");
        cmb.addItem("smtp.exmail.qq.com");
        cmb.addItem("smtp.163.com");
        cmb.addItem("smtp.gmail.com");
        cmb.addItem("smtp.isoftstone.com");
        return cmb;
    }
}
