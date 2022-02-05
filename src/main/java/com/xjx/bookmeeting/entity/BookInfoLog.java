package com.xjx.bookmeeting.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 用户维度预定日志
 *
 * @author xjx
 * @date 2022/2/5 11:39
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BookInfoLog extends BaseDomain {
    @Serial
    private static final long serialVersionUID = -6636375045046367330L;

    /**
     * 参见字段 {@link User#id}
     */
    @TableField("user_id")
    private Integer userId;
    @TableField("log")
    private String log;

    @Override
    public String tableSql() {
        return """
                CREATE TABLE IF NOT EXISTS book_info_log
                (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    create_time BIGINT                            NOT NULL,
                    modify_time BIGINT                            NOT NULL,
                    user_id     INTEGER                           NOT NULL,
                    log         TEXT                              NOT NULL
                )""";
    }
}
