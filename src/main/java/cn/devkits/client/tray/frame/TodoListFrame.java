/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.App;
import cn.devkits.client.DKConstants;
import cn.devkits.client.service.impl.TodoTaskServiceImpl;
import cn.devkits.client.tray.model.TodoTaskModel;
import cn.devkits.client.util.DKSysUIUtil;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * TodoListFrame
 * </p>
 *
 * @author Tophua
 * @since 2020/8/5
 */
public class TodoListFrame extends DKAbstractFrame {
    private String[] reminderHeader = new String[]{"编号", "名称", "Corn", "下次执行时间", "内容", "创建时间"};
    private String[] emailReminderHeader = new String[]{"编号", "名称", "Corn", "下次执行时间", "收件人", "内容", "创建时间"};

    private JTabbedPane jTabbedPane;
    private JTable trayTable = new JTable();
    private JTable emailTable = new JTable();
    private JTable dialogTable = new JTable();

    public TodoListFrame() {
        super(DKSysUIUtil.getLocale("TODO_LIST_TITLE"), 0.7f);

        initUI(getDKPane());
        initListener();
    }


    @Override
    protected void initUI(Container rootContainer) {
        intiJToolBar(rootContainer);

        jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab(DKSysUIUtil.getLocale("TODO_LIST_TAB_TRAY"), createReminderPane(trayTable, reminderHeader, DKConstants.TODO_REMINDER.TRAY));
        jTabbedPane.addTab(DKSysUIUtil.getLocale("TODO_LIST_TAB_EMAIL"), createReminderPane(emailTable, emailReminderHeader, DKConstants.TODO_REMINDER.EMAIL));
        jTabbedPane.addTab(DKSysUIUtil.getLocale("TODO_LIST_TAB_DIALOG"), createReminderPane(dialogTable, reminderHeader,
                DKConstants.TODO_REMINDER.DIALOG));

        jTabbedPane.setFocusable(false);
        rootContainer.add(jTabbedPane, BorderLayout.CENTER);
    }

    private void intiJToolBar(Container rootContainer) {
        JToolBar toolBar = new JToolBar("Todo List Toolbar");
        toolBar.setFloatable(false);
        createToolbarBtns(toolBar);

        setPreferredSize(new Dimension(450, 130));
        rootContainer.add(toolBar, BorderLayout.PAGE_START);
    }

    private String[][] queryData(DKConstants.TODO_REMINDER reminder) {
        TodoTaskServiceImpl service = (TodoTaskServiceImpl) App.getContext().getBean("todoTaskServiceImpl");
        List<TodoTaskModel> list = service.findAllTodoListByReminder(reminder);
        return convertTableData(list, reminder);
    }

    private String[][] convertTableData(List<TodoTaskModel> list, DKConstants.TODO_REMINDER reminder) {
        String[] header = reminder == DKConstants.TODO_REMINDER.EMAIL ? emailReminderHeader : reminderHeader;
        String[][] data = new String[list.size()][header.length];

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < header.length; j++) {
                data[i][0] = String.valueOf(i + 1);
                data[i][1] = list.get(i).getTaskName();
                data[i][2] = list.get(i).getCorn();
                data[i][3] = getNextTime(list.get(i).getCorn());

                if (reminder == DKConstants.TODO_REMINDER.EMAIL) {
                    data[i][4] = list.get(i).getEmail();
                    data[i][5] = list.get(i).getDescription();
                    data[i][6] = list.get(i).getCreateTime();
                } else {
                    data[i][4] = list.get(i).getDescription();
                    data[i][5] = list.get(i).getCreateTime();
                }
            }
        }
        return data;
    }

    private String getNextTime(String corn) {

        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.CRON4J);
        CronParser parser = new CronParser(cronDefinition);
        ExecutionTime executionTime = ExecutionTime.forCron(parser.parse(corn));
        Optional<ZonedDateTime> zonedDateTime = executionTime.nextExecution(ZonedDateTime.now());
        return zonedDateTime.toString();
    }

    private Component createReminderPane(JTable jTable, String[] head, DKConstants.TODO_REMINDER reminder) {
        String[][] data = queryData(reminder);
        DefaultTableModel tableModel = new DefaultTableModel(data, head);
        jTable.setModel(tableModel);

        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jTable);
        return jScrollPane;
    }

    private void createToolbarBtns(JToolBar toolBar) {
        JButton systemBtn = new JButton(DKSysUIUtil.getLocale("TODO_LIST_CREATE"));
        systemBtn.setFocusable(false);
        systemBtn.addActionListener(e -> {
            NewTodoTaskFrame frame = new NewTodoTaskFrame(this);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        });
        toolBar.add(systemBtn);
    }

    @Override
    protected void initListener() {

    }

    public void refresh() {
        String[][] trayData = queryData(DKConstants.TODO_REMINDER.TRAY);
        DefaultTableModel trayTableModel = new DefaultTableModel(trayData, reminderHeader);
        trayTable.setModel(trayTableModel);

        String[][] emailData = queryData(DKConstants.TODO_REMINDER.EMAIL);
        DefaultTableModel emailTableModel = new DefaultTableModel(emailData, emailReminderHeader);
        emailTable.setModel(emailTableModel);

        String[][] dialogData = queryData(DKConstants.TODO_REMINDER.DIALOG);
        DefaultTableModel dialogTableModel = new DefaultTableModel(dialogData, reminderHeader);
        dialogTable.setModel(dialogTableModel);
    }
}
