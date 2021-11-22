package com.xjx.bookmeeting.actions;

import com.alibaba.fastjson.JSON;
import com.xjx.bookmeeting.dto.MeetingBooked;
import com.xjx.bookmeeting.dto.MeetingResponse;
import com.xjx.bookmeeting.dto.Room;
import com.xjx.bookmeeting.utils.http.HttpHelper;
import com.xjx.bookmeeting.utils.http.HttpResponseHelper;
import com.xjx.bookmeeting.utils.login.LoginConstant;
import lombok.Data;
import org.apache.http.cookie.Cookie;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 获取会议室预定情况逻辑类
 *
 * @author XJX
 * @date 2021/9/2 1:58
 */
public class GetRoomBookedAction {
    public static final String URL = "https://meeting.oa.netease.com/service/meetingNew/getmeetingOperation.jsp?oprt=getRoomBook&";

    public static List<MeetingBooked> getBookedRoom(Query query, Cookie cookie) {
        if (query == null) {
            return new ArrayList<>();
        }
        String url = query.getUrl();
        if (url == null) {
            return new ArrayList<>();
        }

        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
        httpHelper.setCookieHeader(cookie);
        httpHelper.setUa(LoginConstant.UA);
        HttpResponseHelper response = httpHelper.request();
        String responseBody = response.getResponseBody();
        Room room = JSON.parseObject(responseBody, Room.class);
        if (room != null && Boolean.TRUE.equals(room.getResultType())) {
            return room.getResult();
        }
        MeetingResponse<Object> objectMeetingResponse = JSON.parseObject(responseBody, MeetingResponse.getObjectTypeReference());
        MeetingResponse.isSuccess(objectMeetingResponse);
        return new ArrayList<>();
    }

    @Data
    public static class Query {
        private static final Calendar CALENDAR = Calendar.getInstance();
        private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        private Long roomId;
        private String meetingStatus;
        private Integer areaId;
        private Date fromDate;
        private Date toDate;

        public void setTodayDate() {
            Date date = new Date(System.currentTimeMillis());
            CALENDAR.setTime(date);
            CALENDAR.set(Calendar.HOUR_OF_DAY, 9);
            CALENDAR.set(Calendar.MINUTE, 0);
            CALENDAR.set(Calendar.SECOND, 0);
            CALENDAR.set(Calendar.MILLISECOND, 0);
            fromDate = CALENDAR.getTime();

            CALENDAR.setTime(date);
            CALENDAR.set(Calendar.HOUR_OF_DAY, 21);
            CALENDAR.set(Calendar.MINUTE, 0);
            CALENDAR.set(Calendar.SECOND, 0);
            CALENDAR.set(Calendar.MILLISECOND, 0);
            toDate = CALENDAR.getTime();
        }

        public void setNullDate() {
            fromDate = null;
            toDate = null;
        }

        public String getUrl() {
            if (roomId == null) {
                return null;
            }

            String fromDateString = null;
            String toDateString = null;
            try {
                if (fromDate != null) {
                    fromDateString = URLEncoder.encode(SDF.format(fromDate), "UTF-8");
                }
                if (toDate != null) {
                    toDateString = URLEncoder.encode(SDF.format(toDate), "UTF-8");
                }
            } catch (UnsupportedEncodingException ignored) {
            }

            String format = "roomId=%s&fromDate=%s&toDate=%s&meetingstatus=%s&areaId=%s";
            String url = String.format(format, roomId, fromDateString, toDateString, meetingStatus, areaId);
            return URL + url.replace("null", "");
        }
    }
}
