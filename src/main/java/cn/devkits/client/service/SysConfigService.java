package cn.devkits.client.service;


import cn.devkits.client.model.SysConfig;

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
public interface SysConfigService {

    /**
     * 系统唯一UUID
     */
    public static final String SYS_CFG_UUID = "SYS_CFG_UUID";

    /**
     * 添加系统配置
     *
     * @param sysConfig 系统配置
     */
    void addSysConfig(SysConfig sysConfig);

    /**
     * 通过key查询系统配置
     *
     * @param key 系统配置KEY
     * @return 系统配置值
     */
    Optional<SysConfig> findByKey(String key);

    /**
     * 查询系统所有配置
     *
     * @return 系统配置
     */
    List<SysConfig> findAllCfgs();
}
