package com.xjx.bookmeeting.login;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/9/2 1:18
 */
@Data
@NoArgsConstructor
public class UserCookieInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 8513878974230668976L;

    private static final String COOKIE_SESSION = "JSESSIONID";
    private static final String COOKIE_LOGIN_ID_WEAVER = "loginidweaver";

    private Cookie cookie;
    private String loginIdWeaver;

    public UserCookieInfo(String cookie, String id) {
        this.cookie = new BasicClientCookie(COOKIE_SESSION, cookie);
        this.loginIdWeaver = id;
    }

    public static UserCookieInfo build(List<Cookie> cookies) {
        UserCookieInfo res = new UserCookieInfo();
        // 抽取 cookie
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if (COOKIE_SESSION.equals(name)) {
                res.setCookie(cookie);
            } else if (COOKIE_LOGIN_ID_WEAVER.equals(name)) {
                res.setLoginIdWeaver(cookie.getValue());
            }
        }

        return res;
    }

    public boolean isValid() {
        return cookie != null && loginIdWeaver != null;
    }

    public static boolean isValid(UserCookieInfo userCookieInfo) {
        if (userCookieInfo == null) {
            return false;
        }
        return userCookieInfo.isValid();
    }
}
