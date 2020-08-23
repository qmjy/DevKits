/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.service;

import cn.devkits.client.DKConstants;
import cn.devkits.client.tray.model.TodoTaskModel;

import java.util.List;


/**
 * <p>
 * TodoTask 业务接口
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2020/8/13
 */
public interface TodoTaskService {
    /**
     * 创建新的待办任务
     *
     * @param todoTaskModel 待办任务信息
     */
    void newTodoTask(TodoTaskModel todoTaskModel);

    List<TodoTaskModel> findAllToList(DKConstants.TODO_REMINDER tray);
}
