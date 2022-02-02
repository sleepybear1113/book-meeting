package com.xjx.bookmeeting.enumeration;

import org.apache.commons.lang3.StringUtils;

/**
 * 预定时间枚举
 *
 * @author xjx
 * @date 2021/10/7 14:58
 */
public enum TimeEnum {
    /**
     * 时间枚举
     */
    T_0800(800, "08:00", "0800"),
    T_0830(830, "08:30", "0830"),
    T_0900(900, "09:00", "0900"),
    T_0930(930, "09:30", "0930"),
    T_1000(1000, "10:00", "1000"),
    T_1030(1030, "10:30", "1030"),
    T_1100(1100, "11:00", "1100"),
    T_1130(1130, "11:30", "1130"),
    T_1200(1200, "12:00", "1200"),
    T_1230(1230, "12:30", "1230"),
    T_1300(1300, "13:00", "1300"),
    T_1330(1330, "13:30", "1330"),
    T_1400(1400, "14:00", "1400"),
    T_1430(1430, "14:30", "1430"),
    T_1500(1500, "15:00", "1500"),
    T_1530(1530, "15:30", "1530"),
    T_1600(1600, "16:00", "1600"),
    T_1630(1630, "16:30", "1630"),
    T_1700(1700, "17:00", "1700"),
    T_1730(1730, "17:30", "1730"),
    T_1800(1800, "18:00", "1800"),
    T_1830(1830, "18:30", "1830"),
    T_1900(1900, "19:00", "1900"),
    T_1930(1930, "19:30", "1930"),
    T_2000(2000, "20:00", "2000"),
    T_2030(2030, "20:30", "2030"),
    T_2100(2100, "21:00", "2100"),
    T_2130(2130, "21:30", "2130"),
    T_2200(2200, "22:00", "2200"),
    T_2230(2230, "22:30", "2230"),
    ;

    private final Integer index;
    private final String time;
    private final String timeStr;

    TimeEnum(Integer index, String time, String timeStr) {
        this.index = index;
        this.time = time;
        this.timeStr = timeStr;
    }

    public Integer getIndex() {
        return index;
    }

    public String getTime() {
        return time;
    }

    public String getTimeLong() {
        return time + ":00";
    }

    public String getTimeStr() {
        return timeStr;
    }

    public static TimeEnum getByTime(String timeStr) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        for (TimeEnum value : TimeEnum.values()) {
            if (value.getTimeStr().equals(timeStr) || value.getTime().equals(timeStr) || value.getTimeLong().equals(timeStr)) {
                return value;
            }
        }
        return null;
    }
}
