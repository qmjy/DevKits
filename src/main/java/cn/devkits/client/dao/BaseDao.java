/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
     * 判断表是否存在<br/>
     * https://blog.csdn.net/p15097962069/article/details/103578662
     *
     * @return 如果返回的数组计数等于1，则表示该表存在。 否则它不存在
     */
    @Select({"SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='${tableName}';"})
    int tableExists(@Param("tableName") String tableName);
}
