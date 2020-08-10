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
        FormLayout layout = new FormLayout(
                "right:pref, 4dlu, 270dlu, 4dlu, pref", // columns：各列的大小
                "pref, 2dlu, pref,2dlu, pref, 2dlu, top:pref"); // rows：各行的大小

        //Column and row groups specifiy that a set of columns or rows will get the same width or height.
        layout.setRowGroups(new int[][]{{1, 3}});
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(DKSystemUIUtil.COMPONENT_UI_PADDING_5, DKSystemUIUtil.COMPONENT_UI_PADDING_5,
                DKSystemUIUtil.COMPONENT_UI_PADDING_5, DKSystemUIUtil.COMPONENT_UI_PADDING_5));
        panel.setLayout(layout);

        CellConstraints cc = new CellConstraints();
        panel.add(new JLabel(DKSystemUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_NAME")), cc.xy(1, 1));
        panel.add(new JTextField(), cc.xyw(3, 1, 3));

        panel.add(new JLabel(DKSystemUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_REMINDER")), cc.xy(1, 3));
        panel.add(new JRadioButton(DKSystemUIUtil.getLocaleString("TODO_NEW_DIALOG_REMINDER_EMAIL")), cc.xyw(3, 3, 2));//第3列，第3行

        panel.add(new JLabel(DKSystemUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_CORN")), cc.xy(1, 5));
        panel.add(new JTextField(), cc.xy(3, 5));
        panel.add(createCornHelpIcon(), cc.xy(5, 5));

        panel.add(new JLabel(DKSystemUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_DESC")), cc.xy(1, 7));
        panel.add(createDescTextArea(), cc.xyw(3, 7, 3));

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
