/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.service.impl;

import cn.devkits.client.action.EmailSettingsAction;
import cn.devkits.client.mapper.EmailMapper;
import cn.devkits.client.service.EmailService;
import cn.devkits.client.tray.model.EmailCfgModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * EmailServiceImpl
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2020/8/29
 */
@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Autowired
    private EmailMapper emailMapper;

    @Override
    public void saveOrUpdate(EmailCfgModel model) {
        if (emailMapper.exist(model.getHost(), model.getAccount()) == null) {
            emailMapper.newEmail(model);
        } else {
            emailMapper.update(model);
        }

        if (model.isDefaultServer()) {
            emailMapper.disableOtherDefaultServer(model);
        }
    }

    @Override
    public List<EmailCfgModel> loadAllEmails() {
        return emailMapper.loadAllEmails();
    }

    @Override
    public EmailCfgModel findDefaultSmtpServer() {
        List<EmailCfgModel> cfgs = emailMapper.loadDefaultEmails();
        if (cfgs != null && !cfgs.isEmpty()) {
            if (cfgs.size() > 1) {
                LOGGER.error("There are multiple SMTP configuration servers found in database, please check!");
            }
            return cfgs.get(0);
        }
        return null;
    }
}
