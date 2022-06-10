package cn.devkits.client.tray.listener;

import cn.devkits.client.util.DKFileUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * 文件类右键操作基础菜单
 */
public class FilesMouseListener extends MouseAdapter {
    private final JTree tree;
    private final JPopupMenu jTreeMenu;

    public FilesMouseListener(JTree tree, JPopupMenu jTreeMenu) {
        this.tree = tree;
        this.jTreeMenu = jTreeMenu;
    }

    /**
     * 展示菜单
     *
     * @param e         MouseEvent
     * @param tree      JTree
     * @param jTreeMenu JPopupMenu
     */
    public static void showPopupMenu(MouseEvent e, JTree tree, JPopupMenu jTreeMenu) {
        TreePath tp = tree.getClosestPathForLocation(e.getX(), e.getY());
        if (tp != null) {
            tree.setSelectionPath(tp);
        }
        jTreeMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopupMenu(e, tree, jTreeMenu);
            return;
        }
        // 鼠标左键
        if (e.getButton() == MouseEvent.BUTTON1) {
            JTree tree = (JTree) e.getSource();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (e.getClickCount() == 2 && node.isLeaf()) {
                File file = new File(node.getUserObject().toString());
                DKFileUtil.openFile(file.getParentFile());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopupMenu(e, tree, jTreeMenu);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopupMenu(e, tree, jTreeMenu);
        }
    }
}
