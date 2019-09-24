package cn.devkits.client.util;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Configuration util
 * 
 * @author fengshao
 * @version 1.0.0
 * @datetime 2019年9月6日 下午10:29:28
 */
public class DKConfigUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DKConfigUtil.class);

    private static DKConfigUtil INSTANCE = new DKConfigUtil();
    private Properties cfgProperties = new Properties();
    private Model model;

    private DKConfigUtil() {
        try {
            cfgProperties.load(DKConfigUtil.class.getResourceAsStream("/META-INF/maven/cn.devkits.client/devkits/pom.properties"));
        } catch (IOException e) {
            LOGGER.error("Load config file failed: " + e.getMessage());
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


    public String getAboutTxt() {
        StringBuilder sb = new StringBuilder();

        sb.append("DevKits is a kits for programmer effective working.");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("Author: shaofeng liu");
        sb.append(System.getProperty("line.separator"));
        sb.append("Version: " + model.getVersion());
        sb.append(System.getProperty("line.separator"));
        sb.append("BuildTime: 20190812120334");
        sb.append(System.getProperty("line.separator"));
        sb.append("HomePage: " + model.getUrl());
        sb.append(System.getProperty("line.separator"));
        sb.append("Issue: https://github.com/qmjy/DevKits/issues");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("Thanks for the open source as follow:");
        sb.append(System.getProperty("line.separator"));

        List<Dependency> dependencies = model.getDependencies();
        for (Dependency dependency : dependencies) {
            sb.append(dependency.getArtifactId());
            sb.append("-");
            sb.append(dependency.getVersion());
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }

}
