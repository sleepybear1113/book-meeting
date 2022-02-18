package com.xjx.bookmeeting.dto;

import com.xjx.bookmeeting.bo.JoinPeople;
import com.xjx.bookmeeting.enumeration.AreaTypeEnum;
import com.xjx.bookmeeting.enumeration.CanBookEnum;
import com.xjx.bookmeeting.enumeration.TimeEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2022/1/30 1:33
 */
@Data
public class BookMeetingInfoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 6253240213843138989L;

    private static final Calendar CALENDAR = Calendar.getInstance();

    private Long id;
    private Long createTime;
    private Long modifyTime;

    private Integer userId;

    private Integer year;
    private Integer month;
    private Integer day;

    private String dayString;

    /**
     * 按照周预定，如果有这个字段
     */
    private Integer week;
    private String weeks;

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
     * 是否自动签到
     */
    private Integer autoSignIn;

    /**
     * 与会人员<br/>
     * 格式：userId@name,userId@name,userId@name
     */
    private String joinPeople;

    /**
     * 获取年月日<br/>
     *
     * @return 格式为 2022-07-15，若无则 null
     */
    public String getYmd() {
        if (this.year == null) {
            return null;
        }
        return this.year + ((this.month < 10 ? "0" : "") + this.month) + ((this.day < 10 ? "0" : "") + this.day);
    }

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
            // 非周期预定，也就是单次按照天预定
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
        return TimeEnum.getByTime(this.timeBegin);
    }

    public TimeEnum getTimeEndTimeEnum() {
        return TimeEnum.getByTime(this.timeEnd);
    }

    public AreaTypeEnum getAreaIdEnum() {
        return AreaTypeEnum.getAreaTypeEnum(this.areaId);
    }

    public boolean isValid() {
        if (this.areaId == null || this.roomId == null) {
            return false;
        }
        if (this.year == null || this.month == null || this.day == null) {
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

    /**
     * 如果是周期预定，那么直接返回周；如果是单次预定，那么返回最近的要预定的周
     *
     * @return week
     */
    public Integer getBookedWeek() {
        if (this.week != null) {
            return this.week;
        }

        CALENDAR.setTime(new Date());
        CALENDAR.set(Calendar.DAY_OF_MONTH, this.day);
        CALENDAR.set(Calendar.MONTH, this.month - 1);
        CALENDAR.set(Calendar.YEAR, this.year);
        int bookWeek = CALENDAR.get(Calendar.DAY_OF_WEEK) - 1;
        bookWeek = bookWeek == 0 ? 7 : bookWeek;
        return bookWeek;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BookMeetingInfoDto that = (BookMeetingInfoDto) o;
        return Objects.equals(this.week, that.week) && Objects.equals(this.year, that.year) && Objects.equals(this.month, that.month) && Objects.equals(this.day, that.day) && Objects.equals(this.timeBegin, that.timeBegin) && Objects.equals(this.timeEnd, that.timeEnd) && Objects.equals(this.areaId, that.areaId) && Objects.equals(this.roomId, that.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.year, this.month, this.day, this.timeBegin, this.timeEnd, this.areaId, this.roomId);
    }

    /**
     * 会议室冲突检测<br/>
     *
     * @param b1 b1
     * @param b2 b2
     * @return 是否冲突
     */
    public static boolean isConflict(BookMeetingInfoDto b1, BookMeetingInfoDto b2) {
        if (b1 == null || b2 == null) {
            return false;
        }

        Integer week1 = b1.getBookedWeek();
        Integer week2 = b2.getBookedWeek();
        Long roomId1 = b1.getRoomId();
        Long roomId2 = b2.getRoomId();
        Integer userId1 = b1.getUserId();
        Integer userId2 = b2.getUserId();
        if (week1 == null || week2 == null) {
            return false;
        }
        if (week1.equals(week2)) {
            // 同一天，那么进行校验时间
            if (Objects.equals(userId1, userId2)) {
                // 校验自己的冲突，仅时间
                return timeConflict(b1.getTimeBeginTimeEnum(), b1.getTimeEndTimeEnum(), b2.getTimeBeginTimeEnum(), b2.getTimeEndTimeEnum());
            } else {
                // 校验别人的冲突，仅同会议室
                if (Objects.equals(roomId1, roomId2)) {
                    return timeConflict(b1.getTimeBeginTimeEnum(), b1.getTimeEndTimeEnum(), b2.getTimeBeginTimeEnum(), b2.getTimeEndTimeEnum());
                }
            }
        }
        return false;
    }

    private static boolean timeConflict(TimeEnum begin1, TimeEnum end1, TimeEnum begin2, TimeEnum end2) {
        int beginMax = Math.max(begin1.getIndex(), begin2.getIndex());
        int endMin = Math.min(end1.getIndex(), end2.getIndex());
        return endMin > beginMax;
    }

    public String bookInfo() {
        if (this.week != null) {
            String format = "[周期预定] 信息：会议室：%s，每周 %s，时间：%s-%s";
            return String.format(format, this.roomName, getBookedWeek(), this.timeBegin, this.timeEnd);
        } else {
            String format = "[单次预定] 信息：会议室：%s，日期 %s，时间：%s-%s";
            return String.format(format, this.roomName, getYmd(), this.timeBegin, this.timeEnd);
        }
    }

    public static String parseJoinPeople(String peopleInfoList) {
        StringBuilder res = new StringBuilder();
        List<JoinPeople> joinPeopleList = JoinPeople.parse(peopleInfoList);
        for (JoinPeople joinPeople : joinPeopleList) {
            res.append(",").append(joinPeople.getUserId());
        }
        return res.toString();
    }
}
