package com.xjx.bookmeeting.actions;

import com.alibaba.fastjson.JSON;
import com.xjx.bookmeeting.dto.Floor;
import com.xjx.bookmeeting.dto.MeetingResponse;
import com.xjx.bookmeeting.enumeration.AreaTypeEnum;
import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.utils.http.HttpHelper;
import com.xjx.bookmeeting.utils.http.HttpResponseHelper;
import com.xjx.bookmeeting.utils.login.LoginConstant;
import lombok.Data;
import org.apache.http.cookie.Cookie;

import java.util.ArrayList;
import java.util.List;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/8/31 1:47
 */
@Data
public class GetFloorAction {
    public static final String URL = "https://meeting.oa.netease.com/service/meetingNew/getmeetingRoom.jsp?oprt=getFloor&areaId=";

    public static List<Floor> getFloor(UserCookieInfo userCookieInfo, AreaTypeEnum areaType) {
        if (userCookieInfo == null || areaType == null || !userCookieInfo.isValid()) {
            return new ArrayList<>();
        }

        String url = URL + areaType.getAreaId();
        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
        httpHelper.setCookieHeader(userCookieInfo.getCookie());
        httpHelper.setUa(LoginConstant.UA);
        HttpResponseHelper response = httpHelper.request();
        String responseBody = response.getResponseBody();
        MeetingResponse<Floor> meetingResponse = JSON.parseObject(responseBody, Floor.getTypeReference());
        boolean success = MeetingResponse.isSuccess(meetingResponse);
        if (success) {
            return meetingResponse.getResult();
        }
        return new ArrayList<>();
    }
}
