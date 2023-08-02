package cn.devkits.client.service.impl;

import cn.devkits.client.service.OsService;
import org.springframework.stereotype.Service;
import oshi.PlatformEnum;
import oshi.SystemInfo;

@Service
public class OsServiceImpl implements OsService {

    @Override
    public PlatformEnum getCurrentOs() {
     return SystemInfo.getCurrentPlatform();
    }
}
