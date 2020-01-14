package cn.devkits.client.tray.frame.assist;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 * 
 * demo from: http://www.java2s.com/Code/Java/Swing-JFC/InstallToolTipsforTreeJTree.htm
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年10月8日 下午9:31:45
 */
public class LargeDuplicateFilesTreeCellRenderer implements TreeCellRenderer {

    private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        if (value != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

            String toolTips = null;
            if (node.getLevel() == 2) {
                toolTips = node.getUserObject().toString();
            }
            renderer.setToolTipText(toolTips);
        }
        return renderer;
    }

}
