package com.xjx.bookmeeting.utils.login;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/8/30 9:52
 */
@Data
public class UserInfo implements Serializable {
    private static final long serialVersionUID = -9017793880291711902L;

    private AuthTypeEnum authType;
    private String trustRoot;
    private String corpId;
    private String corpPw;

    public boolean isInvalid() {
        return authType == null || StringUtils.isBlank(trustRoot) || StringUtils.isBlank(corpId) || StringUtils.isBlank(corpPw);
    }

    /**
     * 组装 post body 的字符串
     *
     * @return body String
     */
    public String getBodyString() {
        if (isInvalid()) {
            return null;
        }

        String bodyFormat = "authm=%s&trust_root=%s&corpid=%s&corppw=%s";
        return String.format(bodyFormat, this.authType.getAuthType(), this.trustRoot, this.corpId, this.corpPw);
    }

    /**
     * 认证类型枚举
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
    }

}
