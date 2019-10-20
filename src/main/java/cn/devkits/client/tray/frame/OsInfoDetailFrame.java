package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.time.Instant;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

public class OsInfoDetailFrame extends DKAbstractFrame {

    private static final long serialVersionUID = 6295111163819170866L;
    private SystemInfo si = new SystemInfo();


    public OsInfoDetailFrame() {
        super("System Details", 1.2f);

        initUI(getRootPane());
        initListener();
    }

    @Override
    protected void initUI(JRootPane jRootPane) {
        jRootPane.setLayout(new BorderLayout());
        jRootPane.setBorder(new EmptyBorder(5, 5, 0, 5));

        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab("Dashboard", initDashboard());
        jTabbedPane.addTab("OS Detail", initOsInfo());
        jTabbedPane.addTab("CPU", initDashboard());
        jTabbedPane.addTab("Main Board", initDashboard());
        jTabbedPane.addTab("Memory", initDashboard());
        jTabbedPane.addTab("Disk", initDashboard());
        jTabbedPane.addTab("Sensors", initDashboard());
        jTabbedPane.addTab("Displays", initDashboard());
        jTabbedPane.addTab("Power", initDashboard());
        jTabbedPane.addTab("Sound Cards", initDashboard());
        jTabbedPane.addTab("USB Devices", initDashboard());

        jTabbedPane.setFocusable(false);// 不显示选项卡上的焦点虚线边框

        jRootPane.add(jTabbedPane, BorderLayout.CENTER);
    }

    private Component initOsInfo() {
        return new JLabel();
    }

    private JLabel initDashboard() {
        OperatingSystem os = si.getOperatingSystem();

        StringBuilder sb = new StringBuilder("<html><body>");
        sb.append(String.valueOf(os));
        sb.append("<br><br>");

        sb.append("Booted: " + Instant.ofEpochSecond(os.getSystemBootTime()));
        sb.append("<br>");
        sb.append("Uptime: " + FormatUtil.formatElapsedSecs(os.getSystemUptime()));
        sb.append("<br><br>");

        sb.append("OS Family: " + os.getFamily().intern());
        sb.append("<br>");
        sb.append("Bitness : " + os.getBitness());
        sb.append("<br>");
        sb.append("Manufacturer : " + os.getManufacturer());
        sb.append("<br>");
        sb.append("System Version : " +  os.getVersion());
        sb.append("<br>");
        sb.append("Process Running: " + os.getProcessCount());
        sb.append("<br>");
        sb.append("Threads  Running: " + os.getThreadCount());
        sb.append("<br><br>");

        sb.append("Running with" + (os.isElevated() ? "" : "out") + " elevated permissions.");
        sb.append("</body></html>");

        return new JLabel(sb.toString());
    }

    @Override
    protected void initListener() {
        // TODO Auto-generated method stub

    }

}
