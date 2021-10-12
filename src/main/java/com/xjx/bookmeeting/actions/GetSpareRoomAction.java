package com.xjx.bookmeeting.actions;

import com.alibaba.fastjson.JSON;
import com.xjx.bookmeeting.dto.MeetingResponse;
import com.xjx.bookmeeting.dto.Room;
import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.utils.OtherUtils;
import com.xjx.bookmeeting.utils.http.HttpHelper;
import com.xjx.bookmeeting.utils.http.HttpResponseHelper;
import com.xjx.bookmeeting.utils.login.LoginConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.Cookie;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/8/31 1:45
 */
@Slf4j
public class GetSpareRoomAction {
    public static final String URL = "https://meeting.oa.netease.com/service/meetingNew/getmeetingRoom.jsp?";

    public static List<Room> getRooms(Query query, UserCookieInfo userCookieInfo) {
        if (query == null || userCookieInfo == null || !userCookieInfo.isValid()) {
            return new ArrayList<>();
        }
        String url = query.getUrl();
        if (url == null) {
            return new ArrayList<>();
        }

        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
        httpHelper.setCookieHeader(userCookieInfo.getCookie());
        httpHelper.setUa(LoginConstant.UA);
        HttpResponseHelper response = httpHelper.request();
        MeetingResponse<Room> meetingResponse = JSON.parseObject(response.getResponseBody(), Room.getTypeReference());
        boolean success = MeetingResponse.isSuccess(meetingResponse);
        if (success) {
            return meetingResponse.getResult();
        }
        return new ArrayList<>();
    }

    @Data
    public static class Query {
        private static final Calendar CALENDAR = Calendar.getInstance();
        private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        private Integer pageSize = 100;
        private Integer pageIndex = 1;
        private Integer areaId;
        private Integer roomStatus;
        private Date fromDate;
        private Date toDate;
        private Integer buildingId;
        private Long[] floorId;
        private String floorName;
        private String roomFunctionValue;
        private Integer peopleNum;
        private Integer spareMode = 0;

        public void setFloorId(Long... floorId) {
            this.floorId = floorId;
        }

        public void setDate(Long time) {
            Date date = new Date(time);
            setDate(date);
        }

        public void setDate(Integer year, Integer month, Integer day) {
            CALENDAR.setTime(new Date());
            CALENDAR.set(Calendar.YEAR, year);
            CALENDAR.set(Calendar.MONTH, month - 1);
            CALENDAR.set(Calendar.DAY_OF_MONTH, day);
            setDate(CALENDAR.getTime());
        }

        public void setDate(Date date) {
            if (date == null) {
                date = new Date(System.currentTimeMillis());
            }

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

        public String getUrl() {
            if (pageIndex == null) {
                pageIndex = 1;
            }
            if (pageSize == null) {
                pageSize = 100;
            }
            if (fromDate == null || toDate == null) {
                log.warn("GetSpareRoomAction.Query.getUrl: date is null");
                return null;
            }

            String fromDateString = OtherUtils.urlEncodeUtf8(SDF.format(fromDate));
            String toDateString = OtherUtils.urlEncodeUtf8(SDF.format(toDate));

            String format = "oprt=getSpareRoom&pageSize=%s&pageIndex=%s&areaId=%s&roomStatus=%s&fromDate=%s&toDate=%s&buildingId=%s&floorId=%s&floorName=%s&roomFunctionValue=&peopleNum=&spareMode=%s";
            String url = String.format(format, pageSize, pageIndex, areaId, roomStatus, fromDateString, toDateString, buildingId, StringUtils.join(floorId, ","), floorName, roomFunctionValue, peopleNum, spareMode);
            return URL + url.replace("null", "");
        }
    }
}
