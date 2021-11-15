/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.tray.frame.assist.BrowserActionListener;
import cn.devkits.client.util.DKSystemUIUtil;
import cn.devkits.client.util.DKSystemUtil;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 编解码应用：二维码、条形码
 *
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年12月19日 下午10:18:50
 */
public class QrCodeFrame extends DKAbstractFrame implements Runnable, DKFrameChosenable, ThreadFactory {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4030282787993924346L;
    private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeFrame.class);
    private static final Dimension CAMERA_DIMENSION = WebcamResolution.VGA.getSize();

    //整体布局
    private Container jRootPane;
    private CardLayout cardLayout;

    //编码控件
    private JTabbedPane encodePanel;

    //解码控件
    private JTabbedPane decodePanel;
    private JTextField uploadTextField;
    private JButton uploadBtn;
    private JLabel imgPreviewLabel;
    private JTextArea console;
    private JPanel cameraPanel;
    private Webcam webcam;
    private JTextField siteTextField;
    private JButton siteBtn;
    private JTextArea siteConsole;

    private Executor executor = Executors.newSingleThreadExecutor(this);

    public QrCodeFrame() {
        super(DKSystemUIUtil.getLocaleString("QR_CODE"), (int) CAMERA_DIMENSION.getWidth(), (int) CAMERA_DIMENSION.getHeight());

        initUI(getContentPane());
        initListener();
        initMenuaBr();

        executor.execute(this);
    }

    private void initMenuaBr() {
        JMenuBar mb = new JMenuBar();
        JMenu fileMenu = new JMenu(DKSystemUIUtil.getLocaleString("COMMON_MENU_FILE"));

        String decodeName = DKSystemUIUtil.getLocaleString("QR_MENUITEM_DECODE");
        JMenuItem decodeMenuItem = new JMenuItem(decodeName);
        Icon qrcodeIcon = IconFontSwing.buildIcon(FontAwesome.QRCODE, 16, new Color(50, 50, 50));
        decodeMenuItem.setIcon(qrcodeIcon);
        decodeMenuItem.addActionListener(e -> {
            switchPane(decodeName);
        });
        fileMenu.add(decodeMenuItem);

        String encodeName = DKSystemUIUtil.getLocaleString("QR_MENUITEM_ENCODE");
        JMenuItem encodeMenuItem = new JMenuItem(encodeName);
        fileMenu.add(encodeMenuItem);
        encodeMenuItem.addActionListener(e -> {
            switchPane(encodeName);
        });
        fileMenu.addSeparator();

        Icon quitIcon = IconFontSwing.buildIcon(FontAwesome.SIGN_OUT, 16, new Color(50, 50, 50));
        JMenuItem quitMenuItem = new JMenuItem(DKSystemUIUtil.getLocaleString("COMMON_MENU_QUIT"));
        quitMenuItem.setIcon(quitIcon);
        quitMenuItem.addActionListener(e -> {
            this.setVisible(false);
        });
        fileMenu.add(quitMenuItem);

        mb.add(fileMenu);
        setJMenuBar(mb);
    }

    /**
     * 编码解码切换
     *
     * @param paneName 容器名称
     */
    private void switchPane(String paneName) {
        cardLayout.show(jRootPane, paneName);
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

            cameraPanel = camPanel;
        } else {
            cameraPanel = new JPanel(new BorderLayout());
            Icon leftIcon = IconFontSwing.buildIcon(FontAwesome.CAMERA, 16, new Color(50, 50, 50));
            cameraPanel.add(new JLabel(DKSystemUIUtil.getLocaleString("NO_CAMERA_FOUND"), leftIcon, SwingConstants.CENTER));
        }
    }


    @Override
    protected void initUI(Container jRootPane) {
        this.cardLayout = new CardLayout();
        this.jRootPane = jRootPane;
        jRootPane.setLayout(cardLayout);

        initCamePanel();

        this.decodePanel = new JTabbedPane();
        decodePanel.addTab(DKSystemUIUtil.getLocaleString("QR_UPLOAD"), initUploadPane());
        decodePanel.addTab(DKSystemUIUtil.getLocaleString("QR_CAMERA"), cameraPanel);
        decodePanel.addTab(DKSystemUIUtil.getLocaleString("QR_SITE"), initSitePane());

        this.encodePanel = new JTabbedPane();

        encodePanel.addTab("Text", new JLabel());
        encodePanel.addTab("URL", new JLabel());
        encodePanel.addTab("Profile", new JLabel());

        // 不显示选项卡上的焦点虚线边框
        decodePanel.setFocusable(false);
        encodePanel.setFocusable(false);

        jRootPane.add(decodePanel, DKSystemUIUtil.getLocaleString("QR_MENUITEM_DECODE"));
        jRootPane.add(encodePanel, DKSystemUIUtil.getLocaleString("QR_MENUITEM_ENCODE"));
    }

    private Component initSitePane() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        SpringLayout springLayout = new SpringLayout();
        topPanel.setLayout(springLayout);

        this.siteTextField = new JTextField(100);
        this.siteBtn = new JButton(DKSystemUIUtil.getLocaleString("QR_START_DECODE"));

        topPanel.add(siteTextField);
        topPanel.add(siteBtn);

        layoutInputPanel(topPanel, springLayout, siteTextField, siteBtn);

        jPanel.add(BorderLayout.PAGE_START, topPanel);
        this.siteConsole = new JTextArea();
        jPanel.add(BorderLayout.CENTER, siteConsole);

        return jPanel;
    }

    private Component initUploadPane() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        SpringLayout springLayout = new SpringLayout();
        topPanel.setLayout(springLayout);

        this.uploadTextField = new JTextField(100);
        this.uploadTextField.setToolTipText("待识别文件或路径，输入后回车识别！");
        Icon uploadIcon = IconFontSwing.buildIcon(FontAwesome.UPLOAD, 16, new Color(50, 50, 50));
        this.uploadBtn = new JButton(DKSystemUIUtil.getLocaleString("QR_START_UPLOAD"), uploadIcon);

        topPanel.add(uploadTextField);
        topPanel.add(uploadBtn);

        layoutInputPanel(topPanel, springLayout, uploadTextField, uploadBtn);

        jPanel.add(BorderLayout.PAGE_START, topPanel);
        this.console = new JTextArea();

        Box horizontalBox = Box.createHorizontalBox();

        JPanel imagePreviewPane = new JPanel();
        imagePreviewPane.setBorder(BorderFactory.createTitledBorder("图片预览窗口"));
        imagePreviewPane.setPreferredSize(new Dimension((int) (CAMERA_DIMENSION.getWidth() * 0.4), (int) CAMERA_DIMENSION.getHeight()));
        imgPreviewLabel = new JLabel("图片预览区");
        imagePreviewPane.add(imgPreviewLabel);

        horizontalBox.add(imagePreviewPane);
        horizontalBox.add(console);

        jPanel.add(BorderLayout.CENTER, horizontalBox);

        return jPanel;
    }


    private void layoutInputPanel(JPanel topPanel, SpringLayout springLayout, JTextField jTextField, JButton uploadBtn) {
        // Adjust constraints for the label so it's at (5,5).
        SpringLayout.Constraints labelCons = springLayout.getConstraints(jTextField);
        labelCons.setX(Spring.constant(DKSystemUIUtil.COMPONENT_UI_PADDING_5));
        labelCons.setY(Spring.constant(DKSystemUIUtil.COMPONENT_UI_PADDING_5));

        // Adjust constraints for the text field so it's at
        // (<label's right edge> + 5, 5).
        SpringLayout.Constraints textFieldCons = springLayout.getConstraints(uploadBtn);
        textFieldCons.setX(Spring.sum(Spring.constant(DKSystemUIUtil.COMPONENT_UI_PADDING_5), labelCons.getConstraint(SpringLayout.EAST)));
        textFieldCons.setY(Spring.constant(DKSystemUIUtil.COMPONENT_UI_PADDING_5));

        // Adjust constraints for the content pane.
        DKSystemUIUtil.setContainerSize(topPanel, DKSystemUIUtil.COMPONENT_UI_PADDING_5);
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
                        WebcamPanel webcamP = (WebcamPanel) cameraPanel;
                        if (webcamP.isStarted()) {
                            webcamP.stop();
                        }
                    }
                }
            }
        });

        uploadTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    callback();
                }
            }
        });

        FileFilter[] filters = new FileFilter[]{DKSystemUIUtil.createFileFilter("Graphics Interchange Format", true, "gif"), DKSystemUIUtil.createFileFilter("JPEG Compge Files", true, "jpg"),
                DKSystemUIUtil.createFileFilter("Portable Network Graphics", true, "png")};

        uploadBtn.addActionListener(new BrowserActionListener(this, filters, "QR file", JFileChooser.FILES_AND_DIRECTORIES, true));

        siteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (siteTextField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Decode URL can not be empty!", "URL Empty Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                decodeImgsFromOnline(siteTextField.getText());
            }
        });
    }

    private void decodeImgsFromOnline(String text) {
        Connection connect = Jsoup.connect(text);
        try {
            Document document = connect.get();
            Elements imgs = document.getElementsByTag("img");
            Set<String> urls = new HashSet<String>();
            for (Element element : imgs) {
                urls.add(element.attr("abs:src"));
            }
            siteConsole.append("There are " + urls.size() + " images in site: " + text + System.lineSeparator());
            decodeImgs(urls);
        } catch (MalformedURLException e) {
            LOGGER.error("Request URL is not a HTTP or HTTPS URL, or is otherwise malformed: {}", text);
            JOptionPane.showMessageDialog(null, "Request URL is not a HTTP or HTTPS URL, or is otherwise malformed!", "Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (HttpStatusException e) {
            LOGGER.error("The response is not OK and HTTP response errors are not ignored！");
            JOptionPane.showMessageDialog(null, "The response is not OK and HTTP response errors are not ignored！", "Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (UnsupportedMimeTypeException e) {
            LOGGER.error("The response mime type is not supported and those errors are not ignored！");
            JOptionPane.showMessageDialog(null, "The response mime type is not supported and those errors are not ignored！", "Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (SocketTimeoutException e) {
            LOGGER.error("The connection times out！");
            JOptionPane.showMessageDialog(null, "The connection times out！", "Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            LOGGER.error("Request URL occurred an error: {}", e.getMessage());
            JOptionPane.showMessageDialog(null, "Request URL occurred an error！", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void decodeImgs(Set<String> urls) {
        for (String imgUrl : urls) {
            BufferedImage bufferedImage;
            try {
                bufferedImage = ImageIO.read(new URL(imgUrl));
                Optional<Result> decodeBufferedImage = decodeBufferedImage(bufferedImage);
                if (decodeBufferedImage.isPresent()) {
                    Result result = decodeBufferedImage.get();
                    String text = result.getText();
                    siteConsole.append("Image URL：" + imgUrl + System.lineSeparator());
                    siteConsole.append("Result：" + text + System.lineSeparator() + System.lineSeparator());
                }
            } catch (IOException e) {
                LOGGER.error("Error occurs during reading online file {}", imgUrl);
            }
        }
    }


    /**
     * 文件上传成功以后自动扫描二维码内容
     */
    @Override
    public void callback() {
        String filePath = uploadTextField.getText();
        if (filePath != null) {
            File file = new File(filePath);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    extractQrOfImg(f);
                    console.append(System.lineSeparator());
                }
            } else {
                extractQrOfImg(file);
            }
        }
    }

    private void extractQrOfImg(File f) {
        imgPreviewLabel.setIcon(new ImageIcon(f.getAbsolutePath()));

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(f);
            Optional<Result> decodeBufferedImage = decodeBufferedImage(bufferedImage);
            if (decodeBufferedImage.isPresent()) {
                Result result = decodeBufferedImage.get();
                console.append("Decode With UTF-8. " + System.lineSeparator());
                console.append("Result：" + result.toString() + System.lineSeparator());
                console.append("QR Format Type：" + result.getBarcodeFormat() + System.lineSeparator());
                console.append("QR Text Content：" + result.getText() + System.lineSeparator());
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("Input stream is null!");
        } catch (IOException e1) {
            LOGGER.error("Error occurs during reading file {}", f.getAbsolutePath());
        } finally {
            bufferedImage.flush();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Optional<Result> decodeBufferedImage(BufferedImage bufferedImage) {
        try {
            /**
             * com.google.zxing.client.j2se.BufferedImageLuminanceSource：缓冲图像亮度源 将 java.awt.image.BufferedImage
             * 转为 zxing 的 缓冲图像亮度源 关键就是下面这几句：HybridBinarizer 用于读取二维码图像数据，BinaryBitmap 二进制位图
             */
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Hashtable hints = new Hashtable();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            /**
             * 如果图片不是二维码图片，则 decode 抛异常：com.google.zxing.NotFoundException MultiFormatWriter 的 encode 用于对内容进行编码成
             * 2D 矩阵 MultiFormatReader 的 decode 用于读取二进制位图数据
             */
            return Optional.of(new MultiFormatReader().decode(bitmap, hints));
        } catch (NotFoundException e) {
            LOGGER.error(" Any errors which occurred...");
        }
        return Optional.empty();
    }


    @Override
    public void updateSelectFilePath(String absolutePath) {
        uploadTextField.setText(absolutePath);
    }

    @Override
    public Component getObj() {
        return this;
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
                    } catch (NotFoundException e) {
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
