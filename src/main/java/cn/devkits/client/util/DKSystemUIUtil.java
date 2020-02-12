package cn.devkits.client.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import cn.devkits.client.tray.frame.AboutFrame;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 系统UI工具类
 * @author Shaofeng Liu
 * @version 1.0.0
 * @time 2020年1月12日 下午1:05:12
 */
public final class DKSystemUIUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DKSystemUIUtil.class);

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
     * @param tree 待操作的树
     * @param parent 待展开的节点
     * @param expand 是否展开
     */
    public static void expandAll(JTree tree, TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
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
     * 使用系统浏览器打开指定网址
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
