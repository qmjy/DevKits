/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.model;

/**
 * <p>
 *
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2020/8/29
 */
public class EmailCfgModel {
    private int id;
    private String host;
    private int port;
    private String account;
    private String email;
    private String pwd;
    private boolean isTls;
    private boolean defaultServer;
    private String createTime;

    public EmailCfgModel() {
    }

    public EmailCfgModel(String host, int port, String account, String pwd, boolean isTls) {
        this.host = host;
        this.port = port;
        this.account = account;
        this.pwd = pwd;
        this.isTls = isTls;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public boolean isTls() {
        return isTls;
    }

    public void setTls(boolean tls) {
        isTls = tls;
    }

    public boolean isDefaultServer() {
        return defaultServer;
    }

    public void setDefaultServer(boolean defaultServer) {
        this.defaultServer = defaultServer;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
