package cn.devkits.client.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import cn.devkits.client.dao.ClipboardDao;
import cn.devkits.client.model.ClipboardModel;
import cn.devkits.client.service.ClipboardService;

@Service
public class ClipboardServiceImpl implements ClipboardService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClipboardServiceImpl.class);

    private ClipboardModel lastClipboardModel;

    @Autowired
    private ClipboardDao dao;

    @Override
    public int insert(ClipboardModel model) {
        if (!model.equals(lastClipboardModel)) {
            lastClipboardModel = model;
            return dao.insert(model);
        }
        return 1;
    }

    @Override
    public List<ClipboardModel> findByPaging(int page, int pageSize) {
        if (page > 0 && pageSize > 0) {
            return dao.paging(page, pageSize);
        }
        return Lists.newArrayList();
    }
}
