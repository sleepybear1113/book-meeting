package com.xjx.bookmeeting.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

/**
 * 表变更的 Controller
 *
 * @author xjx
 * @date 2022/2/18 10:03
 */
@RestController
@Slf4j
public class SqlManagerController {

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;
    @Value(("${spring.datasource.driver-class-name}"))
    private String driveClassName;

    private Connection connection;

    @RequestMapping("/sqlManager/addColumn")
    public Boolean addColumn() {
        try {
            Class.forName(this.driveClassName);
            connection = DriverManager.getConnection(this.dataSourceUrl);
        } catch (ClassNotFoundException | SQLException e) {
            log.warn(e.getMessage(), e);
            return false;
        }

        boolean b = addColumnBookMeetingInfoJoinPeople();

        try {
            connection.close();
        } catch (SQLException e) {
            log.warn(e.getMessage(), e);
        }

        return b;
    }

    /**
     * 为 book_meeting_info 表添加 join_people字段
     *
     * @return 是否成功
     */
    private boolean addColumnBookMeetingInfoJoinPeople() {
        String tableName = "book_meeting_info";
        String columnName = "join_people";
        String addColumnSql = """
                alter table book_meeting_info add column join_people TEXT
                """;
        if (!sqlColumnExist(tableName, columnName)) {
            execSql(addColumnSql);
            log.info("add column " + columnName + " for sql [{}]", addColumnSql);
            return true;
        }

        return false;
    }

    /**
     * 某个表的字段是否存在
     *
     * @param table  表名
     * @param column 字段名
     * @return 存在性
     */
    private boolean sqlColumnExist(String table, String column) {
        boolean columnExist = false;
        String sql = """
                select * from sqlite_master where type = 'table' and name = ? and sql like ?
                """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, table);
            ps.setString(2, "%" + column + "%");
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    columnExist = true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return columnExist;
    }

    /**
     * 执行 SQL
     *
     * @param sql SQL
     */
    private void execSql(String sql) {
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            int update = ps.executeUpdate();
            log.info("update = " + update);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
