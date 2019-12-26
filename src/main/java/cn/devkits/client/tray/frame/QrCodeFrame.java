package cn.devkits.client.tray.frame;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

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

    private Webcam webcam = null;
    private JTabbedPane decodePanel;

    public QrCodeFrame() {
        super("QR Code", 0.7f, 0.6f);

        initUI(getRootPane());
        initListener();
    }


    @Override
    protected void initUI(JRootPane jRootPane) {
        CardLayout cardLayout = new CardLayout();

        jRootPane.setLayout(cardLayout);

        this.decodePanel = new JTabbedPane();
        decodePanel.addTab("Upload", initUploadPane());
        decodePanel.addTab("Camera", new JPanel());

        JTabbedPane codePanel = new JTabbedPane();

        codePanel.addTab("Text", new JLabel());
        codePanel.addTab("URL", new JLabel());
        codePanel.addTab("Profile", new JLabel());

        codePanel.setFocusable(false);// 不显示选项卡上的焦点虚线边框

        jRootPane.add(decodePanel);
        jRootPane.add(codePanel);
    }


    private Component initUploadPane() {
        JPanel jPanel = new JPanel();

        jPanel.setLayout(new GridLayout());


        return jPanel;
    }


    private Component initCameraPane() {
        webcam = Webcam.getWebcams().get(0);
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        // get image from camera
        // webcam.getImage();

        WebcamPanel panel = new WebcamPanel(webcam);

        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);

        return panel;
    }


    @Override
    protected void initListener() {
        decodePanel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = decodePanel.getSelectedIndex();
                JPanel selectedComponent = (JPanel) decodePanel.getSelectedComponent();
                if (selectedIndex == 0) {
                    if (webcam != null && webcam.isOpen()) {
                        webcam.close();
                    }
                    selectedComponent.add(initUploadPane());
                } else if (selectedIndex == 1) {
                    selectedComponent.add(initCameraPane());
                }
            }
        });

    }

    @Override
    public void dispose() {
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
        super.dispose();
    }
}
