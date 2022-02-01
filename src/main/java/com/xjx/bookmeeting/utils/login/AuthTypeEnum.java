package com.xjx.bookmeeting.utils.login;

import org.apache.commons.lang3.StringUtils;

/**
 * 认证类型枚举
 *
 * @author xjx
 * @date 2021/12/25 22:11
 */
public enum AuthTypeEnum {
    /**
     * corp 邮箱认证类型
     */
    CORP("corp"),
    /**
     * mesg.corp 域认证类型
     */
    MESG_CORP("mesg"),
    /**
     * 翼信邮箱认证类型
     */
    YI_XIN("yixin"),
    /**
     * 易现账号认证类型
     */
    YI_XIAN("ezxr"),
    ;
    private final String authType;

    AuthTypeEnum(String authType) {
        this.authType = authType;
    }

    public String getAuthType() {
        return authType;
    }

    public static AuthTypeEnum getAuthTypeEnum(String authType) {
        for (AuthTypeEnum authTypeEnum : AuthTypeEnum.values()) {
            if (authTypeEnum.getAuthType().equals(authType)) {
                return authTypeEnum;
            }
        }

        return null;
    }

    public static String getEmail(String id, AuthTypeEnum authTypeEnum) {
        if (StringUtils.isBlank(id) || authTypeEnum == null) {
            return null;
        }

        return switch (authTypeEnum) {
            case CORP -> id + "@corp.netease.com";
            case MESG_CORP -> id + "mesg.corp.netease.com";
            case YI_XIN -> id + "@yixin.im";
            case YI_XIAN -> id;
        };
    }
}