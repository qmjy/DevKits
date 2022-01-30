/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.component.osinfo.*;
import cn.devkits.client.util.DKSystemUIUtil;
import cn.devkits.client.util.DKSystemUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;
import oshi.util.EdidUtil;
import oshi.util.FormatUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.time.Instant;
import java.util.List;

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
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_DASHBOARD"), initDashboard(si));
        jTabbedPane.addTab("CPU", getPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_MAINBOARD"), getPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_MEMORY"), getPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_DISK"), getPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_SENSORS"), getPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_DISPLAYS"), getPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_NETWORK"), getPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_SOUNDCARDS"), getPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_USB_DEVICES"), getPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_POWER_SOURCES"), getPanel());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("SYS_INFO_TAB_PROCESSES"), getPanel());

        // 不显示选项卡上的焦点虚线边框
        jTabbedPane.setFocusable(false);

        rootContainer.add(jTabbedPane, BorderLayout.CENTER);
    }

    private JPanel getPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        return jPanel;
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
                // 面板上已经有组件承载内容显示，无需刷新
                if (container.getComponents().length > 0) {
                    return;
                }
                SystemInfo si = new SystemInfo();
                switch (tabIndex) {
                    case 1:
                        container.add(new ProcessorPanel(si), BorderLayout.CENTER);
                        break;
                    case 2:
                        container.add(initMainboard(), BorderLayout.CENTER);
                        break;
                    case 3:
                        container.add(new MemoryPanel(si), BorderLayout.CENTER);
                        break;
                    case 4:
                        container.add(new FileStorePanel(si), BorderLayout.CENTER);
                        break;
                    case 5:
                        container.add(initSensors(si.getHardware()), BorderLayout.CENTER);
                        break;
                    case 6:
                        container.add(initDisplay(si.getHardware()), BorderLayout.CENTER);
                        break;
                    case 7:
                        container.add(new InterfacePanel(si), BorderLayout.CENTER);
                        break;
                    case 8:
                        container.add(initSoundCards(si.getHardware()), BorderLayout.CENTER);
                        break;
                    case 9:
                        container.add(initUsb(si.getHardware()), BorderLayout.CENTER);
                        break;
                    case 10:
                        container.add(initPower(si.getHardware()), BorderLayout.CENTER);
                        break;
                    case 11:
                        container.add(new ProcessPanel(si), BorderLayout.CENTER);
                        break;
                    default:
                        break;
                }
            }
        });
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

    private JPanel initDashboard(SystemInfo si) {
        JPanel osFullPanel = new JPanel();
        osFullPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        osFullPanel.setLayout(new BoxLayout(osFullPanel, BoxLayout.Y_AXIS));

        String osStr = "Operating System";
        JPanel osPanel = new JPanel(new BorderLayout());
        osPanel.setBorder(BorderFactory.createTitledBorder(osStr));
        String osInfoPrefix = getOsInfoPrefix(osStr, si);
        JLabel osArea = new JLabel(wrapHtmlTag(osInfoPrefix + "0"));
        osPanel.add(osArea);

        osFullPanel.add(osPanel);

        // Update up time every second
        Timer timer = new Timer(Config.REFRESH_FAST, e -> osArea.setText(updateOsData(osInfoPrefix, si)));
        timer.start();


        JPanel processorPanel = new JPanel(new BorderLayout());
        processorPanel.setBorder(BorderFactory.createTitledBorder("Processor"));
        processorPanel.add(new JLabel(wrapHtmlTag(getProc(si))));

        osFullPanel.add(processorPanel);


        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBorder(BorderFactory.createTitledBorder("Display(s)"));
        displayPanel.add(new JLabel(getDisplay(si)));

        osFullPanel.add(displayPanel);


        JPanel hwPanel = new JPanel(new BorderLayout());
        hwPanel.setBorder(BorderFactory.createTitledBorder("Hardware Information"));
        hwPanel.add(new JLabel(getHw(si)));

        osFullPanel.add(hwPanel);

        return osFullPanel;
    }

    private String wrapHtmlTag(String text) {
        return "<HTML>" + text + "</HTML>";
    }

    private String updateOsData(String osInfoPrefix, SystemInfo si) {
        return wrapHtmlTag(osInfoPrefix + FormatUtil.formatElapsedSecs(si.getOperatingSystem().getSystemUptime()));
    }

    private static String getHw(SystemInfo si) {
        StringBuilder sb = new StringBuilder();
        ComputerSystem computerSystem = si.getHardware().getComputerSystem();
        //TODO need format data
        return computerSystem.toString();
    }

    private static String getDisplay(SystemInfo si) {
        StringBuilder sb = new StringBuilder();
        List<Display> displays = si.getHardware().getDisplays();
        if (displays.isEmpty()) {
            sb.append("None detected.");
        } else {
            int i = 0;
            for (Display display : displays) {
                byte[] edid = display.getEdid();
                byte[][] desc = EdidUtil.getDescriptors(edid);
                String name = "Display " + i;
                for (byte[] b : desc) {
                    if (EdidUtil.getDescriptorType(b) == 0xfc) {
                        name = EdidUtil.getDescriptorText(b);
                    }
                }
                if (i++ > 0) {
                    sb.append(System.getProperty("line.separator"));
                }
                sb.append(name).append(": ");
                int hSize = EdidUtil.getHcm(edid);
                int vSize = EdidUtil.getVcm(edid);
                sb.append(String.format("%d x %d cm (%.1f x %.1f in)", hSize, vSize, hSize / 2.54, vSize / 2.54));
            }
        }
        return sb.toString();
    }

    private static String getProc(SystemInfo si) {
        StringBuilder sb = new StringBuilder();
        CentralProcessor proc = si.getHardware().getProcessor();
        sb.append(proc.toString());

        return sb.toString().replaceAll("\\n", "<BR/>");
    }

    private String getOsInfoPrefix(String osStr, SystemInfo si) {
        StringBuilder sb = new StringBuilder(osStr);
        OperatingSystem os = si.getOperatingSystem();
        sb.append(": ").append(os).append("<BR/>").append("<BR/>").append("Booted: ")
                .append(Instant.ofEpochSecond(os.getSystemBootTime())).append("<BR/>").append("Uptime: ");
        return sb.toString();
    }
}