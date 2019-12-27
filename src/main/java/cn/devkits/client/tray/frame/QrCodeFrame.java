package cn.devkits.client.tray.frame;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.ibatis.javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import cn.devkits.client.util.DKSystemUtil;

/**
 * 
 * 二维码
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年12月19日 下午10:18:50
 */
public class QrCodeFrame extends DKAbstractFrame implements Runnable, ThreadFactory {

    /** serialVersionUID */
    private static final long serialVersionUID = -4030282787993924346L;
    private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeFrame.class);

    private Webcam webcam = null;
    private JTabbedPane decodePanel;
    private Executor executor = Executors.newSingleThreadExecutor(this);

    public QrCodeFrame() {
        super("QR Code", 0.7f, 0.7f);

        initUI(getRootPane());
        initListener();

        executor.execute(this);
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

        decodePanel.setFocusable(false);
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
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

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

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOGGER.info("Sleep failed in recognize QR code...");
            }

            if (webcam != null) {
                BufferedImage image = null;

                if (webcam.isOpen()) {
                    if ((image = webcam.getImage()) == null) {
                        continue;
                    }

                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                    try {
                        Result result = new MultiFormatReader().decode(bitmap);
                        if (result != null) {
                            DKSystemUtil.playSound(DKSystemUtil.SOUND_TYPE_SCAN);
                            String resultTxt = "<html>Recognized result as below has been set to the clipboard:<br/><span style='color:RED'>";

                            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                            Transferable tText = new StringSelection(result.getText());
                            clip.setContents(tText, null);

                            JOptionPane.showMessageDialog(this, resultTxt + result.getText() + "</span></html>", "QR Recognize Result", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (com.google.zxing.NotFoundException e) {
                        LOGGER.info("Recognize QR Code from camera...");
                    }
                }
            }
        } while (true);
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "example-runner");
        t.setDaemon(true);
        return t;
    }
}
