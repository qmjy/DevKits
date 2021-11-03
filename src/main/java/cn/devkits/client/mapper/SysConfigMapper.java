package cn.devkits.client.mapper;

import cn.devkits.client.model.SysConfig;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统配置
 *
 * @author sfliue
 */
@Mapper
public interface SysConfigMapper {

    /**
     * 新增系统配置数据
     *
     * @param sysConfig 系统配置信息
     */
    @Insert({"INSERT INTO devkits_system(key, value, createTime) values(#{model.key}, #{model.value}, datetime(CURRENT_TIMESTAMP,'localtime'))"})
    void addSysConfig(@Param("model") SysConfig sysConfig);

    /**
     * 查询关键Key查询系统配置
     *
     * @param key 系统配置KEY
     * @return 系统配置Value
     */
    @Select({"SELECT * FROM devkits_system WHERE key = #{key}"})
    SysConfig findByKey(@Param("key") String key);

    /**
     * 查询所有系统配置
     *
     * @return 所有系统配置
     */
    @Select({"SELECT * FROM devkits_system"})
    List<SysConfig> findAllCfgs();
}
