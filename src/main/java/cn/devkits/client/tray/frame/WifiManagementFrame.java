package cn.devkits.client.tray.frame;

import cn.devkits.client.tray.frame.listener.SsidListViewSelectionListener;
import cn.devkits.client.util.DKSysUIUtil;
import cn.devkits.client.util.DKSysUtil;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * WIFI管理窗口
 *
 * @author Shaofeng Liu
 * @Date 2022/03/11
 */
public class WifiManagementFrame extends DKAbstractFrame {
    private static final float PANE_WIDTH_L = 0.6f;
    private static final float PANE_WIDTH_R = 1 - PANE_WIDTH_L;
    private static final JLabel currentName = new JLabel("N/A");
    private static final JLabel currentPwd = new JLabel("N/A");
    private static final JLabel currentOnline = new JLabel("N/A");
    private static final JLabel currentQr = new JLabel();

    private JList<String> listView;

    private Set<String> connectedSsids;
    private Map<String, Map<String, String>> availableSsids;

    /**
     * 构造方法
     */
    public WifiManagementFrame() {
        super(DKSysUIUtil.getLocale("SSID_MANAGEMENT_FRAME_TITLE"), 0.7f);

        initUI(getDKPane());
        initListener();
    }


    @Override
    protected void initUI(Container rootContainer) {
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(createWifiListPane());
        horizontalBox.add(createDetailPane());

        rootContainer.add(BorderLayout.CENTER, horizontalBox);
        rootContainer.add(BorderLayout.SOUTH, createBottomPane());
    }

    private Component createDetailPane() {
        JPanel imagePreviewPane = new JPanel();
        imagePreviewPane.setLayout(new BorderLayout());
        imagePreviewPane.setBorder(BorderFactory.createTitledBorder(DKSysUIUtil.getLocale("SSID_MANAGEMENT_DETAIL_INFO_TITLE")));
        imagePreviewPane.setPreferredSize(new Dimension((int) (getWidth() * WifiManagementFrame.PANE_WIDTH_R), getHeight()));

        FormLayout layout = new FormLayout("left:pref, 4dlu, left:pref:grow, 4dlu", "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 10dlu, p, 4dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();

        builder.addSeparator(DKSysUIUtil.getLocale("SSID_MANAGEMENT_DETAIL_INFO"), cc.xyw(1, 1, 4));

        builder.addLabel(DKSysUIUtil.getLocaleWithColon("SSID_MANAGEMENT_DETAIL_SSID_NAME"), cc.xy(1, 3));
        builder.add(currentName, cc.xy(3, 3));
        builder.addLabel(DKSysUIUtil.getLocaleWithColon("SSID_MANAGEMENT_DETAIL_SSID_PWD"), cc.xy(1, 5));
        builder.add(currentPwd, cc.xy(3, 5));
        builder.addLabel(DKSysUIUtil.getLocaleWithColon("SSID_MANAGEMENT_DETAIL_SSID_SIGNAL"), cc.xy(1, 7));
        builder.add(currentOnline, cc.xy(3, 7));

        builder.addSeparator(DKSysUIUtil.getLocale("SSID_MANAGEMENT_DETAIL_QR"), cc.xyw(1, 9, 4));
        builder.add(currentQr, cc.xyw(1, 11, 4));

        imagePreviewPane.add(builder.getPanel());
        return imagePreviewPane;
    }

    private Component createBottomPane() {
        Map<String, String> currentSsid = currentSystemUtil.getCurrentSsid();
        if (currentSsid.isEmpty()) {
            return DKSysUIUtil.createLabelWithTextColor(DKSysUIUtil.getLocale("SSID_MANAGEMENT_NO_NETWORK"), Color.RED);
        } else {
            String ssid = currentSsid.get("SSID");
            currentName.setText(ssid);
            String pwd = currentSystemUtil.getPwdOfSsid(ssid);
            currentPwd.setText(pwd);
            currentOnline.setText(currentSsid.get("信号"));
            currentQr.setIcon(generateQrImg(ssid, pwd));

            StringBuilder sb = new StringBuilder();
            sb.append("SSID").append(": ").append(ssid).append("  |  ");
            sb.append("物理地址").append(": ").append(currentSsid.get("物理地址")).append("  |  ");
            sb.append("信号").append(": ").append(currentSsid.get("信号")).append("  |  ");
            sb.append("传输速率 (Mbps)").append(": ").append(currentSsid.get("传输速率 (Mbps)"));

            return DKSysUIUtil.createLabelWithTextColor(sb.toString(),Color.GREEN);
        }
    }

    private Component createWifiListPane() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.setBorder(BorderFactory.createTitledBorder(DKSysUIUtil.getLocale("SSID_MANAGEMENT_SSID_LIST_LABEL")));

        listView = new JList<>(getSsidList());
        jPanel.add(new JScrollPane(listView), BorderLayout.CENTER);

        jPanel.setPreferredSize(new Dimension((int) (getWidth() * WifiManagementFrame.PANE_WIDTH_L), getHeight()));
        return jPanel;
    }

    private Vector<String> getSsidList() {
        connectedSsids = currentSystemUtil.getSsidNamesOfConnected();
        availableSsids = currentSystemUtil.getAvailableSsids();

        connectedSsids.addAll(availableSsids.keySet());
        return new Vector<>(connectedSsids);
    }

    @Override
    protected void initListener() {
        listView.addListSelectionListener(new SsidListViewSelectionListener(this));
    }

    public void updateSsidDetail(String selectedValue) {
        currentName.setText(selectedValue);
        String pwd = currentSystemUtil.getPwdOfSsid(selectedValue);
        currentPwd.setText(pwd);
        currentOnline.setText(getSignalOfSsid(selectedValue));
        currentQr.setIcon(generateQrImg(selectedValue, pwd));
    }

    private String getSignalOfSsid(String ssid) {
        if (availableSsids.containsKey(ssid)) {
            Map<String, String> map = availableSsids.get(ssid);
            if (map.containsKey("信号")) {
                return map.get("信号");
            }

            if (map.containsKey("Signal")) {
                return map.get("Signal");
            }
        }
        return "0%";
    }

    private ImageIcon generateQrImg(String name, String pwd) {
        if (connectedSsids.contains(name)) {
            //https://blog.csdn.net/jeffasd/article/details/50129621
            String qrTxt = "WIFI:T:WPA;S:" + name + ";P:" + pwd + ";;";
            int qrWidth = (int) (getWidth() * PANE_WIDTH_R * 0.75);
            BufferedImage bufferedImage = DKSysUtil.generateQrImg(qrTxt, qrWidth, qrWidth);
            assert bufferedImage != null;
            return new ImageIcon(bufferedImage);
        } else {
            return null;
        }
    }
}
