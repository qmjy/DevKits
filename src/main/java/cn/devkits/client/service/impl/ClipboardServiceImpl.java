package cn.devkits.client.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.devkits.client.dao.ClipboardDao;
import cn.devkits.client.service.ClipboardService;

@Service
public class ClipboardServiceImpl implements ClipboardService {

    @Autowired
    private ClipboardDao dao;

    @Override
    public int insert() {
        System.out.println("sdfgsdf");
        return 0;
    }

}
