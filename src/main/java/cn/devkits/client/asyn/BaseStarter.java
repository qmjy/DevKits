package cn.devkits.client.asyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * <p>
 *
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2021/10/31
 */
public class BaseStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseStarter.class);

    /**
     * more look and feel:<br>
     * 1.http://www.javasoft.de/synthetica/screenshots/plain/ <br>
     * 2.https://www.cnblogs.com/clarino/p/8668160.html
     */
    protected void initLookAndFeel() {
        // UIManager.getSystemLookAndFeelClassName() get system defualt;
        String lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e1) {
            LOGGER.error("Init Look And Feel error:" + e1.getMessage());
        } catch (UnsupportedLookAndFeelException e) {
            LOGGER.error("UnsupportedLookAndFeelException:" + e.getMessage());
        }
    }

}
