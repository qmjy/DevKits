package cn.devkits.client.model;

import java.util.List;
import java.util.TreeMap;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class LargeDuplicateFilesTreeModel implements TreeModel {

    private TreeMap<String, List<FileModel>> fileMaps;

    public LargeDuplicateFilesTreeModel(TreeMap<String, List<FileModel>> fileMaps) {
        this.fileMaps = fileMaps;
    }

    @Override
    public Object getRoot() {
        return "Duplicate Files";
    }

    @Override
    public Object getChild(Object parent, int index) {
        return "Item" + index;
    }

    @Override
    public int getChildCount(Object parent) {
        return 10;
    }

    @Override
    public boolean isLeaf(Object node) {
        return false;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {

    }

}
