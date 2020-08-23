/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.App;
import cn.devkits.client.DKConstants;
import cn.devkits.client.service.impl.TodoTaskServiceImpl;
import cn.devkits.client.tray.model.TodoTaskModel;
import cn.devkits.client.util.DKDateTimeUtil;
import cn.devkits.client.util.DKSystemUIUtil;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

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

        String[] trayReminderHeader = new String[]{"编号", "名称", "Corn", "下次执行时间", "内容", "创建时间"};
        String[] emailReminderHeader = new String[]{"编号", "名称", "Corn", "下次执行时间", "收件人", "内容", "创建时间"};

        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("TODO_LIST_TAB_TRAY"), createReminderPane(trayReminderHeader, DKConstants.TODO_REMINDER.TRAY));
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("TODO_LIST_TAB_EMAIL"), createReminderPane(emailReminderHeader, DKConstants.TODO_REMINDER.EMAIL));

        jTabbedPane.setFocusable(false);
        rootContainer.add(jTabbedPane, BorderLayout.CENTER);
    }

    private String[][] queryData(String[] head, DKConstants.TODO_REMINDER tray) {
        TodoTaskServiceImpl service = (TodoTaskServiceImpl) App.getContext().getBean("todoTaskServiceImpl");

        List<TodoTaskModel> list = service.findAllToList(tray);
        String[][] data = new String[list.size()][head.length];

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < head.length; j++) {
                data[i][0] = String.valueOf(list.get(i).getId());
                data[i][1] = list.get(i).getTaskName();
                data[i][2] = list.get(i).getCorn();
                data[i][3] = getNextTime(list.get(i).getCorn());

                if (tray == DKConstants.TODO_REMINDER.TRAY) {
                    data[i][4] = list.get(i).getDescription();
                    data[i][5] = list.get(i).getCreateTime();
                } else {
                    data[i][4] = list.get(i).getEmail();
                    data[i][5] = list.get(i).getDescription();
                    data[i][6] = list.get(i).getCreateTime();
                }
            }
        }
        return data;
    }

    private String getNextTime(String corn) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(corn);
        Date date = cronSequenceGenerator.next(new Date());
        return DKDateTimeUtil.getDatetimeStr(date, DKDateTimeUtil.DATE_TIME_PATTERN_DEFAULT);
    }

    private Component createReminderPane(String[] head, DKConstants.TODO_REMINDER reminder) {
        JTable jTable = new JTable();

        String[][] data = queryData(head, reminder);
        DefaultTableModel tableModel = new DefaultTableModel(data, head);
        jTable.setModel(tableModel);
        DKSystemUIUtil.fitTableColumns(jTable);

        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jTable);
        return jScrollPane;
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
