/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.util;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import cn.devkits.client.tray.model.EmailCfgModel;
import cn.devkits.client.tray.model.TodoTaskModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class DKNetworkUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DKNetworkUtil.class);

    public static boolean socketReachable(String address, int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(address, port), 1000);
            boolean portAvailable = socket.isConnected();
            socket.close();
            return portAvailable;
        } catch (UnknownHostException uhe) {
            LOGGER.error("UnknownHostException: " + address);
        } catch (IOException ioe) {
            LOGGER.error("Socket IOException: address is {} and port is {} ", address, port);
        }
        return false;
    }

    public static boolean hostReachable(String ipOrDomain) {
        if (DKStringUtil.isIP(ipOrDomain) || DKStringUtil.isDomain(ipOrDomain)) {
            try {
                InetAddress byName = InetAddress.getByName(ipOrDomain);
                return byName.isReachable(500);
            } catch (UnknownHostException e) {
                LOGGER.error("Unknown Host Check: " + ipOrDomain);
                return false;
            } catch (IOException e) {
                LOGGER.error("IO exception occurred on checking reachable: " + ipOrDomain);
                return false;
            }
        }
        return false;
    }


    /**
     * 获取本地IP地址
     *
     * @return 本地IP
     */
    public static Optional<String> getIp() {
        Optional<InetAddress> inetAddress = getInetAddress();
        if (inetAddress.isPresent()) {
            InetAddress lanIp = inetAddress.get();
            return Optional.of(lanIp.toString().replaceAll("^/+", ""));
        } else {
            return Optional.empty();
        }
    }

    /**
     * 获取当前活动网络设备的MAC地址
     *
     * @return MAC
     */
    public static Optional<String> getMacAddress() {
        Optional<InetAddress> inetAddress = getInetAddress();
        if (inetAddress.isPresent()) {
            InetAddress inetAddress2 = inetAddress.get();
            try {
                NetworkInterface network = NetworkInterface.getByInetAddress(inetAddress2);
                byte[] mac = network.getHardwareAddress();

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                return Optional.of(sb.toString());
            } catch (SocketException e) {
                LOGGER.error("SocketException: " + e.getMessage());
            }
        }
        return Optional.empty();
    }

    private static Optional<InetAddress> getInetAddress() {
        String ipAddress = null;
        Enumeration<NetworkInterface> net;
        try {
            net = NetworkInterface.getNetworkInterfaces();
            while (net.hasMoreElements()) {
                NetworkInterface element = net.nextElement();
                Enumeration<InetAddress> addresses = element.getInetAddresses();

                byte[] hardwareAddress = element.getHardwareAddress();
                while (addresses.hasMoreElements() && hardwareAddress != null && hardwareAddress.length > 0 && !isVMMac(hardwareAddress)) {
                    InetAddress ip = addresses.nextElement();
                    if (ip instanceof Inet4Address) {
                        if (ip.isSiteLocalAddress()) {
                            ipAddress = ip.getHostAddress();
                            return Optional.of(InetAddress.getByName(ipAddress));
                        }
                    }
                }
            }
        } catch (SocketException e) {
            LOGGER.error("SocketException: " + e.getMessage());
        } catch (UnknownHostException e) {
            LOGGER.error("UnknownHostException: " + e.getMessage());
        }
        return Optional.empty();
    }

    private static boolean isVMMac(byte[] mac) {
        if (null == mac) {
            return false;
        }
        byte invalidMacs[][] = {{0x00, 0x05, 0x69}, // VMWare
                {0x00, 0x1C, 0x14}, // VMWare
                {0x00, 0x0C, 0x29}, // VMWare
                {0x00, 0x50, 0x56}, // VMWare
                {0x08, 0x00, 0x27}, // Virtualbox
                {0x0A, 0x00, 0x27}, // Virtualbox
                {0x00, 0x03, (byte) 0xFF}, // Virtual-PC
                {0x00, 0x15, 0x5D} // Hyper-V
        };

        for (byte[] invalid : invalidMacs) {
            if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2]) {
                return true;
            }
        }

        return false;
    }

    /**
     * 邮箱SMTP服务器验证
     *
     * @param cfg 服务器参数配置
     * @return SMTP服务校验结果
     */
    public static Map<Boolean, String> testSmtpServer(EmailCfgModel cfg) {
        HashMap<Boolean, String> resultMap = new HashMap<>();
        try {
            Properties props = new Properties();
            props.put("mail.smtp.starttls.enable", String.valueOf(cfg.isTls()));
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.timeout", "5000");
            props.put("mail.smtp.writetimeout", "5000");

            // or use getDefaultInstance instance if desired...
            Session session = Session.getInstance(props, null);
            Transport transport = session.getTransport("smtp");
            transport.connect(cfg.getHost(), cfg.getPort(), StringUtils.isEmpty(cfg.getAccount()) ? cfg.getEmail() : cfg.getAccount(), cfg.getPwd());
            transport.close();
            resultMap.put(true, "");
            return resultMap;
        } catch (AuthenticationFailedException e) {
            resultMap.put(false, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_TEST_MSG_ERROR_AUTHFAILED"));
        } catch (MessagingException e) {
            resultMap.put(false, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_EMAIL_TEST_MSG_ERROR_OTHERS"));
        }
        return resultMap;
    }

    /**
     * 发送邮件
     *
     * @param cfg      邮箱服务器信息
     * @param title    邮件主题
     * @param content  邮件内容
     * @param reciever 邮件接收人
     */
    public static void sendMail(EmailCfgModel cfg, String title, String content, String reciever) {
        if (cfg == null) {
            LOGGER.error("Can't find default SMTP settings...");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", cfg.getHost());
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);

        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(cfg.getEmail()));
            wrapRecipients(reciever, message);
            message.setSubject(title);

            Multipart multipart = new MimeMultipart();
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(convertHtmlContent(content), "text/html; charset=utf-8");
            multipart.addBodyPart(contentPart);

            message.setContent(multipart);
            message.saveChanges();
            Transport transport = session.getTransport("smtp");
            transport.connect(cfg.getHost(), StringUtils.isEmpty(cfg.getAccount()) ? cfg.getEmail() : cfg.getAccount(), cfg.getPwd());
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            LOGGER.error("Send email failed: {}", e.getMessage());
        }
    }

    private static String convertHtmlContent(String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>")
                .append("<p>")
                .append(content)
                .append("</p>")
                .append("<p style='padding:0;font-size:12px;line-height:18px;color:#888 !important;'><br/><br/><br/>蛋壳需求诉求：")
                .append("<a href='https://github.com/qmjy/DevKits/issues/new' blank='_target' style='color:#555;'>github</a>")
                .append(" | ")
                .append("<a href='http://toolcloud.huawei.com/toolmall/tooldetails/46718d31406842deb0e969a715377c93' blank='_target' style='color:#555;'>toolcloud</a>")
                .append("</p>")
                .append("</body></html>");
        return sb.toString();
    }

    private static void wrapRecipients(String reciever, MimeMessage message) throws MessagingException {
        if (reciever.indexOf(",") >= 0) {
            Address[] internetAddressTo = InternetAddress.parse(reciever);
            message.addRecipients(Message.RecipientType.TO, internetAddressTo);
        } else {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(reciever));
        }
    }
}
