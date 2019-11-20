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

/**
 * 
 * Beans register
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年11月19日 下午9:50:59
 */
@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan({"cn.devkits.client", "cn.devkits.client.asyn"})
@MapperScan("cn.devkits.client.dao")
public class AppSpringContext {

    @Value("${jdbcDriver}")
    private String driver;

    @Value("${jdbcUrl}")
    private String url;

    @Value("${jdbcUsername}")
    private String user;

    @Value("${jdbcPassword}")
    private String password;

    private DruidDataSource ds;

    /**
     * https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE
     * @param driver
     * @param url
     * @param user
     * @param password
     * @return
     */
    @Bean("dataSource")
    @Lazy(false)
    public DataSource dataSource() {
        if (ds == null) {
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
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);

        ds.setMaxActive(20);
        ds.setInitialSize(1);
        ds.setMaxWait(60000);
        ds.setMinIdle(1);
        ds.setTimeBetweenEvictionRunsMillis(60000);
        ds.setMinEvictableIdleTimeMillis(30000);

        ds.setTestWhileIdle(true);
        ds.setTestOnBorrow(false);
        ds.setTestOnReturn(false);

        ds.setPoolPreparedStatements(true);
        ds.setMaxOpenPreparedStatements(20);
        ds.setAsyncInit(true);
        return ds;
    }

}
