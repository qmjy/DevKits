package cn.devkits.client;

import javax.swing.SwingUtilities;
import cn.devkits.client.tray.frame.asyn.AppStarter;


/**
 * 
 * Development Kits
 * 
 * @author fengshao
 * @version 1.0.0
 * @datetime 2019年9月5日 下午10:51:07
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new AppStarter());
    }
}
