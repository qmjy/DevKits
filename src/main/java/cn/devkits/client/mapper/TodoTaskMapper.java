/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.mapper;

import cn.devkits.client.DKConstants;
import cn.devkits.client.tray.model.TodoTaskModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    @Insert({"INSERT INTO devkits_todo_list(taskName, reminder, corn, email, description) values( #{model.taskName}, #{model.reminder}, #{model" +
            ".corn}, #{model.email}, #{model.description})"})
    void newTodoTask(@Param("model") TodoTaskModel todoTaskModel);


    /**
     * 查询当前有效的所有待办任务
     *
     * @param reminder reminder类型：系统托盘/Email
     * @return 待办事务
     */
    @Select({"SELECT * FROM devkits_todo_list WHERE reminder = #{reminder} AND deleted = 0 ORDER BY createTime DESC"})
    List<TodoTaskModel> findAllToList(@Param("reminder") int reminder);
}
