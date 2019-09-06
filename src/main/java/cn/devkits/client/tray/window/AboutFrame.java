package cn.devkits.client.tray.window;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import cn.devkits.client.util.DKConfigUtil;
/**
 * 
 * About Dialog
 * @author shaofeng
 * @version 1.0.0
 * @datetime 2019年9月6日 下午11:39:56
 */
public class AboutFrame extends JFrame implements DKWindowable {

    private static final long serialVersionUID = 3737746590178589617L;


    public AboutFrame() {

        super("About Devkits");

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();

        int startX = (screenSize.width - WINDOW_SIZE_WIDTH) / 2;
        int startY = (screenSize.height - WINDOW_SIZE_HEIGHT) / 2;

        super.setBounds(startX, startY, WINDOW_SIZE_WIDTH, WINDOW_SIZE_HEIGHT);
        initPane();
    }


    private void initPane() {
        JTextArea jTextArea = new JTextArea(10, 50);
        jTextArea.setText(DKConfigUtil.getInstance().getAboutTxt());

        JScrollPane comp = new JScrollPane(jTextArea);

        add(comp);
    }
}
