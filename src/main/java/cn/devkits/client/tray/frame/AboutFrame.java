package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JEditorPane;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
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
    private JEditorPane jEditorPane;


    public AboutFrame() {
        super("About Devkits", 0.5f, 0.7f);

        initUI(getRootPane());
        initListener();
    }



    @Override
    protected void initUI(JRootPane jRootPane) {
        jRootPane.setLayout(new BorderLayout());

        this.jEditorPane = new JEditorPane();
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        jEditorPane.setEditable(false);
        jEditorPane.setText(DKConfigUtil.getInstance().getAboutHtml());
        
        JScrollPane comp = new JScrollPane(jEditorPane);
        jRootPane.add(comp, BorderLayout.CENTER);
    }



    @Override
    protected void initListener() {
        jEditorPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if(Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

    }
}
