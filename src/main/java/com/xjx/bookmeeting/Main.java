package com.xjx.bookmeeting;

import com.xjx.bookmeeting.login.MeetingLogin;
import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.utils.login.AuthTypeEnum;
import com.xjx.bookmeeting.utils.login.UserInfo;

/**
 * 测试类
 *
 * @author XJX
 * @date 2021/8/30 1:05
 */
public class Main {

    public static void main(String[] args) {
        testLogin();
    }

    public static void testLogin() {
        UserInfo userInfo = new UserInfo();
        userInfo.setAuthType(AuthTypeEnum.CORP);
        userInfo.setCorpPw("");
        userInfo.setCorpId("");
        UserCookieInfo userCookieInfo;

        userCookieInfo = MeetingLogin.loginAndGetCookie(userInfo);
        System.out.println(userCookieInfo);
    }
}
