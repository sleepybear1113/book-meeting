package com.xjx.bookmeeting.domain;

import com.xjx.bookmeeting.enumeration.CanBookEnum;
import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.utils.OtherUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/7 19:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class User extends BaseDomain implements Serializable {
    private static final long serialVersionUID = -4796847559746657876L;

    private String username;
    private String password;
    private String authType;

    private String cookie;
    private String loginIdWeaver;

    private List<BookMeetingInfo> bookMeetingInfoList;

    public void withEncryptSetPassword(String password) {
        if (StringUtils.isBlank(password)) {
            this.password = password;
        } else {
            this.password = OtherUtils.encrypt(password);
        }
    }

    public String withDecryptGetPassword() {
        if (StringUtils.isBlank(this.password)) {
            return this.password;
        } else {
            return OtherUtils.decrypt(this.password);
        }
    }

    public boolean isValid() {
        return !StringUtils.isBlank(this.username) && !StringUtils.isBlank(this.password) && !StringUtils.isBlank(this.authType);
    }

    public boolean removeExpired() {
        if (CollectionUtils.isEmpty(this.bookMeetingInfoList)) {
            return false;
        }
        int size = this.bookMeetingInfoList.size();
        this.bookMeetingInfoList.removeIf(o -> o == null || o.isInvalid() || CanBookEnum.isExpire(o.canBook()));
        return size != this.bookMeetingInfoList.size();
    }

    public UserCookieInfo getUserCookieInfo() {
        if (StringUtils.isBlank(this.cookie) || StringUtils.isBlank(this.loginIdWeaver)) {
            return null;
        }

        return new UserCookieInfo(this.cookie, this.loginIdWeaver);
    }
}
