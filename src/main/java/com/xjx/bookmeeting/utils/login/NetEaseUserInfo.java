package com.xjx.bookmeeting.utils.login;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/8/30 9:52
 */
@Data
public class NetEaseUserInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -9017793880291711902L;

    private AuthTypeEnum authType;
    private String trustRoot;
    private String corpId;
    private String corpPw;

    public boolean isInvalid() {
        return this.authType == null || StringUtils.isBlank(this.trustRoot) || StringUtils.isBlank(this.corpId) || StringUtils.isBlank(this.corpPw);
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

    public String getEmail() {
        return AuthTypeEnum.getEmail(this.corpId, this.authType);
    }

}
