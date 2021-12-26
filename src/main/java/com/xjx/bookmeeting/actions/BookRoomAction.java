package com.xjx.bookmeeting.actions;

import com.alibaba.fastjson.JSON;
import com.xjx.bookmeeting.dto.BookRoomResult;
import com.xjx.bookmeeting.enumeration.AreaTypeEnum;
import com.xjx.bookmeeting.enumeration.TimeEnum;
import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.utils.OtherUtils;
import com.xjx.bookmeeting.utils.http.HttpHelper;
import com.xjx.bookmeeting.utils.http.HttpResponseHelper;
import com.xjx.bookmeeting.utils.http.enumeration.MethodEnum;
import com.xjx.bookmeeting.utils.login.LoginConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;

import java.util.Calendar;

/**
 * 会议室预定相关操作逻辑类
 *
 * @author XJX
 * @date 2021/9/14 9:56
 */
@Slf4j
public class BookRoomAction {
    public static final String URL = "https://meeting.oa.netease.com/service/meetingNew/getmeetingOperation.jsp";

    public static BookRoomResult book(UserCookieInfo userCookieInfo, FormData formData) {
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
        System.out.println(responseBody);
        BookRoomResult bookRoomResult = JSON.parseObject(responseBody, BookRoomResult.class);
        boolean success = BookRoomResult.isBookSuccess(bookRoomResult);
        return bookRoomResult;
    }

    @Data
    public static class FormData {
        private static final Calendar CALENDAR = Calendar.getInstance();

        private Long meetingRoomId;
        private String timerange;
        private String name = "会议";
        private String joinUserIds;
        private String fromDate_1;
        private String toDate_1;
        private Integer areaId;
        private Integer isSendPopo = 1;
        private Integer isSendEmail = 0;
        private Integer massEmail = 0;

        private String oprt = "add";
        private Integer platformType = 0;
        private Integer crossDay = 1;
        private String presideUserIds;
        private String speechUserIds;
        private String relation;
        private String onSiteSupportRemark;
        private String CcUserIds;
        private String desc_n;

        public void setJoinUserIds(String s) {
            joinUserIds = s;
        }

        public void setJoinUserIds(Long id) {
            if (id != null) {
                joinUserIds = String.valueOf(id);
            }
        }

        public void setAreaId(AreaTypeEnum areaId) {
            if (areaId != null) {
                this.areaId = areaId.getAreaId();
            }
        }

        public void setTimeRange(int year, int month, int day, TimeEnum begin, TimeEnum end) {
            if (begin == null || end == null) {
                return;
            }

            timerange = OtherUtils.urlEncodeUtf8(begin.getTime() + "-" + end.getTime());
            String dayString = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
            fromDate_1 = OtherUtils.urlEncodeUtf8(dayString + " " + begin.getTimeLong());
            toDate_1 = OtherUtils.urlEncodeUtf8(dayString + " " + end.getTimeLong());
        }

        public String getFormData() {
            if (StringUtils.isBlank(joinUserIds) || meetingRoomId == null || areaId == null || timerange == null || fromDate_1 == null || toDate_1 == null) {
                return null;
            }
            String s = "meetingRoomId=" + meetingRoomId +
                    "&timerange=" + timerange +
                    "&name=" + name +
                    "&joinUserIds=" + joinUserIds +
                    "&fromDate_1=" + fromDate_1 +
                    "&toDate_1=" + toDate_1 +
                    "&areaId=" + areaId +
                    "&isSendPopo=" + isSendPopo +
                    "&isSendEmail=" + isSendEmail +
                    "&massEmail=" + massEmail +
                    "&oprt=" + oprt +
                    "&platformType=" + platformType +
                    "&crossDay=" + crossDay +
                    "&presideUserIds=" + presideUserIds +
                    "&speechUserIds=" + speechUserIds +
                    "&relation=" + relation +
                    "&onSiteSupportRemark=" + onSiteSupportRemark +
                    "&CcUserIds=" + CcUserIds +
                    "&desc_n=" + desc_n;
            return s.replace("null", "");
        }
    }
}
