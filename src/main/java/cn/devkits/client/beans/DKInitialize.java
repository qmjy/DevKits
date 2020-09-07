/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.beans;

import cn.devkits.client.service.InitializeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化
 */
@Configuration
public class DKInitialize implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(DKInitialize.class);
    @Autowired
    private InitializeService initializeService;

    @Override
    public void afterPropertiesSet() throws Exception {
        initializeService.initDb();
    }
}
