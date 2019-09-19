package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class LargeDuplicateFilesFrame extends DKAbstractFrame {

    private static final long serialVersionUID = 6081895254576694963L;

    public LargeDuplicateFilesFrame() {
        super("Large Duplicate Files");

        initPane();
    }

    private void initPane() {
        Object[][] data = {{"zz", "sdfgd", "sdfgd", "sdfgd"}, {"12312", "sdfgd", "sdfgd", "3452345"}};

        String[] names = {"Index", "MD5", "Count", "File Size"};

        JTable jTable = new JTable(data, names);

        this.getContentPane().add(new JScrollPane(jTable), BorderLayout.CENTER);
    }
}