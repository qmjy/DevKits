package cn.devkits.client.service;

import java.util.List;
import cn.devkits.client.model.ClipboardModel;

/**
 * 
 * 剪贴板数据库访问层
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年11月20日 下午10:35:37
 */
public interface ClipboardService {
    int insert(ClipboardModel model);

    List<ClipboardModel> findByPaging(int page, int pageSize);
}
