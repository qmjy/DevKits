package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import cn.devkits.client.util.DKConfigUtil;

/**
 * 
 * About Dialog
 * 
 * @author shaofeng
 * @version 1.0.0
 * @datetime 2019年9月6日 下午11:39:56
 */
public class AboutFrame extends DKAbstractFrame {

    private static final long serialVersionUID = 3737746590178589617L;


    public AboutFrame() {
        super("About Devkits");
        
        initUI(getRootPane());
        initListener();
    }



    @Override
    protected void initUI(JRootPane jRootPane) {
        jRootPane.setLayout(new BorderLayout());

        JTextArea jTextArea = new JTextArea(10, 50);
        jTextArea.setEditable(false);
        jTextArea.setText(DKConfigUtil.getInstance().getAboutTxt());

        JScrollPane comp = new JScrollPane(jTextArea);
        jRootPane.add(comp, BorderLayout.CENTER);
    }



    @Override
    protected void initListener() {
        // TODO Auto-generated method stub

    }
}
