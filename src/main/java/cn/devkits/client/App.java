package cn.devkits.client;

import javax.swing.SwingUtilities;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import cn.devkits.client.asyn.AppStarter;


/**
 * 
 * Development Kits
 * 
 * @author fengshao
 * @version 1.0.0
 * @datetime 2019年9月5日 下午10:51:07
 */
public class App {
    
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(AppSpringContext.class);

        SwingUtilities.invokeLater(new AppStarter());
    }
}
