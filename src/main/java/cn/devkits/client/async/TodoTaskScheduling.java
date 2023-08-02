/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import cn.devkits.client.App;
import cn.devkits.client.DKConstants;
import cn.devkits.client.service.EmailService;
import cn.devkits.client.service.impl.TodoTaskServiceImpl;
import cn.devkits.client.tray.model.TodoTaskModel;
import cn.devkits.client.util.DKDateTimeUtil;

import javax.swing.JOptionPane;
import java.awt.TrayIcon;
import java.util.Date;
import java.util.List;

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


    /**
     * 动态注册待办事项
     *
     * @param model 待办事项数据模型
     */
    @Subscribe
    public void listen(TodoTaskModel model) {
        taskRegistrar.scheduleTriggerTask(new TriggerTask(new TodoThread(model, emailService), new TodoTrigger(model.getCorn())));
    }
}

/**
 * 待办执行线程
 */
class TodoThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoThread.class);

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
        } else if (model.getReminder() == DKConstants.TODO_REMINDER.EMAIL.ordinal()) {
            emailService.sendHtmlMail(model.getEmail(), model.getTaskName(), convertHtmlContent(model.getDescription()));
        } else if (model.getReminder() == DKConstants.TODO_REMINDER.DIALOG.ordinal()) {
            JOptionPane.showMessageDialog(null, model.getDescription(), model.getTaskName(), JOptionPane.INFORMATION_MESSAGE);
        } else {
            LOGGER.info("Trigger task detail remind at '{}' with content: {}", DKDateTimeUtil.currentTimeStr(), model.toString());
        }
    }

    private static String convertHtmlContent(String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>")
                .append("<p>")
                .append(content)
                .append("</p>")
                .append("<p style='padding:0;font-size:12px;line-height:18px;color:#888 !important;'><br/><br/><br/>改进意见：")
                .append("<a href='https://github.com/qmjy/DevKits/issues/new' blank='_target' style='color:#555;'>github</a>")
                .append(" | ")
                .append("<a href='http://toolcloud.huawei.com/toolmall/tooldetails/46718d31406842deb0e969a715377c93' blank='_target' style='color:#555;'>toolcloud</a>")
                .append("</p>")
                .append("</body></html>");
        return sb.toString();
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