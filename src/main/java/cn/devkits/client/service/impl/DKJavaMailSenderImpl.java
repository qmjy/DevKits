/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.service.impl;

import cn.devkits.client.tray.model.EmailCfgModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * <p>
 *
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2020/9/7
 */
public class DKJavaMailSenderImpl extends JavaMailSenderImpl {
    private String from;

    public DKJavaMailSenderImpl() {
    }

    public DKJavaMailSenderImpl(EmailCfgModel server) {
        setHost(server.getHost());
        setPort(server.getPort());
        setUsername(StringUtils.isEmpty(server.getAccount()) ? server.getEmail() : server.getAccount());
        setPassword(server.getPwd());
        setFrom(server.getEmail());
        setDefaultEncoding("UTF-8");
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
