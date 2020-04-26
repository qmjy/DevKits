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
import com.alibaba.druid.pool.DruidDataSource;

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
@MapperScan("cn.devkits.client.dao")
public class AppSpringContext {

    @Value("${jdbcDriver}")
    private String driver;

    @Value("${jdbcUrlPrefix}")
    private String jdbcUrlPrefix;

    @Value("${jdbcFileName}")
    private String jdbcFileName;

    private DruidDataSource ds;


    /**
     * https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE
     *
     * @return datasource
     */
    @Bean("dataSource")
    @Lazy(false)
    public DataSource dataSource() {
        if (ds == null) {
            File f = new File(DKConstant.DEVKIT_WORKSPACE);
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
        this.ds = new DruidDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(jdbcUrlPrefix + DKConstant.DEVKIT_WORKSPACE + jdbcFileName);

        ds.setDefaultAutoCommit(true);// 自动提交事务

        ds.setMaxActive(20);
        ds.setInitialSize(1);
        ds.setMaxWait(60000);
        ds.setMinIdle(1);
        ds.setTimeBetweenEvictionRunsMillis(60000);// 每60秒运行一次空闲连接回收器
        ds.setMinEvictableIdleTimeMillis(30000);// 池中的连接空闲30秒钟后被回收,默认值就是30分钟。

        ds.setPoolPreparedStatements(false);
        ds.setMaxPoolPreparedStatementPerConnectionSize(20);

        ds.setTestWhileIdle(true);
        ds.setTestOnBorrow(false);// 借出连接时不要测试，否则很影响性能
        ds.setTestOnReturn(false);

        ds.setTestWhileIdle(true);// 指明连接是否被空闲连接回收器(如果有)进行检验.如果检测失败,则连接将被从池中去除.
        ds.setValidationQuery("SELECT 1");// 验证连接是否可用，使用的SQL语句

        ds.setPoolPreparedStatements(true);
        ds.setMaxOpenPreparedStatements(0);
        ds.setAsyncInit(true);
        return ds;
    }

}
