package com.xjx.bookmeeting.helper;

import com.xjx.bookmeeting.domain.User;
import com.xjx.bookmeeting.exception.FrontException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie 的 helper 类
 *
 * @author xjx
 * @date 2021/10/13 9:37
 */
public class CookieHelper {
    public static final String COOKIE_NAME_USERNAME = "username";
    public static final String COOKIE_NAME_AUTH_TYPE = "authType";

    /**
     * 最大时间
     */
    public static final int COOKIE_MAX_AGE = Integer.MAX_VALUE;

    /**
     * 获取到 user 后对浏览器进行写 cookie
     *
     * @param user user
     */
    public static void writeLoginCookie(User user) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            return;
        }
        HttpServletResponse response = servletRequestAttributes.getResponse();
        if (user == null || response == null) {
            return;
        }

        Cookie cookieUsername = new Cookie(COOKIE_NAME_USERNAME, user.getUsername());
        cookieUsername.setPath("/");
        cookieUsername.setMaxAge(COOKIE_MAX_AGE);

        Cookie cookieAuthType = new Cookie(COOKIE_NAME_AUTH_TYPE, user.getAuthType());
        cookieAuthType.setPath("/");
        cookieAuthType.setMaxAge(COOKIE_MAX_AGE);

        response.addCookie(cookieUsername);
        response.addCookie(cookieAuthType);
    }

    /**
     * 从 HttpServletRequest 中读取 user 信息
     *
     * @return User
     */
    public static User getLoginCookie() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();

        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }

        User user = new User();
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            String value = cookie.getValue();
            if (COOKIE_NAME_USERNAME.equals(name)) {
                user.setUsername(value);
            } else if (COOKIE_NAME_AUTH_TYPE.equals(name)) {
                user.setAuthType(value);
            }
        }

        return user;
    }

    /**
     * 从 HttpServletRequest 中读取 user 信息，若无则抛异常
     *
     * @return User
     */
    public static User getLoginCookieWithFrontException() {
        User user = getLoginCookie();
        if (user == null) {
            FrontException.throwCommonFrontException("cookie 未包含用户信息，请重新登录");
        }
        return user;
    }
}
