package cn.devkits.client.tray.frame;

import cn.devkits.client.util.DKSystemUIUtil;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * WIFI管理窗口
 *
 * @author Shaofeng Liu
 * @Date 2022/03/11
 */
public class WifiManagementFrame extends DKAbstractFrame {
    private float PANE_WIDTH_L = 0.6f;
    private float PANE_WIDTH_R = 1 - PANE_WIDTH_L;

    private JList<String> view = null;
    private JLabel currentName = new JLabel("N/A");
    private JLabel currentPwd = new JLabel("N/A");
    private JLabel currentOnline = new JLabel("N/A");
    private JLabel currentQr = new JLabel();

    /**
     * 构造方法
     */
    public WifiManagementFrame() {
        super(DKSystemUIUtil.getLocaleString("SSID_MANAGEMENT_FRAME_TITLE"), 0.7f);

        initUI(getContentPane());
        initListener();
    }

    @Override
    protected void initUI(Container rootContainer) {
        rootContainer.setLayout(new BorderLayout());

        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(createWifiListPane(PANE_WIDTH_L));
        horizontalBox.add(createDetailPane(PANE_WIDTH_R));

        rootContainer.add(BorderLayout.CENTER, horizontalBox);
        rootContainer.add(BorderLayout.SOUTH, createBottomPane());
    }

    private Component createDetailPane(float width) {
        JPanel imagePreviewPane = new JPanel();
        imagePreviewPane.setLayout(new BorderLayout());
        imagePreviewPane.setBorder(BorderFactory.createTitledBorder(DKSystemUIUtil.getLocaleString("SSID_MANAGEMENT_DETAIL_INFO_TITLE")));
        imagePreviewPane.setPreferredSize(new Dimension((int) (getWidth() * width), getHeight()));

        FormLayout layout = new FormLayout(
                "left:pref, 4dlu, left:pref:grow, 4dlu",
                "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 10dlu, p, 4dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();

        builder.addSeparator(DKSystemUIUtil.getLocaleString("SSID_MANAGEMENT_DETAIL_INFO"), cc.xyw(1, 1, 4));

        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SSID_MANAGEMENT_DETAIL_SSID_NAME"), cc.xy(1, 3));
        builder.add(currentName, cc.xy(3, 3));
        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SSID_MANAGEMENT_DETAIL_SSID_PWD"), cc.xy(1, 5));
        builder.add(currentPwd, cc.xy(3, 5));
        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SSID_MANAGEMENT_DETAIL_SSID_ONLINE"), cc.xy(1, 7));
        builder.add(currentOnline, cc.xy(3, 7));

        builder.addSeparator(DKSystemUIUtil.getLocaleString("SSID_MANAGEMENT_DETAIL_QR"), cc.xyw(1, 9, 4));
        builder.add(currentQr, cc.xyw(1, 11, 4));

        imagePreviewPane.add(builder.getPanel());
        return imagePreviewPane;
    }

    private Component createBottomPane() {
        Map<String, String> currentSsid = currentSystemUtil.getCurrentSsid();
        if (currentSsid.isEmpty()) {
            return DKSystemUIUtil.createLabelWithRedText(DKSystemUIUtil.getLocaleString("SSID_MANAGEMENT_NO_NETWORK"));
        } else {
            String ssid = currentSsid.get("SSID");
            currentName.setText(ssid);
            String pwd = currentSystemUtil.getPwdOfSsid(ssid);
            currentPwd.setText(pwd);
            currentOnline.setText("Yes");
            currentQr.setIcon(generateQrImg(ssid, pwd));

            StringBuilder sb = new StringBuilder("【当前WIFI】");
            sb.append("SSID").append(": ").append(ssid).append("  |  ");
            sb.append("物理地址").append(": ").append(currentSsid.get("物理地址")).append("  |  ");
            sb.append("信号").append(": ").append(currentSsid.get("信号")).append("  |  ");
            sb.append("传输速率 (Mbps)").append(": ").append(currentSsid.get("传输速率 (Mbps)"));

            return DKSystemUIUtil.createLabelWithGreenText(sb.toString());
        }
    }

    private Component createWifiListPane(float width) {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.setBorder(BorderFactory.createTitledBorder(DKSystemUIUtil.getLocaleString("SSID_MANAGEMENT_ONCE_CONNECTED_LABEL")));

        view = new JList<>(getSsidList());
        jPanel.add(new JScrollPane(view), BorderLayout.CENTER);

        jPanel.setPreferredSize(new Dimension((int) (getWidth() * width), (int) getHeight()));
        return jPanel;
    }

    private Vector<String> getSsidList() {
        List<String> wifiNamesOfConnected = currentSystemUtil.getSsidNamesOfConnected();
        return new Vector<>(wifiNamesOfConnected);
    }

    @Override
    protected void initListener() {
        view.addListSelectionListener(e -> {
            if (!view.getValueIsAdjusting()) {
                String selectedValue = view.getSelectedValue();
                currentName.setText(selectedValue);
                String pwd = currentSystemUtil.getPwdOfSsid(selectedValue);
                currentPwd.setText(pwd);
                currentOnline.setText("N/A");
                currentQr.setIcon(generateQrImg(selectedValue, pwd));
            }
        });
    }

    private ImageIcon generateQrImg(String name, String pwd) {
        //https://blog.csdn.net/jeffasd/article/details/50129621
        String qrTxt = "WIFI:T:WPA;S:" + name + ";P:" + pwd + ";;";
        int qrWidth = (int) (getWidth() * PANE_WIDTH_R * 0.75);
        BufferedImage bufferedImage = currentSystemUtil.generateQrImg(qrTxt, qrWidth, qrWidth);
        return new ImageIcon(bufferedImage);
    }
}
