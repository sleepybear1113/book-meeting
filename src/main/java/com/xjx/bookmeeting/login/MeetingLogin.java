package com.xjx.bookmeeting.login;

import com.xjx.bookmeeting.utils.login.NetEaseUserInfo;
import com.xjx.bookmeeting.utils.login.NetEaseVerifyUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/8/31 1:18
 */
public class MeetingLogin {
    private static final String URL = "https://meeting.oa.netease.com/login/loginOpenId.jsp";
    private static final String TRUST_ROOT = "https://meeting.oa.netease.com";

    public static UserCookieInfo loginAndGetCookie(NetEaseUserInfo netEaseUserInfo) {
        if (netEaseUserInfo == null) {
            return null;
        }

        netEaseUserInfo.setTrustRoot(TRUST_ROOT);
        if (netEaseUserInfo.isInvalid()) {
            return null;
        }

        // 进行登录
        List<Cookie> cookies = NetEaseVerifyUtil.verify(URL, netEaseUserInfo);
        if (CollectionUtils.isEmpty(cookies)) {
            return null;
        }

        return UserCookieInfo.build(cookies);
    }
}
