package com.xjx.bookmeeting.config;

import com.xjx.bookmeeting.entity.BaseDomain;
import com.xjx.bookmeeting.entity.BookMeetingInfo;
import com.xjx.bookmeeting.entity.User;
import com.xjx.bookmeeting.entity.UserCookie;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.sql.*;

/**
 * <strong>配置连接数据库前的初始化操作</strong><br/>
 * 初始化 SQLite 的存储位置和数据库表
 *
 * @author xjx
 * @date 2022/1/29 22:56
 */
@Configuration
@MapperScan(basePackages = {"com.xjx.bookmeeting.mapper"})
@Slf4j
public class DataSourceInitConfig {

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;
    @Value(("${spring.datasource.driver-class-name}"))
    private String driveClassName;

    /**
     * 初始化 db
     */
    @Bean
    @Order(100)
    public void initDataSource() {
        try {
            Class.forName(driveClassName);
            Connection connection = DriverManager.getConnection(dataSourceUrl);
            log.info("==========Open database successfully==========");

            for (BaseDomain baseDomain : getTableCreateList()) {
                baseDomain.createTableIfNotExist(connection);
            }

            connection.close();
            log.info("=============database init finish=============");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 要初始化的 domain 类
     *
     * @return BaseDomain[]
     */
    private BaseDomain[] getTableCreateList() {
        return new BaseDomain[]{
                new User(),
                new UserCookie(),
                new BookMeetingInfo(),
        };
    }
}
