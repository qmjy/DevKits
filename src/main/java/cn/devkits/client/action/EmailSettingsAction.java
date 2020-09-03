/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.action;

import cn.devkits.client.App;
import cn.devkits.client.service.EmailService;
import cn.devkits.client.tray.model.EmailCfgModel;
import cn.devkits.client.util.DKNetworkUtil;
import cn.devkits.client.util.DKSystemUIUtil;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2020/8/25
 */
public class EmailSettingsAction extends BaseAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSettingsAction.class);

    private final String[] header = new String[]{"编号", "账户", "邮箱", "SMTP", "端口", "TLS", "默认", "创建时间"};
    private final String[] items = new String[]{
            "smtpscn.huawei.com",
//            "smtp.qq.com",
            "smtp.exmail.qq.com",
            "smtp.163.com",
//            "smtp.gmail.com",
            "smtp.isoftstone.com"
    };
    private final JTable savedEmailsTable = new JTable();

    private JComboBox<String> smtpServers = new JComboBox<>(items);

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
        FormLayout layout = new FormLayout("right:max(15dlu;p):grow, 3dlu, 80dlu:grow, 3dlu, right:max(15dlu;p):grow, 3dlu, 45dlu:grow, 3dlu, right:max" +
                "(15dlu;p):grow, 3dlu, 50dlu:grow",
                "p, 2dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 10dlu, p, 2dlu, d");

        layout.setRowGroups(new int[][]{{1, 9}});

        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();

        builder.addSeparator(DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_SEG_NEW"), cc.xyw(1, 1, 11));

        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_SERVER"), cc.xy(1, 3));
        builder.add(smtpServers, cc.xy(3, 3));
        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_PORT"), cc.xy(5, 3));
        JTextField portComponent = new JTextField();
        portComponent.setText("25");
        builder.add(portComponent, cc.xy(7, 3));
        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_TLS"), cc.xy(9, 3));
        JRadioButton tlsComponent = new JRadioButton();
        builder.add(tlsComponent, cc.xy(11, 3));

        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_MAIL"), cc.xy(1, 5));
        JTextField mailComponent = new JTextField();
        builder.add(mailComponent, cc.xy(3, 5));
        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_PWD"), cc.xy(5, 5));
        JPasswordField pwdComponent = new JPasswordField();
        builder.add(pwdComponent, cc.xy(7, 5));
        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_ACCOUNT"), cc.xy(9, 5));
        JTextField accountComponent = new JTextField();
        builder.add(accountComponent, cc.xy(11, 5));

        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_EMAIL_LBL_DEFAULT"), cc.xy(1, 7));
        JCheckBox defaultSmtpServerComponent = new JCheckBox();
        builder.add(defaultSmtpServerComponent, cc.xy(3, 7));

        Component btnsPane = createBtnsPane(portComponent, tlsComponent, mailComponent, pwdComponent, accountComponent, defaultSmtpServerComponent);
        builder.add(btnsPane, cc.xyw(1, 9, 11));

        builder.addSeparator(DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_SEG_ALL"), cc.xyw(1, 11, 11));

        refreshAllMailsTable();
        builder.add(new JScrollPane(savedEmailsTable), cc.xyw(1, 13, 11));
        return builder.getPanel();
    }

    private Component createBtnsPane(JTextField portComponent, JRadioButton tlsComponent, JTextField mailComponent, JPasswordField pwdComponent,
                                     JTextField accountComponent, JCheckBox defaultSmtpServerComponent) {
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton testBtn = new JButton(DKSystemUIUtil.getLocaleString("COMMON_BTNS_TEST"));
        jPanel.add(testBtn);
        testBtn.addActionListener(e -> {
            testSmtpSever(portComponent, tlsComponent, accountComponent, pwdComponent, mailComponent);
        });

        JButton save = new JButton(DKSystemUIUtil.getLocaleString("COMMON_BTNS_SAVE_UPDATE"));
        jPanel.add(save);
        save.addActionListener(e -> {
            save2Db(portComponent, tlsComponent, accountComponent, pwdComponent, mailComponent, defaultSmtpServerComponent);
        });
        return jPanel;
    }

    private void save2Db(JTextField portComponent, JRadioButton tlsComponent, JTextField accountComponent, JPasswordField pwdComponent,
                         JTextField mailComponent, JCheckBox defaultSmtpServerComponent) {
        EmailCfgModel cfg = new EmailCfgModel(smtpServers.getSelectedItem().toString(),
                Integer.parseInt(portComponent.getText()), accountComponent.getText(), new String(pwdComponent.getPassword()), tlsComponent.isSelected());
        cfg.setEmail(mailComponent.getText());
        Map.Entry<Boolean, String> next = doSmtpServerTest(cfg);
        if (next.getKey()) {
            persistence2Db(cfg, defaultSmtpServerComponent.isSelected());
            refreshAllMailsTable();
            JOptionPane.showMessageDialog(frame, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_SAVE_MSG_SUCCESS"),
                    DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_TEST_TITLE"), JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, next.getValue(), DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_TEST_TITLE"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void refreshAllMailsTable() {
        EmailService service = (EmailService) App.getContext().getBean("emailServiceImpl");
        List<EmailCfgModel> allMails = service.loadAllEmails();

        String[][] emailData = new String[allMails.size()][header.length];
        for (int i = 0; i < allMails.size(); i++) {
            for (int j = 0; j < header.length; j++) {
                emailData[i][0] = String.valueOf(i + 1);
                emailData[i][1] = allMails.get(i).getAccount();
                emailData[i][2] = allMails.get(i).getEmail();
                emailData[i][3] = allMails.get(i).getHost();
                emailData[i][4] = String.valueOf(allMails.get(i).getPort());
                emailData[i][5] = String.valueOf(allMails.get(i).isTls());
                emailData[i][6] = String.valueOf(allMails.get(i).isDefaultServer());
                emailData[i][7] = allMails.get(i).getCreateTime();
            }
        }
        DefaultTableModel emailTableModel = new DefaultTableModel(emailData, header);
        savedEmailsTable.setModel(emailTableModel);
        DKSystemUIUtil.fitTableColumns(savedEmailsTable);
    }

    private void testSmtpSever(JTextField portComponent, JRadioButton tlsComponent, JTextField accountComponent, JPasswordField pwdComponent, JTextField mailComponent) {
        EmailCfgModel cfg = new EmailCfgModel(smtpServers.getSelectedItem().toString(),
                Integer.parseInt(portComponent.getText()), accountComponent.getText(), new String(pwdComponent.getPassword()), tlsComponent.isSelected());
        cfg.setEmail(mailComponent.getText());
        Map.Entry<Boolean, String> next = doSmtpServerTest(cfg);
        if (next.getKey()) {
            JOptionPane.showMessageDialog(frame, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_TEST_MSG_SUCCESS"),
                    DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_TEST_TITLE"), JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, next.getValue(), DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_TEST_TITLE"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Map.Entry<Boolean, String> doSmtpServerTest(EmailCfgModel cfg) {
        Map<Boolean, String> success = DKNetworkUtil.testSmtpServer(cfg);
        return success.entrySet().iterator().next();
    }

    private void persistence2Db(EmailCfgModel cfg, boolean defaultSmtpServer) {
        EmailService service = (EmailService) App.getContext().getBean("emailServiceImpl");
        cfg.setDefaultServer(defaultSmtpServer);
        service.saveOrUpdate(cfg);
    }
}
