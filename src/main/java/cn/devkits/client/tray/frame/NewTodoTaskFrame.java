/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.util.DKSystemUIUtil;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * <p>
 * 创建待办任务对话框
 * </p>
 *
 * @author liushaofeng
 * @since 2020/8/6
 */
public class NewTodoTaskFrame extends DKAbstractFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewTodoTaskFrame.class);
    private static final int JTextField_COLUMN_20 = 20;

    public NewTodoTaskFrame() {
        super(DKSystemUIUtil.getLocaleString("TODO_NEW_DIALOG_TITLE"), 0.6f);

        initUI(getContentPane());
        initListener();
    }

    /**
     * 布局参考：https://blog.csdn.net/miaoxiongvip/article/details/84464984、https://blog.csdn.net/miaoxiongvip/article/details/84296522
     *
     * @param rootContainer Root Pane
     */
    @Override
    protected void initUI(Container rootContainer) {

        JPanel panel = new JPanel();
        SpringLayout springLayout = new SpringLayout();
        panel.setLayout(springLayout);

        JComponent[][] components = new JComponent[4][3];

        JLabel nameLbl = new JLabel(DKSystemUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_NAME"));
        panel.add(nameLbl);
        JTextField nameTextField = new JTextField();
        panel.add(nameTextField);
        components[0][0] = nameLbl;
        components[0][1] = nameTextField;

        JLabel reminderLbl = new JLabel(DKSystemUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_REMINDER"));
        panel.add(reminderLbl);
        JRadioButton reminderTypeOfEmail = new JRadioButton(DKSystemUIUtil.getLocaleString("TODO_NEW_DIALOG_REMINDER_EMAIL"));
        panel.add(reminderTypeOfEmail);
        components[1][0] = reminderLbl;
        components[1][1] = reminderTypeOfEmail;

        JLabel cornLbl = new JLabel(DKSystemUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_CORN"));
        panel.add(cornLbl);
        JTextField cornTextField = new JTextField();
        panel.add(cornTextField);
        JLabel cornHelpIconLbl = createCornHelpIcon();
        panel.add(cornHelpIconLbl);
        components[2][0] = cornLbl;
        components[2][1] = cornTextField;
        components[2][2] = cornHelpIconLbl;

        JLabel descLbl = new JLabel(DKSystemUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_DESC"));
        panel.add(descLbl);
        JScrollPane descTextAreaPane = createDescTextArea();
        panel.add(descTextAreaPane);
        components[3][0] = descLbl;
        components[3][1] = descTextAreaPane;

        DKSystemUIUtil.doLayoutOfSpring(springLayout, panel, components);

        rootContainer.add(panel, BorderLayout.CENTER);
        rootContainer.add(createBottomPane(), BorderLayout.PAGE_END);
    }

    private JLabel createCornHelpIcon() {
        Icon icon = IconFontSwing.buildIcon(FontAwesome.QUESTION, 16, new Color(50, 50, 50));
        JLabel helpIconLbl = new JLabel();
        helpIconLbl.setIcon(icon);
        helpIconLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String cornUrl = "https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm";
                try {
                    DKSystemUIUtil.browseURL(new URI(cornUrl));
                } catch (URISyntaxException uriSyntaxException) {
                    LOGGER.error("Can't convert string to URI: {}", cornUrl);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        return helpIconLbl;
    }

    private JScrollPane createDescTextArea() {
        JScrollPane jScrollPane = new JScrollPane();

        JTextArea comp = new JTextArea();
        comp.setRows(10);
        comp.setColumns(40);
        jScrollPane.setViewportView(comp);

        return jScrollPane;
    }

    private JPanel createBottomPane() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        buttonPane.add(Box.createHorizontalGlue());

        JButton applyBtn = new JButton(DKSystemUIUtil.getLocaleString("COMMON_BTNS_CREATE"));
        applyBtn.addActionListener(e -> {

        });

        buttonPane.add(applyBtn);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton closeBtn = new JButton(DKSystemUIUtil.getLocaleString("COMMON_BTNS_CLOSE"));
        closeBtn.addActionListener(e -> {

        });
        buttonPane.add(closeBtn);

        return buttonPane;
    }

    @Override
    protected void initListener() {

    }

    @Override
    public boolean isResizable() {
        return false;
    }
}
