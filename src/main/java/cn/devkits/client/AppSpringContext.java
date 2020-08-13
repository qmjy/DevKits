/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.sqlite.SQLiteDataSource;

import java.io.File;

/**
 * Beans register
 *
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年11月19日 下午9:50:59
 */
@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan({"cn.devkits.client", "cn.devkits.client.asyn", "cn.devkits.client.beans"})
@MapperScan("cn.devkits.client.mapper")
public class AppSpringContext {

    @Value("${jdbcDriver}")
    private String driver;

    @Value("${jdbcUrlPrefix}")
    private String jdbcUrlPrefix;

    @Value("${jdbcFileName}")
    private String jdbcFileName;

    private SQLiteDataSource ds;


    /**
     * https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE
     *
     * @return datasource
     */
    @Bean("dataSource")
    @Lazy(false)
    public DataSource dataSource() {
        if (ds == null) {
            File f = new File(DKConstants.DEVKIT_WORKSPACE);
            if (!f.exists()) {
                f.mkdirs();
            }
            return createDataSource();
        } else {
            return ds;
        }
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        return factoryBean.getObject();
    }

    private DataSource createDataSource() {
        this.ds = new SQLiteDataSource();
        ds.setUrl(jdbcUrlPrefix + DKConstants.DEVKIT_WORKSPACE + jdbcFileName);
        return ds;
    }
}
