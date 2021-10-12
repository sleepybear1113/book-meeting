package com.xjx.bookmeeting.actions;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/9/14 15:35
 */
public class CancelBookedRoomAction {
    public static final String URL = "https://meeting.oa.netease.com/service/meetingNew/getmeetingOperation.jsp?oprt=del&";

    @Data
    static class Query {
        private Long meentingId;
        private Integer areaId;

        public String getUrl() {
            if (meentingId == null || areaId == null) {
                return null;
            }

            return URL + "meentingId=" + meentingId + "areaId=" + areaId;
        }
    }
}
