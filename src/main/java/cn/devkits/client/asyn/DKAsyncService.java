package cn.devkits.client.asyn;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import cn.devkits.client.model.ClipboardModel;
import cn.devkits.client.service.ClipboardService;
import cn.devkits.client.util.DKDateTimeUtil;

/**
 * 
 * 系统异步定时任务
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年11月20日 下午9:55:38
 */
@Component
@EnableScheduling
public class DKAsyncService {
    @Autowired
    private ClipboardService service;

    /**
     * 每一秒执行一次<br>
     * https://blog.csdn.net/qq_25652213/article/details/93635540
     */
    @Scheduled(cron = "0/1 * *  * * ? ")
    public void hello() {
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
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
                        service.insert(new ClipboardModel(fileLists.toString(), ClipboardModel.CLIPBOARD_CONTENT_TYPE_FILES, DKDateTimeUtil.currentTimeStr()));
                    }
                } else if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String ret = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    service.insert(new ClipboardModel(ret, ClipboardModel.CLIPBOARD_CONTENT_TYPE_STR, DKDateTimeUtil.currentTimeStr()));
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
