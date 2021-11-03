/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.util.DKConfigUtil;
import cn.devkits.client.util.DKSystemUIUtil;
import cn.devkits.client.util.DKSystemUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * About Dialog
 *
 * @author shaofeng
 * @version 1.0.0
 * @datetime 2019年9月6日 下午11:39:56
 */
public class AboutFrame extends DKAbstractFrame {

    private static final long serialVersionUID = 3737746590178589617L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AboutFrame.class);
    private JLabel name = new JLabel(DKSystemUIUtil.getLocaleString("APP_LOGO"));
    private JLabel version = null;

    public AboutFrame() {
        super(DKSystemUIUtil.getLocaleString("ABOUT_APP"), 0.7f, 0.6f);

        initUI(getContentPane());
        initListener();
    }

    @Override
    protected void initUI(Container jRootPane) {
        name.setFont(getAFont());

        version = new JLabel(DKSystemUIUtil.getLocaleString("VERSION") + DKConfigUtil.getInstance().getPomInfo().getVersion());
        version.setLabelFor(name);

        // Create the panel we'll return and set up the layout.
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        version.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        name.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        panel.add(name);
        panel.add(Box.createVerticalStrut(5)); // extra space
        panel.add(version);

        // Add a vertical spacer that also guarantees us a minimum width:
        panel.add(Box.createRigidArea(new Dimension(150, 10)));

        panel.add(initTabContent());

        jRootPane.add(panel);
    }


    private Component initTabContent() {
        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("VERSION_INFO"), loadVersionDetail());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("OPEN_SOURCE_DEPENDENT"), loadOpenSourceTable());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("OPEN_SOURCE_PRJ"), loadOpenSourcePrj());
        jTabbedPane.addTab(DKSystemUIUtil.getLocaleString("LICENSE"), loadLicensePane());

        // 不显示选项卡上的焦点虚线边框
        jTabbedPane.setFocusable(false);

        return jTabbedPane;
    }

    /**
     * 其他开源项目
     *
     * @return 其他开源项目
     */
    private Component loadOpenSourcePrj() {
        DefaultListModel listModel = new DefaultListModel();
        listModel.addElement("https://github.com/521xueweihan/GitHub520");
        listModel.addElement("https://github.com/XX-net/XX-Net");
        listModel.addElement("https://github.com/chuiliu/the-pixel-art");

        JList list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = list.locationToIndex(e.getPoint());
                    String url = (String) list.getModel().getElementAt(index);
                    try {
                        DKSystemUIUtil.browseURL(new URI(url));
                    } catch (URISyntaxException e1) {
                        LOGGER.error("Open URL '{}' failed: {}", url, e1.getMessage());
                    }
                }
            }
        });
        list.setToolTipText(DKSystemUIUtil.getLocaleString("DB_CLICK_TOOLTIPS"));
        JScrollPane listScrollPane = new JScrollPane(list);

        return listScrollPane;
    }

    private Component loadLicensePane() {
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        jEditorPane.setEditable(false);
        jEditorPane.setText(loadLicenseContent());
        jEditorPane.setCaretPosition(0);

        return new JScrollPane(jEditorPane);
    }

    private String loadLicenseContent() {
        File licenseFile = DKSystemUtil.isDevelopMode() ? new File("LICENSE") : new File("../LICENSE");
        StringBuilder sb = new StringBuilder();
        try {
            LineIterator lineIterator = FileUtils.lineIterator(licenseFile);
            while (lineIterator.hasNext()) {
                sb.append(lineIterator.nextLine()).append("<br>");
            }
        } catch (IOException e) {
            LOGGER.error("Reading license file failed: {}", e.getMessage());
        }

        return sb.toString();
    }

    private Component loadVersionDetail() {
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        jEditorPane.setEditable(false);
        jEditorPane.setText(DKConfigUtil.getInstance().getAboutHtml());
        jEditorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    URI uri = e.getURL().toURI();
                    boolean browseURL = DKSystemUIUtil.browseURL(uri);
                    if (!browseURL) {
                        JOptionPane.showMessageDialog(this, "Open URL with system browser failed: " + uri, "Browse URL Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (URISyntaxException e1) {
                    LOGGER.error("URL exception: " + e1.getMessage());
                }
            }
        });

        return new JScrollPane(jEditorPane);
    }

    private Component loadOpenSourceTable() {
        Model pomInfo = DKConfigUtil.getInstance().getPomInfo();

        JTable jTable = new JTable(new OpenSourceTableModel(pomInfo));
        DKSystemUIUtil.fitTableColumns(jTable);

        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jTable);

        return jScrollPane;
    }

    private Font getAFont() {
        // initial strings of desired fonts
        String[] desiredFonts = {"French Script", "FrenchScript", "Script"};

        String[] existingFamilyNames = null; // installed fonts
        String fontName = null; // font we'll use

        // Search for all installed font families. The first
        // call may take a while on some systems with hundreds of
        // installed fonts, so if possible execute it in idle time,
        // and certainly not in a place that delays painting of
        // the UI (for example, when bringing up a menu).
        //
        // In systems with malformed fonts, this code might cause
        // serious problems; use the latest JRE in this case. (You'll
        // see the same problems if you use Swing's HTML support or
        // anything else that searches for all fonts.) If this call
        // causes problems for you under the latest JRE, please let
        // us know.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (ge != null) {
            existingFamilyNames = ge.getAvailableFontFamilyNames();
        }

        // See if there's one we like.
        if ((existingFamilyNames != null) && (desiredFonts != null)) {
            int i = 0;
            while ((fontName == null) && (i < desiredFonts.length)) {

                // Look for a font whose name starts with desiredFonts[i].
                int j = 0;
                while ((fontName == null) && (j < existingFamilyNames.length)) {
                    if (existingFamilyNames[j].startsWith(desiredFonts[i])) {

                        // We've found a match. Test whether it can display
                        // the Latin character 'A'. (You might test for
                        // a different character if you're using a different
                        // language.)
                        Font f = new Font(existingFamilyNames[j], Font.PLAIN, 1);
                        if (f.canDisplay('A')) {
                            fontName = existingFamilyNames[j];
                            System.out.println("Using font: " + fontName);
                        }
                    }
                    j++; // Look at next existing font name.
                }
                i++; // Look for next desired font.
            }
        }

        // Return a valid Font.
        if (fontName != null) {
            return new Font(fontName, Font.PLAIN, 36);
        } else {
            return new Font("Serif", Font.ITALIC, 36);
        }
    }

    @Override
    protected void initListener() {
    }
}


/**
 * 开源软件数据模型
 *
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年10月30日 下午10:33:33
 */
class OpenSourceTableModel implements TableModel {

    private Model pomInfo;

    public OpenSourceTableModel(Model pomInfo) {
        this.pomInfo = pomInfo;
    }

    @Override
    public int getRowCount() {
        return pomInfo.getDependencies().size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Index";
            case 1:
                return "Name";
            case 2:
                return "GroupId";
            case 3:
                return "ArtifactId";
            case 4:
                return "Version";
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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        List<Dependency> dependencies = pomInfo.getDependencies();
        for (int i = 0; i < dependencies.size(); i++) {
            if (rowIndex == i) {
                Dependency dependency = dependencies.get(i);
                switch (columnIndex) {
                    case 0:
                        return rowIndex + 1;
                    case 1:
                        return dependency.getArtifactId() + "-" + dependency.getVersion();
                    case 2:
                        return dependency.getGroupId();
                    case 3:
                        return dependency.getArtifactId();
                    case 4:
                        return dependency.getVersion();
                    default:
                        return "";
                }
            }
        }
        return "";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

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
