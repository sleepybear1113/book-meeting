package com.xjx.bookmeeting.dao;

import com.xjx.bookmeeting.login.UserCookieInfo;
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

    private List<BookOnceInfo> bookOnceInfoList;

    public boolean isValid() {
        return !StringUtils.isBlank(username) && !StringUtils.isBlank(password) && !StringUtils.isBlank(authType);
    }

    public boolean removeExpired() {
        boolean b = removeExpiredOnceInfoList();
        return b;
    }

    public boolean removeExpiredOnceInfoList() {
        if (CollectionUtils.isEmpty(bookOnceInfoList)) {
            return false;
        }
        int size = bookOnceInfoList.size();
        bookOnceInfoList.removeIf(o -> o == null || !o.isValid() || o.getBookTimestamp() == null || o.getBookTimestamp() + 86400 * 1000 < System.currentTimeMillis());
        return size != bookOnceInfoList.size();
    }

    public UserCookieInfo getUserCookieInfo() {
        if (StringUtils.isBlank(cookie) || StringUtils.isBlank(loginIdWeaver)) {
            return null;
        }

        return new UserCookieInfo(cookie, loginIdWeaver);
    }
}
