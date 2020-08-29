/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.service.impl;

import cn.devkits.client.mapper.BaseMapper;
import cn.devkits.client.service.InitializeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Spring容器初始化后执行的动作
 */
@Service
public class InitializeServiceImpl implements InitializeService {
    @Autowired
    private BaseMapper baseDao;

    @Override
    public void initDb() {
        baseDao.createClipboardTable();
        baseDao.createSystemConfigTable();
        baseDao.createTodoTaskTable();
        baseDao.createEmailTable();
    }
}
