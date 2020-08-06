/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.util.DKSystemUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * <p>
 * TodoListFrame
 * </p>
 *
 * @author Tophua
 * @since 2020/8/5
 */
public class TodoListFrame extends DKAbstractFrame {

    private JTabbedPane jTabbedPane;

    public TodoListFrame() {
        super(DKSystemUIUtil.getLocaleString("TODO_LIST_TITLE"), 0.7f);

        initUI(getContentPane());
        initListener();
    }

    @Override
    protected void initUI(Container rootContainer) {
        JToolBar toolBar = new JToolBar("Todo List Toolbar");
        toolBar.setFloatable(false);
        createToolbarBtns(toolBar);

        setPreferredSize(new Dimension(450, 130));
        add(toolBar, BorderLayout.PAGE_START);

        this.jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("TODO_LIST_TAB_MY"), new JScrollPane());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("TODO_LIST_TAB_OTHERS"), new JScrollPane());

        jTabbedPane.setFocusable(false);// 不显示选项卡上的焦点虚线边框
        rootContainer.add(jTabbedPane, BorderLayout.CENTER);
    }

    private void createToolbarBtns(JToolBar toolBar) {
        JButton systemBtn = new JButton(DKSystemUIUtil.getLocaleString("TODO_LIST_CREATE"));
        systemBtn.setFocusable(false);
        systemBtn.addActionListener(e -> {
            NewTodoTaskFrame frame = new NewTodoTaskFrame();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        });
        toolBar.add(systemBtn);
    }

    @Override
    protected void initListener() {

    }
}
