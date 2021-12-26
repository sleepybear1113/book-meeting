package com.xjx.bookmeeting.actions;

import com.alibaba.fastjson.JSON;
import com.xjx.bookmeeting.dto.BookRoomResult;
import com.xjx.bookmeeting.dto.SignInRoomResponse;
import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.utils.http.HttpHelper;
import com.xjx.bookmeeting.utils.http.HttpResponseHelper;
import com.xjx.bookmeeting.utils.http.enumeration.MethodEnum;
import com.xjx.bookmeeting.utils.login.LoginConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;

/**
 * 会议室签到
 *
 * @author xjx
 * @date 2021/12/25 19:51
 */
@Slf4j
public class SignInRoomAction {
    public static final String URL = "https://meeting.oa.netease.com/service/meetingNew/getmeetingOperation.jsp?";

    public static SignInRoomResponse signIn(UserCookieInfo userCookieInfo, BookRoomResult bookRoomResult, Integer areaId, String email) {
        if (bookRoomResult == null || !BookRoomResult.isBookSuccess(bookRoomResult)) {
            log.warn("预定失败，无法进行签到");
            return null;
        }
        return signIn(userCookieInfo, new FormData(bookRoomResult.getMeetingId(), email, areaId));
    }

    public static SignInRoomResponse signIn(UserCookieInfo userCookieInfo, Long meetingId, Integer areaId, String email) {
        return signIn(userCookieInfo, new FormData(meetingId, email, areaId));
    }

    public static SignInRoomResponse signIn(UserCookieInfo userCookieInfo, FormData formData) {
        if (userCookieInfo == null || userCookieInfo.getCookie() == null || userCookieInfo.getLoginIdWeaver() == null || formData == null) {
            return null;
        }

        Cookie cookie = userCookieInfo.getCookie();

        HttpHelper httpHelper = HttpHelper.makeDefaultTimeoutHttpHelper(URL, MethodEnum.METHOD_POST);
        httpHelper.setCookieHeader(cookie);
        httpHelper.setUa(LoginConstant.UA);
        String body = formData.getFormData();
        if (body == null) {
            return null;
        }
        httpHelper.setPostBody(body, ContentType.APPLICATION_FORM_URLENCODED);
        HttpResponseHelper response = httpHelper.request();
        String responseBody = response.getResponseBody();
        log.info(responseBody);
        SignInRoomResponse signInRoomResponse = JSON.parseObject(response.getResponseBody(), SignInRoomResponse.class);
        if (signInRoomResponse == null) {
            log.warn("response is null");
            return null;
        }

        if (!signInRoomResponse.signInSuccess()) {
            log.warn(signInRoomResponse.getResult());
        }

        return signInRoomResponse;
    }

    @Data
    public static class FormData {
        Long meetingId;
        String oprt = "signMan";
        String email;
        String signTime = "";
        String detail = "";
        Integer areaId;

        public FormData() {
        }

        public FormData(Long meetingId, String email, Integer areaId) {
            this.meetingId = meetingId;
            this.email = email;
            this.areaId = areaId;
        }

        public String getFormData() {
            if (this.meetingId == null || areaId == null || StringUtils.isBlank(this.email)) {
                return null;
            }
            String format = "meentingId=%s&oprt=signMan&email=%s&signTime=&detail=&areaId=%s";
            return String.format(format, this.meetingId, this.email, this.areaId);
        }
    }
}
