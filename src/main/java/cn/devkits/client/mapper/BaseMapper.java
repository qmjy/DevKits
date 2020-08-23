/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BaseMapper {
    /**
     * 建表 devkits_clipboard_history
     *
     * @return int
     */
    @Update({"CREATE TABLE IF NOT EXISTS devkits_clipboard_history(id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT, type INT NOT NULL, createTime TimeStamp NOT NULL DEFAULT (datetime('now','localtime')))"})
    int createClipboardTable();

    /**
     * 创建系统配置表
     */
    @Update({"CREATE TABLE IF NOT EXISTS devkits_system(id INTEGER PRIMARY KEY AUTOINCREMENT, key VARCHAR NOT NULL, value VARCHAR, createTime TimeStamp NOT NULL DEFAULT (datetime('now','localtime')))"})
    void createSystemConfig();

    /**
     * 创建待办任务信息
     */
    @Update({"CREATE TABLE IF NOT EXISTS devkits_todo_list(id INTEGER PRIMARY KEY AUTOINCREMENT, taskName TEXT NOT NULL, reminder INTEGER, corn TEXT " +
            "NOT NULL, email TEXT, description TEXT, createTime TimeStamp NOT NULL DEFAULT (datetime('now','localtime')), deleted NUMERIC DEFAULT 0)"})
    void createTodoTask();
}
