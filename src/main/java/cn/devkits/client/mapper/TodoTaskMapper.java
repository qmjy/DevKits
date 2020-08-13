/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.mapper;

import cn.devkits.client.tray.model.TodoTaskModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 待办数据库访问接口
 * </p>
 *
 * @author liushaofeng
 * @since 2020/8/13
 */
@Mapper
public interface TodoTaskMapper {

    @Insert({"insert into devkits_todo_list(taskName, reminder, corn, email, description) values( #{model.taskName}, #{model.reminder}, #{model" +
            ".corn}, #{model.email}, #{model.description})"})
    void newTodoTask(@Param("model") TodoTaskModel todoTaskModel);
}
