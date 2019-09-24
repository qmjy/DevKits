package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.TreeModel;
import org.apache.commons.codec.digest.DigestUtils;
import cn.devkits.client.model.FileModel;
import cn.devkits.client.model.LargeDuplicateFilesTreeModel;

public class LargeDuplicateFilesFrame extends DKAbstractFrame {

    private static final long serialVersionUID = 6081895254576694963L;
    // 端口检查线程，充分利用CPU，尽量让IO吞吐率达到最大阈值
    private static final int FIXED_THREAD_NUM = Runtime.getRuntime().availableProcessors() * 250;

    private ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(FIXED_THREAD_NUM);

    public LargeDuplicateFilesFrame() {
        super("Large Duplicate Files");
    }


    @Override
    protected JRootPane createRootPane() {
        JRootPane jRootPane = new JRootPane();
        jRootPane.setLayout(new BorderLayout());

        JSplitPane jSplitPane = new JSplitPane();

        jSplitPane.setLeftComponent(new JScrollPane(new JTree(new LargeDuplicateFilesTreeModel(loadLargeDuplicateFiles()))));

        Object[][] data = {{"zz", "sdfgd", "sdfgd", "sdfgd"}, {"12312", "sdfgd", "sdfgd", "3452345"}};
        String[] names = {"Index", "MD5", "Count", "File Size"};

        JTable jTable = new JTable(data, names);
        jSplitPane.setRightComponent(new JScrollPane(jTable));

        jRootPane.add(jSplitPane, BorderLayout.CENTER);

        return jRootPane;
    }

    private TreeMap<String, List<FileModel>> loadLargeDuplicateFiles() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                FileSystemView sys = FileSystemView.getFileSystemView();
                File[] roots = sys.getRoots();
                for (File file : roots) {
                    recursiveSearch(file);
                }
                newFixedThreadPool.shutdown();
            }
        };

        new Thread(runnable).start();

        return null;
    }


    @Override
    protected void initListener() {

    }

    private void recursiveSearch(File dirFile) {
        File[] listFiles = dirFile.listFiles();
        for (File file2 : listFiles) {
            if (file2.isDirectory()) {
                recursiveSearch(file2);
            } else {
                newFixedThreadPool.submit(new FileMd5Thread(file2));
            }
        }
    }
}


class FileMd5Thread extends Thread {

    private File file;

    public FileMd5Thread(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        try {
            String md5Hex = DigestUtils.md5Hex(new FileInputStream(file));
            FileModel fileModel = new FileModel(md5Hex, file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
