package com.xjx.bookmeeting.actions;

import com.alibaba.fastjson.JSON;
import com.xjx.bookmeeting.dto.UserInfo;
import com.xjx.bookmeeting.exception.FrontException;
import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.utils.http.HttpHelper;
import com.xjx.bookmeeting.utils.http.HttpResponseHelper;
import com.xjx.bookmeeting.utils.login.LoginConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取系统用户系统的查询
 *
 * @author xjx
 * @date 2022/2/17 14:40
 */
public class QueryUserInfoAction {
    public static final String URL = "https://meeting.oa.netease.com/service/hrm/common/getUserInfoByQueryKey.jsp?queryKey=";

    public static List<UserInfo> queryUserInfo(String queryKey, UserCookieInfo userCookieInfo) {
        if (StringUtils.isBlank(queryKey) || userCookieInfo == null || !userCookieInfo.isValid()) {
            return new ArrayList<>();
        }

        for (int i = 0; i < queryKey.length(); i++) {
            char c = queryKey.charAt(i);
            if (c > Byte.MAX_VALUE) {
                throw new FrontException("目前暂时不支持中文字符或者特殊符号");
            }
        }

        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(URL + queryKey);
        httpHelper.setCookieHeader(userCookieInfo.getCookie());
        httpHelper.setUa(LoginConstant.UA);
        HttpResponseHelper response = httpHelper.request();
        String responseBody = response.getResponseBody();

        UserInfo userInfo = JSON.parseObject(responseBody, UserInfo.class);
        return userInfo.getUserInfoList();
    }
}
