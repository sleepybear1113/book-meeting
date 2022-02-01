package com.xjx.bookmeeting.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 用户相关类，保存用户名、密码等信息
 *
 * @author xjx
 * @date 2022/1/29 21:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseDomain {
    @Serial
    private static final long serialVersionUID = -931952178333678599L;

    @TableField(value = "username")
    private String username;
    @TableField("password")
    private String password;

    /**
     * 账号类型，见 {@link com.xjx.bookmeeting.utils.login.AuthTypeEnum}
     */
    @TableField("auth_type")
    private String authType;

    /**
     * 预定系统分配的 id
     */
    @TableField("login_id_weaver")
    private String loginIdWeaver;

    @Override
    public String tableSql() {
        return """
                CREATE TABLE IF NOT EXISTS user
                (
                    id              INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    create_time     BIGINT                            NOT NULL,
                    modify_time     BIGINT                            NOT NULL,
                    username        TEXT                              NOT NULL,
                    password        TEXT                              NOT NULL,
                    auth_type       TEXT                              NOT NULL,
                    login_id_weaver TEXT                              NOT NULL
                )""";
    }
}
