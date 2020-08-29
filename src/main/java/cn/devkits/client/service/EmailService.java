/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.service;

import cn.devkits.client.tray.model.EmailCfgModel;

import java.util.List;

/**
 * <p>
 * Email Service
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2020/8/29
 */
public interface EmailService {
    /**
     * 添加一个email服务器
     *
     * @param model emial服务器信息
     */
    void newEmail(EmailCfgModel model);

    /**
     * 加载所有邮箱配置
     * @return 邮箱配置
     */
    List<EmailCfgModel> loadAllEmails();
}
