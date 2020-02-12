package cn.devkits.client.util;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Configuration util
 * @author fengshao liu
 * @version 1.0.0
 * @time 2019年9月6日 下午10:29:28
 */
public class DKConfigUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DKConfigUtil.class);

    private static DKConfigUtil INSTANCE = new DKConfigUtil();
    private Properties cfgProperties = new Properties();
    private Model model;

    private DKConfigUtil() {
        try {
            cfgProperties.load(DKConfigUtil.class.getResourceAsStream("/META-INF/maven/cn.devkits.client/devkits/pom.properties"));
        } catch (NullPointerException e) {
            LOGGER.error("Load devkits pom.properties file failed: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Load devkits pom.xml file failed: " + e.getMessage());
        }

        MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
            model = reader.read(DKConfigUtil.class.getResourceAsStream("/META-INF/maven/cn.devkits.client/devkits/pom.xml"));
        } catch (IOException | XmlPullParserException e) {
            LOGGER.error("Load pom.xml file failed: " + e.getMessage());
        }
    }

    public static DKConfigUtil getInstance() {
        return INSTANCE;
    }

    public Model getPomInfo() {
        return model;
    }

    public Properties getPomProperties() {
        return cfgProperties;
    }

    public String getAboutHtml() {
        if (model == null) {
            LOGGER.debug("Clean this maven project please, if you are running this app in your IDE.");
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("Devkits is a toolkit for improving work efficiency.");
        sb.append("<br/><br/>");
        sb.append("Author: shaofeng liu");
        sb.append("<br/>");
        sb.append("BuildTime: 20190812120334");
        sb.append("<br/>");
        sb.append("HomePage: <a href='");
        sb.append(model.getUrl());
        sb.append("'>");
        sb.append(model.getUrl());
        sb.append("</a>");
        sb.append("<br/>");
        sb.append("Issue: <a href='" + getIssueUri() + "'>" + getIssueUri() + "</a>");

        return sb.toString();
    }

    public String getIssueUri() {
        return model.getIssueManagement().getUrl();
    }

}
