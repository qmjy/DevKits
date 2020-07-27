/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.service.impl;

import cn.devkits.client.dao.BaseDao;
import cn.devkits.client.service.InitializeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitializeServiceImpl implements InitializeService {
    @Autowired
    private BaseDao baseDao;

    @Override
    public void initDb() {
        baseDao.createClipboardTable();
        baseDao.createSystemConfig();
    }
}
