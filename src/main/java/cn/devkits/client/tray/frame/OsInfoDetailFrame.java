package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.time.Instant;
import java.util.Arrays;
import java.util.Enumeration;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import cn.devkits.client.util.DKSystemUIUtil;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.hardware.SoundCard;
import oshi.hardware.UsbDevice;
import oshi.hardware.VirtualMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

/**
 * 
 * 当前计算机软硬件信息展示
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年10月21日 下午8:23:09
 */
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
        jTabbedPane.addTab("Dashboard", initDashboard(si.getHardware(), si.getOperatingSystem()));
        jTabbedPane.addTab("CPU", initCpu(si.getHardware()));
        jTabbedPane.addTab("Main Board", initMainboard());
        jTabbedPane.addTab("Memory", initMemory(si.getHardware()));
        jTabbedPane.addTab("Disk", initDisk(si.getHardware(), si.getOperatingSystem()));
        jTabbedPane.addTab("Sensors", initSensors(si.getHardware()));
        jTabbedPane.addTab("Displays", initDisplay(si.getHardware()));
        jTabbedPane.addTab("Network", initNetwork(si.getHardware(), si.getOperatingSystem()));
        jTabbedPane.addTab("Sound Cards", initSoundCards(si.getHardware()));
        jTabbedPane.addTab("USB Devices", initUsb(si.getHardware()));
        jTabbedPane.addTab("Power Sources", initPower(si.getHardware()));

        jTabbedPane.setFocusable(false);// 不显示选项卡上的焦点虚线边框

        jRootPane.add(jTabbedPane, BorderLayout.CENTER);
    }

    private Component initNetwork(HardwareAbstractionLayer hal, OperatingSystem os) {
        NetworkIF[] networkIFs = hal.getNetworkIFs();

        StringBuilder sb = new StringBuilder("<html><body>Network Interfaces:<br>");

        if (networkIFs.length == 0) {
            sb.append(" Unknown");
        }
        for (NetworkIF net : networkIFs) {
            sb.append("<br> ").append(net.toString());
        }
        sb.append(sb.toString());


        NetworkParams networkParams = os.getNetworkParams();

        sb.append("<br>Network parameters:<br> " + networkParams.toString());

        sb.append("</body></html>");
        return new JLabel(sb.toString());
    }

    private Component initCpu(HardwareAbstractionLayer hal) {
        CentralProcessor processor = hal.getProcessor();

        StringBuilder sb = new StringBuilder("<html><body>");

        sb.append("Context Switches/Interrupts: " + processor.getContextSwitches() + " / " + processor.getInterrupts()).append("<br>");

        long[] prevTicks = processor.getSystemCpuLoadTicks();
        long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
        sb.append("CPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks)).append("<br>");
        // Wait a second...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        sb.append("CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks)).append("<br>");
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

        sb.append(String.format("User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%", 100d * user / totalCpu, 100d * nice / totalCpu,
                100d * sys / totalCpu, 100d * idle / totalCpu, 100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu, 100d * steal / totalCpu)).append("<br>");
        sb.append(String.format("CPU load: %.1f%%", processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100)).append("<br>");
        double[] loadAverage = processor.getSystemLoadAverage(3);
        sb.append("CPU load averages:" + (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0])) + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2]))).append("<br>");
        // per core CPU
        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
        double[] load = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
        for (double avg : load) {
            procCpu.append(String.format(" %.1f%%", avg * 100));
        }
        sb.append(procCpu.toString()).append("<br>");
        long freq = processor.getProcessorIdentifier().getVendorFreq();
        if (freq > 0) {
            sb.append("Vendor Frequency: " + FormatUtil.formatHertz(freq)).append("<br>");
        }
        freq = processor.getMaxFreq();
        if (freq > 0) {
            sb.append("Max Frequency: " + FormatUtil.formatHertz(freq)).append("<br>");
        }
        long[] freqs = processor.getCurrentFreq();
        if (freqs[0] > 0) {
            StringBuilder sb1 = new StringBuilder("Current Frequencies: ");
            for (int i = 0; i < freqs.length; i++) {
                if (i > 0) {
                    sb1.append(", ");
                }
                sb1.append(FormatUtil.formatHertz(freqs[i]));
            }
            sb.append(sb1.toString()).append("<br>");
        }


        sb.append("<br><br>").append(processor.toString()).append("<br>");

        sb.append("</body></html>");
        return new JLabel(sb.toString());
    }

    private Component initMainboard() {
        // TODO Auto-generated method stub
        return null;
    }

    private Component initMemory(HardwareAbstractionLayer hal) {
        GlobalMemory memory = hal.getMemory();

        StringBuilder sb = new StringBuilder("<html><body>Sensors: <br>");

        sb.append("Memory: <br> " + memory.toString());
        VirtualMemory vm = memory.getVirtualMemory();
        sb.append("Swap: <br> " + vm.toString());
        PhysicalMemory[] pmArray = memory.getPhysicalMemory();
        if (pmArray.length > 0) {
            sb.append("Physical Memory: ");
            for (PhysicalMemory pm : pmArray) {
                sb.append(" " + pm.toString());
            }
        }

        sb.append("</body></html>");
        return new JLabel(sb.toString());
    }

    private Component initSensors(HardwareAbstractionLayer hal) {
        Sensors sensors = hal.getSensors();

        StringBuilder sb = new StringBuilder("<html><body>Sensors: <br>");

        sb.append(sensors.toString()).append("<br>");

        sb.append("</body></html>");
        return new JLabel(sb.toString());
    }

    private Component initDisplay(HardwareAbstractionLayer hal) {
        Display[] displays = hal.getDisplays();

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
        UsbDevice[] usbDevices = hal.getUsbDevices(true);

        StringBuilder sb = new StringBuilder("<html><body>USB Devices: <br>");

        for (UsbDevice usbDevice : usbDevices) {
            sb.append(String.valueOf(usbDevice)).append("<br>");
        }
        sb.append("</body></html>");
        return new JLabel(sb.toString());
    }

    private Component initSoundCards(HardwareAbstractionLayer hal) {
        SoundCard[] soundCards = hal.getSoundCards();

        StringBuilder sb = new StringBuilder("<html><body>Sound Cards: <br>");

        for (SoundCard card : soundCards) {
            sb.append(" " + String.valueOf(card)).append("<br>");
        }
        sb.append("</body></html>");
        return new JLabel(sb.toString());
    }

    private Component initPower(HardwareAbstractionLayer hal) {
        PowerSource[] powerSources = hal.getPowerSources();

        StringBuilder sb = new StringBuilder("<html><body>Power Sources: <br>");
        if (powerSources.length == 0) {
            sb.append("Unknown<br>");
        }
        for (PowerSource powerSource : powerSources) {
            sb.append("<br> ").append(powerSource.toString());
            sb.append("<br>");
        }
        sb.append("</body></html>");
        return new JLabel(sb.toString());
    }

    private Component initDisk(HardwareAbstractionLayer hal, OperatingSystem os) {
        JPanel diskRootPanel = new JPanel();
        diskRootPanel.setLayout(new GridLayout(2, 1));

        JTable partitionTable = new JTable(new DiskTableModel(hal.getDiskStores()));
        DKSystemUIUtil.fitTableColumns(partitionTable);
        JScrollPane diskScrollPanel = new JScrollPane(partitionTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        diskScrollPanel.setBorder(BorderFactory.createTitledBorder("Physical Disks"));
        diskRootPanel.add(diskScrollPanel);

        JTable fileSystemTable = new JTable(new FileSystemModel(os.getFileSystem().getFileStores()));
        DKSystemUIUtil.fitTableColumns(fileSystemTable);
        JScrollPane fileScrollPane = new JScrollPane(fileSystemTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        fileScrollPane.setBorder(BorderFactory.createTitledBorder("File System:"));
        diskRootPanel.add(fileScrollPane);

        return diskRootPanel;
    }



    private JLabel initDashboard(HardwareAbstractionLayer hal, OperatingSystem os) {
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
        sb.append("System Version : " + os.getVersion());
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


class FileSystemModel implements TableModel {
    private OSFileStore[] fileStores;

    public FileSystemModel(OSFileStore[] fileStores) {
        this.fileStores = fileStores;
    }

    @Override
    public int getRowCount() {
        return fileStores.length;
    }

    @Override
    public int getColumnCount() {
        return 12;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Name";
            case 1:
                return "Volume";
            case 2:
                return "Logical Volume";
            case 3:
                return "Mount";
            case 4:
                return "Description";
            case 5:
                return "Type";
            case 6:
                return "UUID";
            case 7:
                return "Free Space";
            case 8:
                return "Usable Space";
            case 9:
                return "Total Space";
            case 10:
                return "Free Inodes";
            case 11:
                return "Total Inodes";
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return fileStores[rowIndex].getName();
            case 1:
                return fileStores[rowIndex].getVolume();
            case 2:
                return fileStores[rowIndex].getLogicalVolume();
            case 3:
                return fileStores[rowIndex].getMount();
            case 4:
                return fileStores[rowIndex].getDescription();
            case 5:
                return fileStores[rowIndex].getType();
            case 6:
                return fileStores[rowIndex].getUUID();
            case 7:
                return FormatUtil.formatBytesDecimal(fileStores[rowIndex].getFreeSpace());
            case 8:
                return FormatUtil.formatBytesDecimal(fileStores[rowIndex].getUsableSpace());
            case 9:
                return FormatUtil.formatBytesDecimal(fileStores[rowIndex].getTotalSpace());
            case 10:
                return fileStores[rowIndex].getFreeInodes();
            case 11:
                return fileStores[rowIndex].getTotalInodes();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // TODO Auto-generated method stub
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        // TODO Auto-generated method stub
    }
}


class DiskTableModel implements TableModel {

    private HWDiskStore[] diskStores;

    public DiskTableModel(HWDiskStore[] diskStores) {
        this.diskStores = diskStores;
    }

    @Override
    public int getRowCount() {
        int count = 0;
        for (int i = 0; i < diskStores.length; i++) {
            count += diskStores[0].getPartitions().length;
        }
        return count;
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Identification";
            case 1:
                return "Name";
            case 2:
                return "Type";
            case 3:
                return "UUID";
            case 4:
                return "Size";
            case 5:
                return "Major";
            case 6:
                return "Minor";
            case 7:
                return "MountPoint";
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int rowCount = 0;
        for (HWDiskStore hwDiskStore : diskStores) {
            HWPartition[] partitions = hwDiskStore.getPartitions();
            for (int j = 0; j < partitions.length; j++) {
                if (rowCount == rowIndex) {
                    switch (columnIndex) {
                        case 0:
                            return partitions[j].getIdentification();
                        case 1:
                            return partitions[j].getName();
                        case 2:
                            return partitions[j].getType();
                        case 3:
                            return partitions[j].getUuid();
                        case 4:
                            return FormatUtil.formatBytesDecimal(partitions[j].getSize());
                        case 5:
                            return partitions[j].getMajor();
                        case 6:
                            return partitions[j].getMinor();
                        case 7:
                            return partitions[j].getMountPoint();
                        default:
                            return "";
                    }
                }
                rowCount++;
            }
        }
        return "";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        // TODO Auto-generated method stub

    }
}
