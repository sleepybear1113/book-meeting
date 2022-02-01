package com.xjx.bookmeeting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <strong>entity 公用父类</strong><br/>
 * 内含 id、createTime、modifyTime 字段
 *
 * @author xjx
 * @date 2021/10/7 20:29
 */
@Data
@Slf4j
public abstract class BaseDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = -8465232923281938510L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("create_time")
    private Long createTime;
    @TableField("modify_time")
    private Long modifyTime;

    public void fillAllTime() {
        long now = System.currentTimeMillis();
        this.createTime = now;
        this.modifyTime = now;
    }

    public void fillModifyTime() {
        this.modifyTime = System.currentTimeMillis();
    }

    /**
     * 每个子类都需要 override 该方法，来提供自己的表结构<br/>
     * 先在这里写上 return ""，选择 Language inject setting 选择 SQLite，这样子类也能有代码高亮
     *
     * @return 建表语句
     */
    public abstract String tableSql();

    /**
     * 判断数据库子类的 table 是否存在，若不存在则新建<br/>
     * 通过 {@link TableName} 注解来获取表名，判断表的存在性，如果不存在，调用建表语句建表<br/>
     * 建表方式：每个子类下都有一个 tableSql() 方法，调用即可
     *
     * @param connection 数据库连接
     * @throws SQLException SQLException
     */
    public void createTableIfNotExist(Connection connection) throws SQLException {
        Class<? extends BaseDomain> clazz = this.getClass();
        TableName annotation = clazz.getAnnotation(TableName.class);
        if (annotation == null) {
            log.warn("class " + clazz + " has no annotation");
            return;
        }
        String tableName = annotation.value();
        if (tableExist(connection, tableName)) {
            return;
        }

        PreparedStatement ps = connection.prepareStatement(tableSql());
        ps.executeUpdate();
        log.info(">>> create table " + tableName);
        ps.close();
    }

    /**
     * 判断 SQLite 数据库表是否存在
     *
     * @param connection 数据库连接
     * @param tableName  数据库表名
     * @return 是否存在
     */
    private boolean tableExist(Connection connection, String tableName) {
        String sql = """
                SELECT COUNT(*)
                from sqlite_master
                where type = 'table'
                  and name = ?""";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, tableName);
            ResultSet resultSet = ps.executeQuery();
            long count = resultSet.getLong(1);
            if (count > 0) {
                log.info("table " + tableName + " exists");
                return true;
            } else {
                log.info("table " + tableName + " not exists");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking database table existence!");
        }
    }
}
