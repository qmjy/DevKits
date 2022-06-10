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

    /**
     * 获取当前节点代码的文件对象
     *
     * @return 当前节点代表的文件对象
     */
    public File getNode() {
        return node;
    }

    public String getNodePath() {
        return node.getAbsolutePath();
    }

    public String getNodeParentPath() {
        return node.getParent();
    }


    @Override
    public String toString() {
        return node.getName();
    }
}
