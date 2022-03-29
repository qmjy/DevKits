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
    private static JLabel currentName = new JLabel("N/A");
    private static JLabel currentPwd = new JLabel("N/A");
    private static JLabel currentOnline = new JLabel("N/A");
    private static JLabel currentQr = new JLabel();

    private JList<String> listView;

    private List<String> connectedSsids;
    private Map<String, Map<String, String>> availableSsids;

    /**
     * 构造方法
     */
    public WifiManagementFrame() {
        super(DKSysUIUtil.getLocaleString("SSID_MANAGEMENT_FRAME_TITLE"), 0.7f);
    }

    @Override
    protected void initData() {
        connectedSsids = currentSystemUtil.getSsidNamesOfConnected();
        availableSsids = currentSystemUtil.getAvailableSsids();
    }

    @Override
    protected void initUI(Container rootContainer) {
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(createWifiListPane(PANE_WIDTH_L));
        horizontalBox.add(createDetailPane(PANE_WIDTH_R));

        rootContainer.add(BorderLayout.CENTER, horizontalBox);
        rootContainer.add(BorderLayout.SOUTH, createBottomPane());
    }

    private Component createDetailPane(float width) {
        JPanel imagePreviewPane = new JPanel();
        imagePreviewPane.setLayout(new BorderLayout());
        imagePreviewPane.setBorder(BorderFactory.createTitledBorder(DKSysUIUtil.getLocaleString("SSID_MANAGEMENT_DETAIL_INFO_TITLE")));
        imagePreviewPane.setPreferredSize(new Dimension((int) (getWidth() * width), getHeight()));

        FormLayout layout = new FormLayout(
                "left:pref, 4dlu, left:pref:grow, 4dlu",
                "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 10dlu, p, 4dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();

        builder.addSeparator(DKSysUIUtil.getLocaleString("SSID_MANAGEMENT_DETAIL_INFO"), cc.xyw(1, 1, 4));

        builder.addLabel(DKSysUIUtil.getLocaleStringWithColon("SSID_MANAGEMENT_DETAIL_SSID_NAME"), cc.xy(1, 3));
        builder.add(currentName, cc.xy(3, 3));
        builder.addLabel(DKSysUIUtil.getLocaleStringWithColon("SSID_MANAGEMENT_DETAIL_SSID_PWD"), cc.xy(1, 5));
        builder.add(currentPwd, cc.xy(3, 5));
        builder.addLabel(DKSysUIUtil.getLocaleStringWithColon("SSID_MANAGEMENT_DETAIL_SSID_SIGNAL"), cc.xy(1, 7));
        builder.add(currentOnline, cc.xy(3, 7));

        builder.addSeparator(DKSysUIUtil.getLocaleString("SSID_MANAGEMENT_DETAIL_QR"), cc.xyw(1, 9, 4));
        builder.add(currentQr, cc.xyw(1, 11, 4));

        imagePreviewPane.add(builder.getPanel());
        return imagePreviewPane;
    }

    private Component createBottomPane() {
        Map<String, String> currentSsid = currentSystemUtil.getCurrentSsid();
        if (currentSsid.isEmpty()) {
            return DKSysUIUtil.createLabelWithRedText(DKSysUIUtil.getLocaleString("SSID_MANAGEMENT_NO_NETWORK"));
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

            return DKSysUIUtil.createLabelWithGreenText(sb.toString());
        }
    }

    private Component createWifiListPane(float width) {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.setBorder(BorderFactory.createTitledBorder(DKSysUIUtil.getLocaleString("SSID_MANAGEMENT_SSID_LIST_LABEL")));

        listView = new JList<>(getSsidList());
        jPanel.add(new JScrollPane(listView), BorderLayout.CENTER);

        jPanel.setPreferredSize(new Dimension((int) (getWidth() * width), getHeight()));
        return jPanel;
    }

    private Vector<String> getSsidList() {
        List<String> objects = new ArrayList<>();
        objects.addAll(connectedSsids);

        Iterator<String> iterator = availableSsids.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (objects.contains(next)) {
                objects.add(next);
            }
        }
        return new Vector<>(objects);
    }

    @Override
    protected void initListener() {
        listView.addListSelectionListener(new SsidListViewSelectionListener(this));
    }

    public  void updateSsidDetail(String selectedValue){
        currentName.setText(selectedValue);
        String pwd = currentSystemUtil.getPwdOfSsid(selectedValue);
        currentPwd.setText(pwd);
        currentOnline.setText(getSignalOfSsid(selectedValue));
        currentQr.setIcon(generateQrImg(selectedValue, pwd));
    }

    private String getSignalOfSsid(String ssid) {
        if (availableSsids.containsKey(ssid)) {
            return availableSsids.get(ssid).get("信号");
        } else {
            return "0%";
        }
    }

    private ImageIcon generateQrImg(String name, String pwd) {
        if (connectedSsids.contains(name)) {
            //https://blog.csdn.net/jeffasd/article/details/50129621
            String qrTxt = "WIFI:T:WPA;S:" + name + ";P:" + pwd + ";;";
            int qrWidth = (int) (getWidth() * PANE_WIDTH_R * 0.75);
            BufferedImage bufferedImage = DKSysUtil.generateQrImg(qrTxt, qrWidth, qrWidth);
            return new ImageIcon(bufferedImage);
        } else {
            return null;
        }
    }
}
