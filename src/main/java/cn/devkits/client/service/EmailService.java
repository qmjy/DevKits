/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.service;

import cn.devkits.client.tray.model.EmailCfgModel;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Email Service
 * https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/integration.html#mail
 * https://www.jianshu.com/p/c0af856814a8
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2020/8/29
 */
public interface EmailService {
    /**
     * 添加一个Email服务器，重复判断条件：SMTP服务器，邮箱
     *
     * @param model Email服务器信息
     */
    void saveOrUpdate(EmailCfgModel model);

    /**
     * 加载所有邮箱配置
     *
     * @return 邮箱配置
     */
    List<EmailCfgModel> loadAllEmails();


    /**
     * 邮箱SMTP服务器验证
     *
     * @param cfg 服务器参数配置
     * @return SMTP服务校验结果
     */
    Map<Boolean, String> testSMTPServer(EmailCfgModel cfg);

    /**
     * 发送普通邮件
     *
     * @param to      邮件接收人，多人用','分割
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    void sendSimpleMail(String to, String subject, String content);

    /**
     * 发送HTML邮件
     *
     * @param to      邮件接收人，多人用','分割
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    void sendHtmlMail(String to, String subject, String content);
}
