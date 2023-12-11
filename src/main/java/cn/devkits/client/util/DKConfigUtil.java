/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration util
 *
 * @author fengshao liu
 * @version 1.0.0
 * @datetime 2019年9月6日 下午10:29:28
 */
public class DKConfigUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DKConfigUtil.class);

    private static final DKConfigUtil INSTANCE = new DKConfigUtil();
    private Model model;

    private DKConfigUtil() {
        loadVersionProperties(DKSysUtil.isDevelopMode());
    }

    private void loadVersionProperties(boolean isDevelopMode) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
            InputStream input = isDevelopMode ? Files.newInputStream(Paths.get(getDevelopModelFilePath())) : DKConfigUtil.class.getResourceAsStream("/META-INF/maven/cn.devkits.client/devkits/pom.xml");
            model = reader.read(input);
        } catch (IOException | XmlPullParserException e) {
            LOGGER.error("Load pom.xml file failed: " + e.getMessage());
        }
    }

    private String getDevelopModelFilePath() {
        String path = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        int startIndex = path.indexOf("/target");
        if (startIndex != -1) {
            path = path.substring(0, startIndex);
        }
        return path + File.separator + "pom.xml";
    }

    public static DKConfigUtil getInstance() {
        return INSTANCE;
    }

    public Model getPomInfo() {
        return model;
    }


    public String getAboutHtml() {
        if (model == null) {
            LOGGER.debug("Clean this maven project please, if you are running this app in your IDE.");
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append(DKSysUIUtil.getLocale("DEVKITS_DESC")).append("<br/><br/>");
        sb.append(DKSysUIUtil.getLocaleWithColon("AUTHOR") + " Shaofeng Liu").append("<br/>");
        sb.append(DKSysUIUtil.getLocaleWithColon("HOMEPAGE") + " <a href='").append(model.getUrl()).append("'>");
        sb.append(model.getUrl()).append("</a>").append("<br/>");
        sb.append(DKSysUIUtil.getLocaleWithColon("ISSUE") + " <a href='" + getIssueUri() + "'>" + getIssueUri() + "</a>");

        return sb.toString();
    }

    public String getIssueUri() {
        return model.getIssueManagement().getUrl();
    }


    public String getVersion() {
        return model.getVersion();
    }

}
