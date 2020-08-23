/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.service.impl;

import cn.devkits.client.DKConstants;
import cn.devkits.client.mapper.TodoTaskMapper;
import cn.devkits.client.service.TodoTaskService;
import cn.devkits.client.tray.model.TodoTaskModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 *
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2020/8/13
 */
@Service
public class TodoTaskServiceImpl implements TodoTaskService {

    @Autowired
    private TodoTaskMapper todoMapper;

    @Override
    public void newTodoTask(TodoTaskModel todoTaskModel) {
        todoMapper.newTodoTask(todoTaskModel);
    }

    @Override
    public List<TodoTaskModel> findAllTodoList(DKConstants.TODO_REMINDER tray) {
        return todoMapper.findAllToList(tray.ordinal());
    }
}
