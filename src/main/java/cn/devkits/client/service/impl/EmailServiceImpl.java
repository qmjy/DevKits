/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.service.impl;

import cn.devkits.client.mapper.EmailMapper;
import cn.devkits.client.service.EmailService;
import cn.devkits.client.tray.model.EmailCfgModel;
import cn.devkits.client.util.DKSystemUIUtil;

import com.sun.istack.internal.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (emailMapper.exist(model.getHost(), model.getEmail()) == null) {
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
    public Map<Boolean, String> testSMTPServer(EmailCfgModel cfg) {
        HashMap<Boolean, String> resultMap = new HashMap<>();
        DKJavaMailSenderImpl dkJavaMailSender = new DKJavaMailSenderImpl(cfg);
        try {
            dkJavaMailSender.testConnection();
            resultMap.put(true, "");
        } catch (MessagingException e) {
            resultMap.put(false, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_TEST_MSG_ERROR_AUTHFAILED"));
        }
        return resultMap;
    }

    @Override
    public void sendSimpleMail(@NotNull String to, String subject, String content) {
        DKJavaMailSenderImpl mailSender = createMailSender();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailSender.getFrom());
        message.setTo(to.split(","));
        message.setSubject(subject);
        message.setText(content);

        try {
            mailSender.send(message);
            LOGGER.info("Simple email has been sent!");
        } catch (Exception e) {
            LOGGER.error("Sent simple email failed!", e);
        }

    }

    @Override
    public void sendHtmlMail(@NotNull String to, String subject, String content) {
        DKJavaMailSenderImpl mailSender = createMailSender();

        MimeMessage message = mailSender.createMimeMessage();
        try {
            // true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailSender.getFrom());
            helper.setTo(to.split(","));
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            LOGGER.info("Html email has been sent!");
        } catch (MessagingException e) {
            LOGGER.error("Sent html email failed!", e);
        }
    }

    private DKJavaMailSenderImpl createMailSender() {
        EmailCfgModel server = findDefaultSmtpServer();
        if (server == null) {
            LOGGER.error("Create java mail sender bean failed(not config SMTP server)!");
            return null;
        }
        return new DKJavaMailSenderImpl(server);
    }

    private EmailCfgModel findDefaultSmtpServer() {
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
