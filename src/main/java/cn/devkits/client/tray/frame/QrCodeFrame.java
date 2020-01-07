package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import cn.devkits.client.tray.frame.assist.BrowserActionListener;
import cn.devkits.client.util.DKSystemUIUtil;
import cn.devkits.client.util.DKSystemUtil;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

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
    private static final Dimension CAMERA_DIMENSION = WebcamResolution.VGA.getSize();

    private Webcam webcam;
    private JPanel topPanel;
    private JTabbedPane decodePanel;
    private JButton uploadBtn;
    private JTextField jTextField;
    private JTextArea console;
    private Executor executor = Executors.newSingleThreadExecutor(this);

    public QrCodeFrame() {
        super("QR Code", (int) CAMERA_DIMENSION.getWidth(), (int) CAMERA_DIMENSION.getHeight());

        initUI(getRootPane());
        initListener();

        executor.execute(this);
    }


    private void initCamePanel() {
        // 检测是否有摄像头
        // https://github.com/sarxos/webcam-capture/blob/master/webcam-capture/src/example/java/DetectWebcamExample.java
        webcam = Webcam.getDefault();
        if (webcam != null) {
            webcam.setViewSize(CAMERA_DIMENSION);

            WebcamPanel camPanel = new WebcamPanel(webcam, false);

            camPanel.setFPSDisplayed(true);
            camPanel.setDisplayDebugInfo(true);
            camPanel.setImageSizeDisplayed(true);
            camPanel.setMirrored(true);

            topPanel = camPanel;
        } else {
            topPanel = new JPanel(new BorderLayout());
            Icon leftIcon = IconFontSwing.buildIcon(FontAwesome.CAMERA, 16, new Color(50, 50, 50));
            topPanel.add(new JLabel("No camera device be found！", leftIcon, SwingConstants.CENTER));
        }
    }


    @Override
    protected void initUI(JRootPane jRootPane) {
        CardLayout cardLayout = new CardLayout();
        jRootPane.setLayout(cardLayout);

        initCamePanel();

        this.decodePanel = new JTabbedPane();
        decodePanel.addTab("Upload", initUploadPane());
        decodePanel.addTab("Camera", topPanel);
        decodePanel.addTab("Site", new JLabel());

        JTabbedPane codePanel = new JTabbedPane();

        codePanel.addTab("Text", new JLabel());
        codePanel.addTab("URL", new JLabel());
        codePanel.addTab("Profile", new JLabel());

        decodePanel.setFocusable(false);
        codePanel.setFocusable(false);// 不显示选项卡上的焦点虚线边框

        jRootPane.add(decodePanel);
        // jRootPane.add(codePanel);
    }


    private Component initUploadPane() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        SpringLayout springLayout = new SpringLayout();
        topPanel.setLayout(springLayout);

        this.jTextField = new JTextField(100);
        Icon uploadIcon = IconFontSwing.buildIcon(FontAwesome.UPLOAD, 16, new Color(50, 50, 50));
        this.uploadBtn = new JButton("Upload", uploadIcon);

        topPanel.add(jTextField);
        topPanel.add(uploadBtn);

        layoutInputPanel(topPanel, springLayout, jTextField, uploadBtn);

        jPanel.add(BorderLayout.PAGE_START, topPanel);
        this.console = new JTextArea();
        jPanel.add(BorderLayout.CENTER, console);

        return jPanel;
    }


    private void layoutInputPanel(JPanel topPanel, SpringLayout springLayout, JTextField jTextField, JButton uploadBtn) {
        // Adjust constraints for the label so it's at (5,5).
        SpringLayout.Constraints labelCons = springLayout.getConstraints(jTextField);
        labelCons.setX(Spring.constant(5));
        labelCons.setY(Spring.constant(5));

        // Adjust constraints for the text field so it's at
        // (<label's right edge> + 5, 5).
        SpringLayout.Constraints textFieldCons = springLayout.getConstraints(uploadBtn);
        textFieldCons.setX(Spring.sum(Spring.constant(5), labelCons.getConstraint(SpringLayout.EAST)));
        textFieldCons.setY(Spring.constant(5));

        // Adjust constraints for the content pane.
        DKSystemUIUtil.setContainerSize(topPanel, 5);
    }


    @Override
    protected void initListener() {
        decodePanel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (webcam != null) {
                    if (decodePanel.getSelectedIndex() == 1) {
                        WebcamPanel webcamP = (WebcamPanel) decodePanel.getSelectedComponent();
                        if (!webcamP.isStarted()) {
                            webcamP.start();
                        }
                    } else {
                        WebcamPanel webcamP = (WebcamPanel) topPanel;
                        if (webcamP.isStarted()) {
                            webcamP.stop();
                        }
                    }
                }
            }
        });

        FileFilter[] filters = new FileFilter[] {DKSystemUIUtil.createFileFilter("Graphics Interchange Format", true, "gif"), DKSystemUIUtil.createFileFilter("JPEG Compge Files", true, "jpg"),
                DKSystemUIUtil.createFileFilter("Portable Network Graphics", true, "png")};

        uploadBtn.addActionListener(new BrowserActionListener(this, filters, true));
    }


    /**
     * 文件上传成功以后自动扫描二维码内容
     */
    @Override
    public void callback() {
        String text = jTextField.getText();
        if (text != null && new File(text).isFile()) {
            MultiFormatReader formatReader = new MultiFormatReader();
            BufferedImage bufferedImage = null;
            try {
                // 读取指定的二维码文件
                bufferedImage = ImageIO.read(new File(text));
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
                // 定义二维码参数
                Map hints = new HashMap();
                hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                com.google.zxing.Result result = formatReader.decode(binaryBitmap, hints);
                // 输出相关的二维码信息
                console.append("Decode With UTF-8. " + System.lineSeparator());
                console.append("Result：" + result.toString() + System.lineSeparator());
                console.append("QR Format Type：" + result.getBarcodeFormat() + System.lineSeparator());
                console.append("QR Text Content：" + result.getText() + System.lineSeparator() + System.lineSeparator());

            } catch (IOException e) {
                LOGGER.error("Error occurs during reading file {}", text);
            } catch (NotFoundException e) {
                LOGGER.error("Any errors which occurred when decode code.");
            } finally {
                bufferedImage.flush();
            }
        }
    }


    @Override
    public void updateSelectFilePath(String absolutePath) {
        jTextField.setText(absolutePath);
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
