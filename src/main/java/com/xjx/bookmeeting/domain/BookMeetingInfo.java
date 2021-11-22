package com.xjx.bookmeeting.domain;

import com.xjx.bookmeeting.enumeration.AreaTypeEnum;
import com.xjx.bookmeeting.enumeration.CanBookEnum;
import com.xjx.bookmeeting.enumeration.TimeEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/9 23:53
 */
@Data
public class BookMeetingInfo extends BaseDomain implements Serializable {
    private static final long serialVersionUID = 1777036795428134524L;
    private static final Calendar CALENDAR = Calendar.getInstance();

    private Integer year;
    private Integer month;
    private Integer day;

    /**
     * 按照周预定，如果有这个字段
     */
    private Integer week;

    private String timeBegin;
    private String timeEnd;
    private Integer areaId;
    private Long roomId;
    private String meetingName;
    private String roomName;

    /**
     * 可预订时间字符串，例如 8days、6months
     */
    private String bookTime;

    /**
     * 获取可预订的天数
     *
     * @return Integer
     */
    public Integer getBookTimeDays() {
        if (StringUtils.isBlank(this.bookTime)) {
            return null;
        }
        String yearString = "years";
        String monthString = "months";
        String daysString = "days";

        int years = 0;
        int months = 0;
        int days = 0;

        // 提取年
        if (this.bookTime.contains(yearString)) {
            String[] splitYear = this.bookTime.split(yearString);
            years = parseTime(splitYear);
        }

        // 提取月
        if (this.bookTime.contains(monthString)) {
            String bookTimeMonth = this.bookTime.replace(years + yearString, "");
            String[] splitMonth = bookTimeMonth.split(monthString);
            months = parseTime(splitMonth);
        }

        // 提取日
        if (this.bookTime.contains(daysString)) {
            String bookTimeDay = this.bookTime.replace(years + yearString, "").replace(months + monthString, "");
            String[] splitDay = bookTimeDay.split(daysString);
            days = parseTime(splitDay);
        }

        return 365 * years + 30 * months + days;
    }

    private Integer parseTime(String[] times) {
        if (times == null || times.length == 0) {
            return 0;
        }
        String time = times[0];
        if (StringUtils.isNumeric(time)) {
            return Integer.valueOf(time);
        }
        return 0;
    }

    public CanBookEnum canBook() {
        // 预定时间
        Integer bookTimeDays = getBookTimeDays();
        if (bookTimeDays == null || bookTimeDays <= 0) {
            return CanBookEnum.EXPIRED;
        }

        CALENDAR.setTime(new Date());
        int today = CALENDAR.get(Calendar.DAY_OF_MONTH);
        // 设置时间为当前 + 预定时间 -1，也就是可预订的最早的那天
        CALENDAR.set(Calendar.DAY_OF_MONTH, today + bookTimeDays - 1);

        // 预定时间的年月日周
        int bookYear = CALENDAR.get(Calendar.YEAR);
        int bookMonth = CALENDAR.get(Calendar.MONTH) + 1;
        int bookDay = CALENDAR.get(Calendar.DAY_OF_MONTH);
        int bookWeek = CALENDAR.get(Calendar.DAY_OF_WEEK) - 1;
        // 周日转 7
        bookWeek = bookWeek == 0 ? 7 : bookWeek;

        if (this.week != null) {
            // 设置具体日期，以便直接获取进行预定
            this.day = bookDay;
            this.month = bookMonth;
            this.year = bookYear;

            // 是周期预定
            if (bookWeek == this.week) {
                return CanBookEnum.CAN_BOOK;
            }
            return CanBookEnum.READY;
        } else {
            // 非周期预定，也就是
            if (this.year == null || this.month == null || this.day == null) {
                return CanBookEnum.EXPIRED;
            }

            // 会议室可预订的时间
            Date bookDate = CALENDAR.getTime();
            long bookDateTime = bookDate.getTime();

            // 设置当前预定的时间
            CALENDAR.set(Calendar.DAY_OF_MONTH, this.day);
            CALENDAR.set(Calendar.MONTH, this.month - 1);
            CALENDAR.set(Calendar.YEAR, this.year);
            Date bookInfoDate = CALENDAR.getTime();
            long bookInfoDateTime = bookInfoDate.getTime();

            if (bookDateTime == bookInfoDateTime) {
                return CanBookEnum.CAN_BOOK;
            } else if (bookDateTime < bookInfoDateTime) {
                return CanBookEnum.READY;
            } else {
                return CanBookEnum.EXPIRED;
            }
        }
    }

    public TimeEnum getTimeBeginTimeEnum() {
        return TimeEnum.getByTime(timeBegin);
    }

    public TimeEnum getTimeEndTimeEnum() {
        return TimeEnum.getByTime(timeEnd);
    }

    public AreaTypeEnum getAreaIdEnum() {
        return AreaTypeEnum.getAreaTypeEnum(areaId);
    }

    public boolean isValid() {
        if (areaId == null || roomId == null) {
            return false;
        }
        if (year == null || month == null || day == null) {
            return week != null;
        }

        return getTimeBeginTimeEnum() != null && getTimeEndTimeEnum() != null && getAreaIdEnum() != null;
    }

    public boolean isInvalid() {
        return !isValid();
    }

    public static boolean isWeekStringValid(String weekString) {
        if (StringUtils.isBlank(weekString)) {
            return false;
        }

        for (char c : weekString.toCharArray()) {
            if (c < '1' || c > '7') {
                return false;
            }
        }
        return true;
    }

    public static List<Integer> weekToList(String weekString) {
        List<Integer> weeks = new ArrayList<>();
        weeks.add(null);
        if (StringUtils.isBlank(weekString)) {
            return weeks;
        }
        for (String s : weekString.split("")) {
            if (!StringUtils.isNumeric(s)) {
                continue;
            }

            int week = Integer.parseInt(s);
            if (week >= 1 && week <= 7) {
                weeks.add(week);
            }
        }

        if (weeks.size() > 1) {
            weeks.removeIf(Objects::isNull);
        }
        return weeks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BookMeetingInfo that = (BookMeetingInfo) o;
        return Objects.equals(week, that.week) && Objects.equals(year, that.year) && Objects.equals(month, that.month) && Objects.equals(day, that.day) && Objects.equals(timeBegin, that.timeBegin) && Objects.equals(timeEnd, that.timeEnd) && Objects.equals(areaId, that.areaId) && Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day, timeBegin, timeEnd, areaId, roomId);
    }
}

