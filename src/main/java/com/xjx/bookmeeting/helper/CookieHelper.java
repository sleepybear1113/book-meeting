package com.xjx.bookmeeting.helper;

import com.xjx.bookmeeting.dto.UserDto;
import com.xjx.bookmeeting.exception.FrontException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Cookie 的 helper 类
 *
 * @author xjx
 * @date 2021/10/13 9:37
 */
public class CookieHelper {
    /**
     * cookie 名
     */
    public static final String USER_ID = "userId";

    /**
     * cookie 过期的最大时间
     */
    public static final int COOKIE_MAX_AGE = Integer.MAX_VALUE;

    /**
     * 获取到 userDto 后对浏览器进行写 cookie
     *
     * @param userDto userDto
     */
    public static void writeLoginCookie(UserDto userDto) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            return;
        }
        HttpServletResponse response = servletRequestAttributes.getResponse();
        if (userDto == null || response == null) {
            return;
        }

        Cookie cookieUsername = new Cookie(USER_ID, String.valueOf(userDto.getId()));
        cookieUsername.setPath("/");
        cookieUsername.setMaxAge(COOKIE_MAX_AGE);

        response.addCookie(cookieUsername);
    }

    /**
     * 从 HttpServletRequest 中读取 user 信息
     *
     * @return UserDto
     */
    public static UserDto getLoginCookie() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();

        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }

        UserDto userDto = null;
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            String value = cookie.getValue();
            if (USER_ID.equals(name)) {
                if (StringUtils.isNumeric(value)) {
                    userDto = new UserDto();
                    userDto.setId(Integer.valueOf(value));
                }
            }
        }

        return userDto;
    }

    /**
     * 从 HttpServletRequest 中读取 user 信息，若无则抛异常
     *
     * @return UserDto
     */
    public static UserDto getLoginCookieWithFrontException() {
        UserDto userDto = getLoginCookie();
        if (userDto == null) {
            throw new FrontException("cookie 未包含用户信息，请重新登录");
        }
        return userDto;
    }
}
