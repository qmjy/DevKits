/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.mapper;

import cn.devkits.client.tray.model.EmailCfgModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Email Data Mapper
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2020/8/29
 */
@Mapper
public interface EmailMapper {

    @Insert({"INSERT INTO devkits_email_list(host, port, account, pwd, tls, defaultServer) values( #{model.host}, #{model.port}, #{model.account}, #{model.pwd}, #{model.tls}, #{model.defaultServer})"})
    void newEmail(@Param("model") EmailCfgModel model);

    @Select({"SELECT * FROM devkits_email_list WHERE deleted = 0 ORDER BY createTime DESC"})
    List<EmailCfgModel> loadAllEmails();
}
