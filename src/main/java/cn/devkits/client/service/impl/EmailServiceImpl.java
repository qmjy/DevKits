/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.service.impl;

import cn.devkits.client.mapper.EmailMapper;
import cn.devkits.client.service.EmailService;
import cn.devkits.client.tray.model.EmailCfgModel;
import cn.devkits.client.util.DKStringUtil;
import cn.devkits.client.util.DKSysUIUtil;
import com.drew.lang.annotations.NotNull;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
            resultMap.put(false, DKSysUIUtil.getLocale("SETTINGS_SYS_SETTINGS_EMAIL_TEST_MSG_ERROR_AUTHFAILED"));
        }
        return resultMap;
    }

    @Override
    public void sendSimpleMail(@NotNull String tos, String subject, String content) {
        DKJavaMailSenderImpl mailSender = createMailSender();

        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setFrom(mailSender.getFrom());
            setRecipients(message, null, tos.toLowerCase(Locale.getDefault()));
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            LOGGER.info("Simple email has been sent!");
        } catch (Exception e) {
            LOGGER.error("Sent simple email failed!", e);
        }
    }

    @Override
    public void sendHtmlMail(@NotNull String tos, String subject, String content) {
        DKJavaMailSenderImpl mailSender = createMailSender();

        MimeMessage message = mailSender.createMimeMessage();
        try {
            // true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailSender.getFrom());
            setRecipients(null, helper, tos.toLowerCase(Locale.getDefault()));
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            LOGGER.info("Html email has been sent!");
        } catch (MessagingException e) {
            LOGGER.error("Sent html email failed!", e);
        }
    }


    private void setRecipients(SimpleMailMessage message, MimeMessageHelper helper, String tos) throws MessagingException {
        String[] split = getSplit(tos);
        int ccLength = DKStringUtil.countSubStr(tos, "cc:");
        int bcLength = DKStringUtil.countSubStr(tos, "bc:");
        int toLength = split.length - ccLength - bcLength;

        String[] ccArray = new String[ccLength], bcArray = new String[bcLength], toArray = new String[toLength];
        int ccCount = 0, bcCount = 0, toCount = 0;

        for (String s : split) {
            if (s.startsWith("cc:")) {
                ccArray[ccCount] = s.substring(3);
                ccCount++;
            } else if (s.startsWith("bc:")) {
                bcArray[bcCount] = s.substring(3);
                bcCount++;
            } else {
                toArray[toCount] = s;
                toCount++;
            }
        }
        if (message == null) {
            helper.setTo(toArray);
            helper.setCc(ccArray);
            helper.setBcc(bcArray);
        } else {
            message.setTo(toArray);
            message.setCc(ccArray);
            message.setBcc(bcArray);
        }
    }

    private String[] getSplit(String tos) {
        if (tos.indexOf(";") > 0) {
            return tos.split(";");
        } else if (tos.indexOf(",") > 0) {
            return tos.split(",");
        } else {
            return new String[]{tos};
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
