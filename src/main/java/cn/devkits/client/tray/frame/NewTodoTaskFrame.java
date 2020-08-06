/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.util.DKSystemUIUtil;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.Container;
import java.awt.BorderLayout;


/**
 * <p>
 * 创建待办任务对话框
 * </p>
 *
 * @author liushaofeng
 * @since 2020/8/6
 */
public class NewTodoTaskFrame extends DKAbstractFrame {

    private static final int JTextField_COLUMN_15 = 15;

    public NewTodoTaskFrame() {
        super(DKSystemUIUtil.getLocaleString("TODO_NEW_DIALOG_TITLE"), 0.6f);

        initUI(getContentPane());
        initListener();
    }

    @Override
    protected void initUI(Container rootContainer) {
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(""));
        builder.setBorder(BorderFactory.createEmptyBorder(DKSystemUIUtil.COMPONENT_UI_PADDING_5, DKSystemUIUtil.COMPONENT_UI_PADDING_5, DKSystemUIUtil.COMPONENT_UI_PADDING_5, DKSystemUIUtil.COMPONENT_UI_PADDING_5));
        builder.appendColumn("right:pref");
        builder.appendColumn("3dlu");
        builder.appendColumn("fill:max(pref; 100px)");
        builder.appendColumn("5dlu");
        builder.appendColumn("right:pref");
        builder.appendColumn("3dlu");
        builder.appendColumn("fill:max(pref; 100px)");

        builder.append("First:", new JTextField(JTextField_COLUMN_15));

        builder.append("Last:", new JTextField(JTextField_COLUMN_15));
        builder.nextLine();

        builder.append("Married:", new JCheckBox());
        builder.nextLine();

        builder.append("Phone:", new JTextField(JTextField_COLUMN_15));
        builder.nextLine();

        builder.append("Fax:", new JTextField(JTextField_COLUMN_15));
        builder.nextLine();

        builder.append("Email:", new JTextField(JTextField_COLUMN_15));
        builder.nextLine();

        builder.appendSeparator("Work");

        builder.append("Company:", new JTextField(JTextField_COLUMN_15));
        builder.nextLine();

        builder.append("Phone:", new JTextField(JTextField_COLUMN_15));
        builder.nextLine();

        builder.append("Fax:", new JTextField(JTextField_COLUMN_15));
        builder.nextLine();


        rootContainer.add(builder.getPanel(), BorderLayout.CENTER);
    }

    @Override
    protected void initListener() {

    }

}
