package com.xjx.bookmeeting.actions;

import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.utils.http.HttpHelper;
import com.xjx.bookmeeting.utils.http.HttpResponseHelper;
import com.xjx.bookmeeting.utils.login.LoginConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.Cookie;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/7 17:02
 */
public class TestLoginValidAction {
    public static final String URL = "https://meeting.oa.netease.com/service/meetingNew/getUserAuthorize.jsp?oprt=getAuthorize";
    public static final String SUCCESS_FETCH = "获取成功";

    /**
     * 登录信息 session 是否还有效
     *
     * @param userCookieInfo userCookieInfo
     * @return boolean
     */
    public static boolean testLogin(UserCookieInfo userCookieInfo) {
        if (userCookieInfo == null || userCookieInfo.getCookie() == null || userCookieInfo.getLoginIdWeaver() == null) {
            return false;
        }

        Cookie cookie = userCookieInfo.getCookie();
        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(URL);
        httpHelper.setCookieHeader(cookie);
        httpHelper.setUa(LoginConstant.UA);
        HttpResponseHelper response = httpHelper.request();
        String responseBody = response.getResponseBody();
        if (StringUtils.isNotBlank(responseBody)) {
            return responseBody.contains(SUCCESS_FETCH);
        }

        return false;
    }
}
