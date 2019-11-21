package cn.devkits.client.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.devkits.client.asyn.DKAsyncService;
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
            LOGGER.info("Insert clipboard data...");
            return dao.insert(model);
        }
        return 1;
    }

}
