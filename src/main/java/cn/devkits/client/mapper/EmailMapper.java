/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.mapper;

import cn.devkits.client.tray.model.EmailCfgModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    @Insert({"INSERT INTO devkits_email_list(host, port, account, email, pwd, tls, defaultServer) values(#{model.host}, #{model.port}, #{model.account}, " +
            "#{model.email}, #{model.pwd}, #{model.tls}, #{model.defaultServer})"})
    void newEmail(@Param("model") EmailCfgModel model);

    @Select({"SELECT * FROM devkits_email_list ORDER BY createTime DESC"})
    List<EmailCfgModel> loadAllEmails();

    @Select({"SELECT * FROM devkits_email_list WHERE host=#{host} AND email=#{email}"})
    EmailCfgModel exist(@Param("host") String host, @Param("email") String email);

    @Update({"Update devkits_email_list SET port=#{model.port}, pwd=#{model.pwd}, email=#{model.email}, tls=#{model.tls}, defaultServer=#{model.defaultServer} WHERE " +
            "host=#{model.host} AND account=#{model.account}"})
    int update(@Param("model") EmailCfgModel model);

    @Update({"Update devkits_email_list SET defaultServer=0 WHERE host!=#{model.host} AND account!=#{model.account}"})
    void disableOtherDefaultServer(@Param("model") EmailCfgModel model);

    @Select({"SELECT * FROM devkits_email_list WHERE defaultServer = 1"})
    List<EmailCfgModel> loadDefaultEmails();
}
