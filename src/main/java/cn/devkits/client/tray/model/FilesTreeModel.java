package cn.devkits.client.tray.model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.File;

public class FilesTreeModel extends DefaultMutableTreeNode {
    private File node = null;
    private File[] children = new File[0];


    public FilesTreeModel(String wechatDataPath) {
        File file = new File(wechatDataPath);
        if (file.exists()) {
            this.node = new File(wechatDataPath);
            this.children = node.listFiles();
        }
    }

    @Override
    public Object getUserObject() {
        return node;
    }

    @Override
    public int getChildCount() {
        return children.length;
    }

    @Override
    public TreeNode getChildAt(int index) {
        return new FilesTreeModel(children[index].toString());
    }

    @Override
    public boolean isLeaf() {
        return node == null ? true : !(node.isDirectory());
    }

    public File getNode() {
        return node;
    }


    @Override
    public String toString() {
        return node.getName();
    }
}
