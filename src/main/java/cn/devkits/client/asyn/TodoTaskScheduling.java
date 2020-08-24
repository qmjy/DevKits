/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.asyn;

import cn.devkits.client.App;
import cn.devkits.client.DKConstants;
import cn.devkits.client.service.impl.TodoTaskServiceImpl;
import cn.devkits.client.tray.model.TodoTaskModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.awt.TrayIcon;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 待办事务提醒
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2020/8/23
 */
@Component
@EnableScheduling
public class TodoTaskScheduling implements SchedulingConfigurer {

    @Autowired
    private TodoTaskServiceImpl todoTaskServiceImpl;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        List<TodoTaskModel> allTodoList = todoTaskServiceImpl.findAllTodoList();
        if (!allTodoList.isEmpty()) {
            List<TodoTaskModel> trayTodoList = allTodoList.stream().filter(e -> {
                return e.getReminder() == DKConstants.TODO_REMINDER.TRAY.ordinal();
            }).collect(Collectors.toList());

            List<TodoTaskModel> emailTodoList = allTodoList.stream().filter(e -> {
                return e.getReminder() == DKConstants.TODO_REMINDER.EMAIL.ordinal();
            }).collect(Collectors.toList());

            registerTrayReminder(taskRegistrar, trayTodoList);
            registerEmailReminder(taskRegistrar, emailTodoList);
        }
    }


    private void registerTrayReminder(ScheduledTaskRegistrar taskRegistrar, List<TodoTaskModel> trayTodoList) {
        for (TodoTaskModel model : trayTodoList) {
            taskRegistrar.addTriggerTask(new TodoThread(model), new TodoTrigger(model.getCorn()));
        }
    }

    private void registerEmailReminder(ScheduledTaskRegistrar taskRegistrar, List<TodoTaskModel> emailTodoList) {
        for (TodoTaskModel model : emailTodoList) {
            taskRegistrar.addTriggerTask(new TodoThread(model), new TodoTrigger(model.getCorn()));
        }
    }
}

/**
 * 待办执行线程
 */
class TodoThread implements Runnable {

    private final TodoTaskModel model;

    public TodoThread(TodoTaskModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        if (model.getReminder() == DKConstants.TODO_REMINDER.TRAY.ordinal()) {
            App.getTrayIcon().displayMessage(model.getTaskName(), model.getDescription(), TrayIcon.MessageType.INFO);
        }
    }
}

/**
 * 待办任务Trigger
 */
class TodoTrigger implements Trigger {

    private final String expression;

    public TodoTrigger(String expression) {
        this.expression = expression;
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        CronTrigger cronTrigger = new CronTrigger(expression);
        return cronTrigger.nextExecutionTime(triggerContext);
    }
}