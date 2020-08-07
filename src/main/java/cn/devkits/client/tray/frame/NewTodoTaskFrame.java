/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.util.DKSystemUIUtil;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
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
                "pref, 4dlu, 50dlu, 4dlu, pref", // columns：各列的大小
                "pref, 2dlu, pref, 2dlu, pref"); // rows：各行的大小

        layout.setRowGroups(new int[][]{{1, 3, 5}});//第1，3，5行具有相同的高度
        JPanel panel = new JPanel();
        panel.setLayout(layout);

        CellConstraints cc = new CellConstraints();//网格约束
        panel.add(new JLabel("Name:"), cc.xy(1, 1)); //第一列，第一行
        panel.add(new JTextField(), cc.xyw(3, 1, 3));//第一列，第三行，占3列的宽度
        panel.add(new JLabel("Reminder:"), cc.xy(1, 3));//第一列，第三行
        panel.add(new JTextField(), cc.xy(3, 3));//第3列，第3行
        panel.add(new JLabel("Description:"), cc.xy(1, 5));//第一列，第五行
        panel.add(new JTextField(), cc.xy(3, 5));//第3列，第五行
        panel.add(new JButton("detail"), cc.xy(5, 5));//第5列，第5行


        rootContainer.add(panel, BorderLayout.CENTER);
    }

    @Override
    protected void initListener() {

    }

    @Override
    public boolean isResizable() {
        return false;
    }
}
