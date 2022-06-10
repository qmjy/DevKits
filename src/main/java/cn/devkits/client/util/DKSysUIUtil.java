/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.util;

import cn.devkits.client.App;

import cn.devkits.client.cmd.ui.DKJImagePopupMenu;

import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * 系统UI工具类
 *
 * @author Shaofeng Liu
 * @version 1.0.0
 * @time 2020年1月12日 下午1:05:12
 */
public final class DKSysUIUtil {

    /**
     * 黄金比例常数
     */
    public static final float GOLDEN_RATIO = 0.618f;

    /**
     * 控件边距：0
     */
    public static final int COMPONENT_UI_PADDING_0 = 0;

    /**
     * 控件边距：2
     */
    public static final int COMPONENT_UI_PADDING_2 = 2;
    /**
     * 控件边距：5
     */
    public static final int COMPONENT_UI_PADDING_5 = 5;
    /**
     * 控件边距：8
     */
    public static final int COMPONENT_UI_PADDING_8 = 8;

    /**
     * 控件边距：10
     */
    public static final int COMPONENT_UI_PADDING_10 = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(DKSysUIUtil.class);

    /**
     * 创建文件类右键默认菜单
     *
     * @param parent 父容器
     * @param tree   菜单绑定的容器
     * @return 创建后的基础菜单
     */
    public static JPopupMenu createDefaultFileBaseMenu(JFrame parent, JTree tree) {
        DKJImagePopupMenu popupMenu = createDKJPopupMenu();

        JMenuItem copyPath2Clipboard = new JMenuItem(DKSysUIUtil.getLocale("POPUP_MENU_FILE_COPY_PATH"));
        copyPath2Clipboard.addActionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            DKSysUIUtil.setSystemClipboard(node.getUserObject().toString());
        });
        popupMenu.add(copyPath2Clipboard);
        JMenuItem copyParentPath2Clipboard = new JMenuItem(DKSysUIUtil.getLocale("POPUP_MENU_FILE_COPY_PARENT_PATH"));
        copyParentPath2Clipboard.addActionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            DKSysUIUtil.setSystemClipboard(new File(node.getUserObject().toString()).getParent());
        });
        popupMenu.add(copyParentPath2Clipboard);
        popupMenu.addSeparator();
        JMenuItem openFolder = new JMenuItem(DKSysUIUtil.getLocale("POPUP_MENU_FILE_SHOW_IN_EXPLORER"));
        openFolder.addActionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            DKFileUtil.openFolder(node.getUserObject().toString());
        });
        popupMenu.add(openFolder);
        JMenuItem openFile = new JMenuItem(DKSysUIUtil.getLocale("POPUP_MENU_FILE_OPEN"));
        openFile.addActionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            DKFileUtil.openFile(node.getUserObject().toString());
        });
        popupMenu.add(openFile);
        popupMenu.addSeparator();
        JMenuItem delete = new JMenuItem(DKSysUIUtil.getLocale("POPUP_MENU_FILE_DELETE"));
        delete.addActionListener(e -> {
            int deleteOption = JOptionPane.showConfirmDialog(parent, DKSysUIUtil.getLocale(
                    "POPUP_MENU_FILE_DEL_DIALOG_CONTENT"), DKSysUIUtil.getLocale(
                    "POPUP_MENU_FILE_DEL_DIALOG_TITLE"), JOptionPane.YES_NO_OPTION);
            if (deleteOption == JOptionPane.YES_OPTION) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                File file = new File(node.getUserObject().toString());
                if (FileUtils.deleteQuietly(file)) {
                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    model.reload();
                }
            }
        });
        popupMenu.add(delete);
        return popupMenu;
    }

    /**
     * 获取自定义央视JPopupMenu
     *
     * @return JPopupMenu
     */
    public static DKJImagePopupMenu createDKJPopupMenu() {
        return new DKJImagePopupMenu("Devkits");
    }

    /**
     * 创建带边框的Panel
     *
     * @param color 边框颜色
     * @return 带颜色的Jpanel容器
     */
    public static JPanel createPaneWithBorder(Color color) {
        JPanel jPanel = new JPanel();
        Border lineBorder = BorderFactory.createLineBorder(color);
        jPanel.setBorder(new CompoundBorder(lineBorder, new EmptyBorder(5, 10, 5, 10)));
        return jPanel;
    }

    /**
     * 创建绿色文本的的label
     *
     * @param text 待设定的文本内容
     * @return 绿色文本的Label
     */
    public static JLabel createLabelWithGreenText(String text) {
        return createLabelWithText(text, new Color(0, 200, 0));
    }

    /**
     * 创建红色文本的的label
     *
     * @param text 待设定的文本内容
     * @return 红色文本的Label
     */
    public static JLabel createLabelWithRedText(String text) {
        return createLabelWithText(text, new Color(200, 0, 0));
    }

    /**
     * 创建指定颜色文本的的label
     *
     * @param text 待设定的文本内容
     * @return 指定颜色文本的Label
     */
    public static JLabel createLabelWithText(String text, Color color) {
        JLabel jLabel = new JLabel(text);
        jLabel.setForeground(color);
        jLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        return jLabel;
    }


    /**
     * 获取系统默认语言字符
     *
     * @param code 字典查询的code
     * @return 字典查询结果
     */
    public static String getLocale(String code) {
        return getLocale(code, "'" + code + "'");
    }

    /**
     * 获取待占位符的语言
     *
     * @param key       字典查询的code
     * @param arguments 占位符数据
     * @return 填充数据后的i18n字符串
     */
    public static String getLocaleWithParam(String key, Object... arguments) {
        return MessageFormat.format(getLocale(key), arguments);
    }

    /**
     * 获取系统默认语言字符（带省略号），用于打开对话框的情况
     *
     * @param code 字典查询的code
     * @return 字典查询结果，并且会追加一个省略号
     */
    public static String getLocaleWithEllipsis(String code) {
        return getLocale(code) + "...";
    }

    /**
     * 获取系统默认语言字符（带冒号）
     *
     * @param code 字典查询的code
     * @return 字典查询结果，并且会追加一个冒号
     */
    public static String getLocaleWithColon(String code) {
        return getLocale(code) + ":";
    }

    /**
     * 获取系统默认语言字符（带叹号）
     *
     * @param code 字典查询的code
     * @return 字典查询结果，并且会追加一个叹号
     */
    public static String getLocaleWithExclamation(String code) {
        return getLocale(code) + "!";
    }

    /**
     * 获取系统默认语言字符
     *
     * @param code    字典查询的code
     * @param defualt 语言字典不存在时的，则会显示此默认值
     * @return 字典查询结果
     */
    public static String getLocale(String code, String defualt) {
        return App.getContext().getMessage(code, null, defualt, Locale.getDefault());
    }

    /**
     * get center bounds
     *
     * @param width  dialog or frame width
     * @param height dialog or frame height
     * @return dialog or frame bounds
     */
    public static Rectangle getCenter(int width, int height) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Rectangle((int) ((screenSize.width - width) / 2), (int) ((screenSize.height - height) / 2), (int) width, (int) height);
    }


    /**
     * 居中显示一个对话框，并指定对话框的宽度
     *
     * @param jDialog 待居中的容器组件
     * @param width   容器的宽度
     */
    public static void centerWithWidth(JDialog jDialog, int width) {
        int height = (int) (width * GOLDEN_RATIO);
        jDialog.setBounds(getCenter(width, height));
    }

    public static void setLookAndFeel(String lookAndFeelName) {
        try {
            UIManager.setLookAndFeel(lookAndFeelName);
            Window[] windows = Frame.getWindows();
            for (Window win : windows) {
                SwingUtilities.updateComponentTreeUI(win);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e1) {
            LOGGER.error("Init Look And Feel error:" + e1.getMessage());
        } catch (UnsupportedLookAndFeelException e) {
            LOGGER.error("UnsupportedLookAndFeelException:" + e.getMessage());
        }
    }

    /**
     * 开启表格右键选中行效果
     *
     * @param e     鼠标事件
     * @param table 待开启的表格
     */
    public static void enableRightClickSelect(MouseEvent e, JTable table) {
        int r = table.rowAtPoint(e.getPoint());
        if (r >= 0 && r < table.getRowCount()) {
            table.setRowSelectionInterval(r, r);
        } else {
            table.clearSelection();
        }
    }

    /**
     * 表格头列自适应
     *
     * @param myTable 待处理的表格
     */
    public static void fitTableColumns(JTable myTable) {
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();
        Enumeration columns = myTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int) myTable.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
            for (int row = 0; row < rowCount; row++) {
                int preferedWidth = (int) myTable.getCellRenderer(row, col).getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                width = Math.max(width, preferedWidth);
            }
            header.setResizingColumn(column); // 此行很重要
            column.setWidth(width + myTable.getIntercellSpacing().width);
        }
    }

    /**
     * 更新jtable表格在JScrollPane占用高度过高的问题<br>
     * https://coderanch.com/t/336316/java/JScrollPane-packed-content
     *
     * @param table 待更新表格
     * @return 表格的实际宽高
     */
    public static Dimension updatePreferredScrollableViewportSize(JTable table) {
        int rowCount = table.getRowCount();

        int cols = table.getColumnModel().getTotalColumnWidth();
        int rows = table.getRowHeight() * rowCount - 1;
        return new Dimension(cols, rows);
    }


    /**
     * 展开指定节点的所有子节点
     *
     * @param tree   待操作的树
     * @param parent 待展开的节点
     * @param expand 是否展开
     */
    public static void expandAll(JTree tree, TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    /**
     * 坐标索引超出最大范围，或当前行的下一个控件为null,则为当前行最后一个组件
     *
     * @param components 组件集合
     * @param i          行号
     * @param j          列号
     * @return 是否一行的最后一个组件
     */
    private static boolean isEndColumnOfRow(JComponent[][] components, int i, int j) {
        if (i >= components.length - 1 || j >= components[0].length - 1) {
            return true;
        }
        if (components[i][j + 1] == null) {
            return true;
        }
        return false;
    }


    /**
     * 使用系统浏览器打开指定网址
     *
     * @param uri 待打开的网址
     * @return 打开是否成功
     */
    public static boolean browseURL(URI uri) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(uri);
                return true;
            } catch (IOException e1) {
                LOGGER.error("Open url '{}' failed: {}", uri, e1.getMessage());
            }
        }
        return false;
    }


    public static void setContainerSize(Container parent, int pad) {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component[] components = parent.getComponents();
        Spring maxHeightSpring = Spring.constant(0);
        SpringLayout.Constraints pCons = layout.getConstraints(parent);

        // Set the container's right edge to the right edge
        // of its rightmost component + padding.
        Component rightmost = components[components.length - 1];
        SpringLayout.Constraints rCons = layout.getConstraints(rightmost);
        pCons.setConstraint(SpringLayout.EAST, Spring.sum(Spring.constant(pad), rCons.getConstraint(SpringLayout.EAST)));

        // Set the container's bottom edge to the bottom edge
        // of its tallest component + padding.
        for (int i = 0; i < components.length; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(components[i]);
            maxHeightSpring = Spring.max(maxHeightSpring, cons.getConstraint(SpringLayout.SOUTH));
        }
        pCons.setConstraint(SpringLayout.SOUTH, Spring.sum(Spring.constant(pad), maxHeightSpring));
    }

    /**
     * 设置字符串到系统剪贴板
     *
     * @param content 要设置到剪贴板的字符串内容
     */
    public static void setSystemClipboard(String content) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(content);
        clip.setContents(tText, null);
    }

    /**
     * 获取微信用户数据目录
     *
     * @return 微信用户数据目录
     */
    public static String getWechatDataPath() {
        if (DKSysUtil.isWindows()) {
            return DKSysUtil.getHomePath() + File.separator + "Documents";
        }
        return "";
    }


    /**
     * 获取当前操作系统中所有可视窗口
     *
     * @return 操作系统的所有可视窗口
     */
    public List<DesktopWindow> getAllWindowsOfSystem() {
        return WindowUtils.getAllWindows(true);
    }

    /**
     * 注册图标
     */
    public static void regIcon() {
        IconFontSwing.register(FontAwesome.getIconFont());
    }

    public static FileFilter createFileFilter(String description, boolean showExtensionInDescription, String... extensions) {
        if (showExtensionInDescription) {
            description = createFileNameFilterDescriptionFromExtensions(description, extensions);
        }
        return new FileNameExtensionFilter(description, extensions);
    }

    private static String createFileNameFilterDescriptionFromExtensions(String description, String[] extensions) {
        String fullDescription = (description == null) ? "(" : description + " (";
        // build the description from the extension list
        fullDescription += "." + extensions[0];
        for (int i = 1; i < extensions.length; i++) {
            fullDescription += ", .";
            fullDescription += extensions[i];
        }
        fullDescription += ")";
        return fullDescription;
    }
}
