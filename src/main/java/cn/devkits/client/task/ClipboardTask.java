package cn.devkits.client.task;

import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.TimerTask;

public class ClipboardTask extends TimerTask {

    private Clipboard sysClip;

    public ClipboardTask(Clipboard sysClip) {
        this.sysClip = sysClip;
    }

    @Override
    public void run() {
        Transferable contents = sysClip.getContents(null);
        if (contents != null) {
            try {
                if (contents.isDataFlavorSupported(DataFlavor.allHtmlFlavor)) {
                    String ret = (String) contents.getTransferData(DataFlavor.allHtmlFlavor);
                    System.out.println(ret);
                } else if (contents.isDataFlavorSupported(DataFlavor.fragmentHtmlFlavor)) {
                    System.out.println("DataFlavor.fragmentHtmlFlavor");
                } else if (contents.isDataFlavorSupported(DataFlavor.selectionHtmlFlavor)) {
                    System.out.println("DataFlavor.selectionHtmlFlavor");
                } else if (contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    Image img = (Image) contents.getTransferData(DataFlavor.imageFlavor);
                    System.out.println(img);
                } else if (contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {// 拷贝了本地文件
                    Object transferData = contents.getTransferData(DataFlavor.javaFileListFlavor);
                    if (transferData instanceof List<?>) {
                        List<String> fileLists = (List<String>) transferData;
                        System.out.println(fileLists);
                    }
                } else if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String ret = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    System.out.println(ret);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
