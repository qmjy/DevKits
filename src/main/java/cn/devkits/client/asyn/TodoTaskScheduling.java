/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.asyn;

import cn.devkits.client.App;
import cn.devkits.client.DKConstants;
import cn.devkits.client.service.EmailService;
import cn.devkits.client.service.impl.TodoTaskServiceImpl;
import cn.devkits.client.tray.model.EmailCfgModel;
import cn.devkits.client.tray.model.TodoTaskModel;
import cn.devkits.client.util.DKNetworkUtil;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.awt.Event;
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
    @Autowired
    private EmailService emailService;
    private ScheduledTaskRegistrar taskRegistrar;

    public TodoTaskScheduling() {
        App.getEventBus().register(this);
    }

    //TODO 删除通知的功能待开发
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;

        List<TodoTaskModel> allTodoList = todoTaskServiceImpl.findAllTodoList();
        if (!allTodoList.isEmpty()) {
            for (TodoTaskModel model : allTodoList) {
                taskRegistrar.addTriggerTask(new TodoThread(model, emailService), new TodoTrigger(model.getCorn()));
            }
        }
    }

    @Subscribe
    public void listen(TodoTaskModel model) {
        taskRegistrar.addTriggerTask(new TodoThread(model, emailService), new TodoTrigger(model.getCorn()));
    }
}

/**
 * 待办执行线程
 */
class TodoThread implements Runnable {

    private final TodoTaskModel model;
    private final EmailService emailService;

    public TodoThread(TodoTaskModel model, EmailService emailService) {
        this.model = model;
        this.emailService = emailService;
    }

    @Override
    public void run() {
        if (model.getReminder() == DKConstants.TODO_REMINDER.TRAY.ordinal()) {
            App.getTrayIcon().displayMessage(model.getTaskName(), model.getDescription(), TrayIcon.MessageType.INFO);
        } else {
            EmailCfgModel cfg = emailService.findDefaultSmtpServer();
            DKNetworkUtil.sendMail(cfg, model.getTaskName(), model.getDescription(), model.getEmail());
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