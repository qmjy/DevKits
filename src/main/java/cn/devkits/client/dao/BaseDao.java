/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.dao;

import org.apache.ibatis.annotations.Update;

public interface BaseDao {

    /**
     * 建表 devkits_clipboard_history
     *
     * @return int
     */
    @Update({"CREATE TABLE IF NOT EXISTS devkits_clipboard_history(ID INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT, type INT NOT NULL, createTime TimeStamp NOT NULL DEFAULT (datetime('now','localtime')))"})
    int createClipboardTable();

    /**
     * 创建系统配置表
     */
    @Update({"CREATE TABLE IF NOT EXISTS devkits_system(ID INTEGER PRIMARY KEY AUTOINCREMENT, key VARCHAR NOT NULL, value VARCHAR, createTime TimeStamp NOT NULL DEFAULT (datetime('now','localtime')))"})
    void createSystemConfig();
}
