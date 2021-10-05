/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.component.osinfo.FileStorePanel;
import cn.devkits.client.component.osinfo.MemoryPanel;
import cn.devkits.client.component.osinfo.ProcessPanel;
import cn.devkits.client.component.osinfo.ProcessorPanel;
import cn.devkits.client.util.DKSystemUIUtil;
import cn.devkits.client.util.DKSystemUtil;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.Instant;
import java.util.List;

import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.hardware.SoundCard;
import oshi.hardware.UsbDevice;
import oshi.software.os.NetworkParams;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

/**
 * 当前计算机软硬件信息展示
 *
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年10月21日 下午8:23:09
 */
public class OsInfoDetailFrame extends DKAbstractFrame {

    private static final long serialVersionUID = 6295111163819170866L;
    private JTabbedPane jTabbedPane;
    private SystemInfo si = new SystemInfo();

    public OsInfoDetailFrame() {
        super(DKSystemUIUtil.getLocaleString("SYS_INFO_TITLE"), 0.9f);

        initUI(getContentPane());
        initListener();
    }

    @Override
    protected void initUI(Container rootContainer) {
        JToolBar toolBar = new JToolBar("System Information Toolbar");
        toolBar.setFloatable(false);
        createToolbarBtns(toolBar);

        setPreferredSize(new Dimension(450, 130));
        add(toolBar, BorderLayout.PAGE_START);

        this.jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_DASHBOARD"), initDashboard(si.getHardware(), si.getOperatingSystem()));
        jTabbedPane.addTab("CPU", new JPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_MAINBOARD"), new JPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_MEMORY"), new JPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_DISK"), new JPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_SENSORS"), new JPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_DISPLAYS"), new JPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_NETWORK"), new JPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_SOUNDCARDS"), new JPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_USB_DEVICES"), new JPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_POWER_SOURCES"), new JPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_PROCESSES"), new JPanel());

        // 不显示选项卡上的焦点虚线边框
        jTabbedPane.setFocusable(false);

        rootContainer.add(jTabbedPane, BorderLayout.CENTER);
    }

    private void createToolbarBtns(JToolBar toolBar) {
        JButton systemBtn = new JButton(DKSystemUIUtil.getLocaleString("SYS_INFO_TOOL_BAR_SYS"));
        systemBtn.setFocusable(false);
        systemBtn.addActionListener(e -> {
            DKSystemUtil.openSystemInfoClient("msinfo32");
        });
        toolBar.add(systemBtn);

        JButton dxBtn = new JButton(DKSystemUIUtil.getLocaleString("SYS_INFO_TOOL_BAR_DX"));
        dxBtn.setFocusable(false);
        dxBtn.addActionListener(e -> {
            DKSystemUtil.openSystemInfoClient("dxdiag");
        });
        toolBar.add(dxBtn);
    }


    @Override
    protected void initListener() {
        jTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane pane = (JTabbedPane) e.getSource();
                int tabIndex = pane.getSelectedIndex();
                JPanel container = (JPanel) pane.getSelectedComponent();
                if (container.getComponents().length > 0) {// 面板上已经有组件承载内容显示，无需刷新
                    return;
                }
                SystemInfo si = new SystemInfo();
                switch (tabIndex) {
                    case 1:
                        container.add(new ProcessorPanel(si));
                        break;
                    case 2:
                        container.add(initMainboard());
                        break;
                    case 3:
                        container.add(new MemoryPanel(si));
                        break;
                    case 4:
                        container.add(new FileStorePanel(si));
                        break;
                    case 5:
                        container.add(initSensors(OsInfoDetailFrame.this.si.getHardware()));
                        break;
                    case 6:
                        container.add(initDisplay(OsInfoDetailFrame.this.si.getHardware()));
                        break;
                    case 7:
                        container.add(initNetwork(OsInfoDetailFrame.this.si.getHardware(), OsInfoDetailFrame.this.si.getOperatingSystem()));
                        break;
                    case 8:
                        container.add(initSoundCards(OsInfoDetailFrame.this.si.getHardware()));
                        break;
                    case 9:
                        container.add(initUsb(OsInfoDetailFrame.this.si.getHardware()));
                        break;
                    case 10:
                        container.add(initPower(OsInfoDetailFrame.this.si.getHardware()));
                        break;
                    case 11:
                        container.add(new ProcessPanel(si));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private Component initNetwork(HardwareAbstractionLayer hal, OperatingSystem os) {
        List<NetworkIF> networkIFs = hal.getNetworkIFs();

        StringBuilder sb = new StringBuilder("<html><body>Network Interfaces:<br>");

        if (networkIFs.size() == 0) {
            sb.append(" Unknown");
        }
        for (NetworkIF net : networkIFs) {
            sb.append("<br> ").append(net.toString());
        }

        NetworkParams networkParams = os.getNetworkParams();

        sb.append("<br>Network parameters:<br> " + networkParams.toString());

        sb.append("</body></html>");

        return new JLabel(sb.toString());
    }


    private Component initMainboard() {
        return new JPanel();
    }


    private Component initSensors(HardwareAbstractionLayer hal) {
        Sensors sensors = hal.getSensors();

        StringBuilder sb = new StringBuilder("<html><body>Sensors: <br>");

        sb.append(sensors.toString()).append("<br>");

        sb.append("</body></html>");

        return new JLabel(sb.toString());
    }

    private Component initDisplay(HardwareAbstractionLayer hal) {
        List<Display> displays = hal.getDisplays();

        StringBuilder sb = new StringBuilder("<html><body>Displays: <br>");

        int i = 0;
        for (Display display : displays) {
            sb.append(" Display " + i + ":").append("<br>");
            sb.append(String.valueOf(display));
            i++;
        }

        sb.append("</body></html>");

        return new JLabel(sb.toString());
    }

    private Component initUsb(HardwareAbstractionLayer hal) {
        List<UsbDevice> usbDevices = hal.getUsbDevices(true);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("USB Devices");
        for (UsbDevice usbDevice : usbDevices) {
            appendNodes(root, usbDevice);
        }

        JTree jTree = new JTree(root);

        TreeNode rootNode = (TreeNode) jTree.getModel().getRoot();
        DKSystemUIUtil.expandAll(jTree, new TreePath(rootNode), true);

        return jTree;
    }

    private String appendVenderInfo(UsbDevice usbDevice) {
        if (!usbDevice.getVendor().trim().isEmpty()) {
            return " (" + usbDevice.getVendor() + ")";
        }
        return "";
    }

    private void appendNodes(DefaultMutableTreeNode root, UsbDevice usbDevice) {
        DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(usbDevice.getName() + appendVenderInfo(usbDevice));
        root.add(newChild);

        List<UsbDevice> connectedDevices = usbDevice.getConnectedDevices();
        if (connectedDevices.size() > 0) {
            for (UsbDevice dev : connectedDevices) {
                appendNodes(newChild, dev);
            }
        }
    }

    private Component initSoundCards(HardwareAbstractionLayer hal) {
        List<SoundCard> soundCards = hal.getSoundCards();

        StringBuilder sb = new StringBuilder("<html><body>Sound Cards: <br>");

        for (SoundCard card : soundCards) {
            sb.append(" " + String.valueOf(card)).append("<br>");
        }
        sb.append("</body></html>");

        return new JLabel(sb.toString());
    }

    private Component initPower(HardwareAbstractionLayer hal) {
        List<PowerSource> powerSources = hal.getPowerSources();

        StringBuilder sb = new StringBuilder("<html><body>Power Sources: <br>");
        if (powerSources.size() == 0) {
            sb.append("Unknown<br>");
        }
        for (PowerSource powerSource : powerSources) {
            sb.append("<br> ").append(powerSource.toString());
            sb.append("<br>");
        }
        sb.append("</body></html>");

        return new JLabel(sb.toString());
    }

    private JComponent initDashboard(HardwareAbstractionLayer hal, OperatingSystem os) {
        StringBuilder sb = new StringBuilder("<html><body>");
        sb.append(String.valueOf(os));
        sb.append("<br><br>");

        sb.append("Booted: " + Instant.ofEpochSecond(os.getSystemBootTime()));
        sb.append("<br>");
        sb.append("Uptime: " + FormatUtil.formatElapsedSecs(os.getSystemUptime()));
        sb.append("<br><br>");

        ComputerSystem computerSystem = hal.getComputerSystem();
        computerSystem.getManufacturer();

        sb.append("system: " + computerSystem.toString());
        sb.append("<br>");
        sb.append("firmware: " + computerSystem.getFirmware().toString());
        sb.append("<br>");
        sb.append("baseboard: " + computerSystem.getBaseboard().toString());
        sb.append("<br>");

        sb.append("OS Family: " + os.getFamily().intern());
        sb.append("<br>");
        sb.append("Bitness : " + os.getBitness());
        sb.append("<br>");
        sb.append("Manufacturer : " + os.getManufacturer());
        sb.append("<br>");
        sb.append("Process Running: " + os.getProcessCount());
        sb.append("<br>");
        sb.append("Threads  Running: " + os.getThreadCount());
        sb.append("<br><br>");

        sb.append("Running with" + (os.isElevated() ? "" : "out") + " elevated permissions.");
        sb.append("</body></html>");

        JPanel jPanel = new JPanel(new GridLayout());
        jPanel.add(new JLabel(sb.toString()));

        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jPanel);
        return jScrollPane;
    }
}