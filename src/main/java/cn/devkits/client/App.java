package cn.devkits.client;

import javax.swing.SwingUtilities;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.util.Locale;
import cn.devkits.client.asyn.AppStarter;


/**
 * 
 * Development Kits
 * 
 * @author Fengshao Liu
 * @version 1.0.0
 * @datetime 2019年9月5日 下午10:51:07
 */
public class App {

    private static AnnotationConfigApplicationContext context;

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        context = new AnnotationConfigApplicationContext(AppSpringContext.class);
        SwingUtilities.invokeLater(new AppStarter());
    }

    public static AnnotationConfigApplicationContext getContext() {
        return context;
    }
}
