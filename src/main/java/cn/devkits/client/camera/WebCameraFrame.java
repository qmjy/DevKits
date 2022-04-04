package cn.devkits.client.camera;

import com.github.sarxos.webcam.WebcamResolution;
import cn.devkits.client.tray.frame.DKAbstractFrame;
import cn.devkits.client.util.DKSysUIUtil;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

/**
 * <p>
 *
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2021/11/7
 */
public class WebCameraFrame extends DKAbstractFrame {

    private static final Dimension CAMERA_DIMENSION = WebcamResolution.VGA.getSize();

    public WebCameraFrame() {
        super(DKSysUIUtil.getLocale("CAMERA"), (int) CAMERA_DIMENSION.getWidth(), (int) CAMERA_DIMENSION.getHeight());
        createMenubar();

        initUI(getDKPane());
        initListener();
    }

    private void createMenubar() {
        JMenuBar mb = new JMenuBar();
        JMenu fileMenu = new JMenu(DKSysUIUtil.getLocale("COMMON_MENU_FILE"));

        JMenuItem quitMenuItem = new JMenuItem(DKSysUIUtil.getLocale("COMMON_MENU_QUIT"));
        Icon quitIcon = IconFontSwing.buildIcon(FontAwesome.SIGN_OUT, 16, new Color(50, 50, 50));
        quitMenuItem.setIcon(quitIcon);
        quitMenuItem.addActionListener(e -> {
            this.setVisible(false);
        });
        fileMenu.add(quitMenuItem);

        mb.add(fileMenu);

        mb.add(new JMenu("设备"));
        mb.add(new JMenu("选项"));
        mb.add(new JMenu("捕捉"));
        setJMenuBar(mb);
    }

    private JToolBar createToolbar() {
        JToolBar jToolBar = new JToolBar();

        Icon cameraIcon = IconFontSwing.buildIcon(FontAwesome.CAMERA, 16, new Color(50, 50, 50));
        JButton cameraBtn = new JButton(cameraIcon);
        cameraBtn.setFocusable(false);
        jToolBar.add(cameraBtn);
        return jToolBar;
    }

    @Override
    protected void initUI(Container rootContainer) {
        rootContainer.add(createToolbar(), BorderLayout.PAGE_START);
        rootContainer.add(new JLabel("CENTER"), BorderLayout.CENTER);
    }

    @Override
    protected void initListener() {

    }
}
