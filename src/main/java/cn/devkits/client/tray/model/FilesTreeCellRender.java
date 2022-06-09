package cn.devkits.client.tray.model;

import cn.devkits.client.util.DKFileUtil;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.io.File;

public class FilesTreeCellRender extends DefaultTreeCellRenderer {
    private FileSystemView fileSystemView = DKFileUtil.getFileSysView();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        FilesTreeModel treeModel = (FilesTreeModel) value;
        File file = treeModel.getNode();

        setIcon(fileSystemView.getSystemIcon(file));
        setText(file.getName());
        return this;
    }
}
