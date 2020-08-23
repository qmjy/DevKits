/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.model;

import cn.devkits.client.DKConstants;
import com.google.gson.internal.$Gson$Preconditions;

/**
 * <p>
 * 待办任务数据模型
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2020/8/13
 */
public class TodoTaskModel {
    private int id;
    private String taskName;
    private int reminder;
    private String corn;
    private String email;
    private String description;
    private String createTime;
    private boolean isDeleted = false;

    public TodoTaskModel(String name, String corn, String desc) {
        this.taskName = name;
        this.corn = corn;
        this.description = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getReminder() {
        return reminder;
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }

    public void setReminder(DKConstants.TODO_REMINDER reminder) {
        this.reminder = reminder.ordinal();
    }

    public String getCorn() {
        return corn;
    }

    public void setCorn(String corn) {
        this.corn = corn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }


    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
