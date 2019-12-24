package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

/**
 * 
 * 二维码
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年12月19日 下午10:18:50
 */
public class QrCodeFrame extends DKAbstractFrame {

    /** serialVersionUID */
    private static final long serialVersionUID = -4030282787993924346L;


    public QrCodeFrame() {
        super("QR Code", 0.7f, 0.6f);

        initUI(getRootPane());
        initListener();
    }


    @Override
    protected void initUI(JRootPane jRootPane) {
        jRootPane.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar("QR Code Tool Bar");
        toolBar.setFloatable(false);

        JButton qrCode = new JButton(IconFontSwing.buildIcon(FontAwesome.QRCODE, 20, new Color(50, 50, 50)));
        JButton deCode = new JButton(IconFontSwing.buildIcon(FontAwesome.EYE, 20, new Color(50, 50, 50)));

        toolBar.add(qrCode);
        toolBar.add(deCode);

        JTabbedPane jTabbedPane = new JTabbedPane();

        jTabbedPane.addTab("Text", new JLabel());
        jTabbedPane.addTab("URL", new JLabel());
        jTabbedPane.addTab("Profile", new JLabel());

        jTabbedPane.setFocusable(false);// 不显示选项卡上的焦点虚线边框


        jRootPane.add(toolBar, BorderLayout.PAGE_START);
        jRootPane.add(jTabbedPane);
    }


    @Override
    protected void initListener() {
        // TODO Auto-generated method stub

    }
}
