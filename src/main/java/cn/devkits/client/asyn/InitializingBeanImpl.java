package cn.devkits.client.asyn;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.devkits.client.model.SysConfig;
import cn.devkits.client.service.SysConfigService;

import java.util.Optional;
import java.util.UUID;

/**
 * 程序启动后执行
 *
 * @author sfliue
 */
@Service
public class InitializingBeanImpl implements InitializingBean {

    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public void afterPropertiesSet() throws Exception {
        initUuid();
    }

    private void initUuid() {
        Optional<SysConfig> cfgOptional = sysConfigService.findByKey(SysConfigService.SYS_CFG_UUID);
        if (cfgOptional.isPresent()) {
            SysConfig sysConfig = cfgOptional.get();
        } else {
            UUID uuid = UUID.randomUUID();
            sysConfigService.addSysConfig(new SysConfig(SysConfigService.SYS_CFG_UUID, uuid.toString()));
        }
    }
}