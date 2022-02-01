package com.xjx.bookmeeting.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 保存 user 和暂存的 cookie 信息
 *
 * @author xjx
 * @date 2022/1/30 0:56
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_cookie")
public class UserCookie extends BaseDomain {
    @Serial
    private static final long serialVersionUID = 5972595985473300796L;

    /**
     * 参见字段 {@link User#id}
     */
    @TableField("user_id")
    private Integer userId;
    @TableField("cookie")
    private String cookie;

    @Override
    public String tableSql() {
        return """
                CREATE TABLE IF NOT EXISTS user_cookie
                (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    create_time BIGINT                            NOT NULL,
                    modify_time BIGINT                            NOT NULL,
                    user_id     INTEGER                           NOT NULL,
                    cookie      TEXT
                )""";
    }
}
