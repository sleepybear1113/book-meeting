package com.xjx.bookmeeting.utils.login;

import com.xjx.bookmeeting.utils.http.HttpHelper;
import com.xjx.bookmeeting.utils.http.HttpResponseHelper;
import com.xjx.bookmeeting.utils.http.enumeration.MethodEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 统一身份认证的登录获取 cookie 的工具类
 *
 * @author XJX
 * @date 2021/8/30 1:06
 */
@Data
@Slf4j
public class NetEaseVerify {
    /**
     * 进行统一身份认证
     *
     * @param url      原网站跳转统一身份认证的 URL
     * @param userInfo 用户登录信息
     * @return 返回认证后的 cookie
     */
    public static List<Cookie> verify(String url, UserInfo userInfo) {
        String redirectLocation = toVerify(url);
        if (StringUtils.isBlank(redirectLocation)) {
            log.warn("============= to verify failed! =============");
            return new ArrayList<>();
        }

        LocationCookie locationCookie = login(redirectLocation, userInfo);
        if (locationCookie == null) {
            log.warn("============= login failed! =============");
            return new ArrayList<>();
        }

        List<Cookie> cookies = returnToOrigin(locationCookie);
        if (CollectionUtils.isEmpty(cookies)) {
            log.warn("============= get cookie failed! =============");
            return new ArrayList<>();
        }

        return cookies;
    }

    /**
     * 转到认证的前置逻辑，从 URL 跳转过去之后，到达的是输入用户名密码的页面
     *
     * @param url 跳转 URL
     * @return 获取到的认证页面的 URL，用来给下一步提供 URL 进行 post
     */
    public static String toVerify(String url) {
        log.info("============== toVerify ==============");
        if (StringUtils.isBlank(url)) {
            return null;
        }

        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
        httpHelper.setUa(LoginConstant.UA);
        HttpResponseHelper response = httpHelper.request();
        if (response == null) {
            log.warn("response is null");
            return null;
        }
        boolean success = response.isResponse2xx3xx();
        if (!success) {
            log.warn("response is not 2xx 3xx");
            return null;
        }

        HttpClientContext context = response.getContext();
        if (context == null) {
            log.warn("context not found");
            return null;
        }
        List<URI> redirectLocations = context.getRedirectLocations();
        if (CollectionUtils.isEmpty(redirectLocations)) {
            log.warn("redirectLocations not found");
            return null;
        }

        URI lastUri = redirectLocations.get(redirectLocations.size() - 1);
        if (lastUri == null) {
            log.warn("redirectLocation is null");
            return null;
        }

        return lastUri.toString();
    }

    /**
     * 进行登录，需要用户信息
     *
     * @param userInfo 用户信息
     * @param url      跳转到登录页的 URL
     * @return 登录成功之后会返回 cookie 和重定向 location
     */
    public static LocationCookie login(String url, UserInfo userInfo) {
        log.info("============== login ==============");
        if (StringUtils.isBlank(url) || userInfo == null || userInfo.isInvalid()) {
            log.warn("invalid");
            return null;
        }

        HttpHelper loginHttpHelper = HttpHelper.makeDefaultTimeoutHttpHelper(url, MethodEnum.METHOD_POST);
        loginHttpHelper.setPostBody(userInfo.getBodyString(), ContentType.APPLICATION_FORM_URLENCODED);
        HttpResponseHelper response = loginHttpHelper.request();
        if (response == null) {
            log.warn("response is null");
            return null;
        }
        boolean success = response.isResponse2xx3xx();
        if (!success) {
            log.warn("response is not 2xx 3xx");
            return null;
        }

        Header[] headers = response.getHeaders();
        if (headers == null || headers.length == 0) {
            return null;
        }
        String location = null;
        for (Header header : headers) {
            String name = header.getName();
            if ("location".equalsIgnoreCase(name)) {
                location = header.getValue();
            }
        }
        if (StringUtils.isBlank(location)) {
            return null;
        }

        CookieStore httpCookieStore = response.getHttpCookieStore();
        List<Cookie> cookies = httpCookieStore.getCookies();
        if (CollectionUtils.isEmpty(cookies)) {
            return null;
        }

        LocationCookie locationCookie = new LocationCookie();
        locationCookie.setLocation(location);
        locationCookie.setCookies(cookies);
        return locationCookie;
    }

    /**
     * 根据 cookie 和重定向 location 进行访问，获取到目标网页的 cookie
     *
     * @param locationCookie 登录页面的 cookie 和重定向 location
     * @return 目标页面的 cookie，即登录成功之后能过使用的 cookie
     */
    private static List<Cookie> returnToOrigin(LocationCookie locationCookie) {
        log.info("============== returnToOrigin ==============");
        if (locationCookie == null || locationCookie.isInvalid()) {
            return new ArrayList<>();
        }
        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(locationCookie.location);
        httpHelper.setCookieHeaders(locationCookie.cookies);
        httpHelper.setUa(LoginConstant.UA);
        HttpResponseHelper response = httpHelper.request();
        if (response == null) {
            log.warn("response is null");
            return new ArrayList<>();
        }
        boolean success = response.isResponse2xx3xx();
        if (!success) {
            log.warn("response is not 2xx 3xx");
            return new ArrayList<>();
        }
        return response.getHttpCookieStore().getCookies();
    }

    /**
     * 这是一个封装的类，用来存放重定向 location 和 cookie
     */
    @Data
    static class LocationCookie {
        private String location;
        private List<Cookie> cookies;

        public boolean isInvalid() {
            return StringUtils.isBlank(location) || CollectionUtils.isEmpty(cookies);
        }
    }
}
