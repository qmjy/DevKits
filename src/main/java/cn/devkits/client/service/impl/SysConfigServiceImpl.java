package cn.devkits.client.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.devkits.client.mapper.SysConfigMapper;
import cn.devkits.client.model.SysConfig;
import cn.devkits.client.service.SysConfigService;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 *
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2021/11/3
 */
@Service
public class SysConfigServiceImpl implements SysConfigService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public void addSysConfig(SysConfig sysConfig) {
        sysConfigMapper.addSysConfig(sysConfig);
    }

    @Override
    public Optional<SysConfig> findByKey(String key) {
        SysConfig byKey = sysConfigMapper.findByKey(key);
        if (byKey == null) {
            return Optional.empty();
        } else {
            return Optional.of(byKey);
        }
    }

    @Override
    public List<SysConfig> findAllCfgs() {
        return sysConfigMapper.findAllCfgs();
    }
}
