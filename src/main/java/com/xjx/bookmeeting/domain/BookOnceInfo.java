package com.xjx.bookmeeting.domain;

import com.xjx.bookmeeting.enumeration.AreaTypeEnum;
import com.xjx.bookmeeting.enumeration.TimeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/9 23:53
 */
@Data
public class BookOnceInfo implements Serializable {
    private static final long serialVersionUID = 1777036795428134524L;
    private static final Calendar CALENDAR = Calendar.getInstance();

    private Integer year;
    private Integer month;
    private Integer day;
    private String timeBegin;
    private String timeEnd;
    private Integer areaId;
    private Long roomId;
    private String name;

    public Long getBookTimestamp() {
        if (year == null || month == null || day == null) {
            return null;
        }
        CALENDAR.setTime(new Date());
        CALENDAR.set(Calendar.YEAR, year);
        CALENDAR.set(Calendar.MONTH, month - 1);
        CALENDAR.set(Calendar.DAY_OF_MONTH, day);
        CALENDAR.set(Calendar.MILLISECOND, 0);
        return CALENDAR.getTime().getTime();
    }

    public boolean isToday() {
        if (isInvalid()) {
            return false;
        }

        CALENDAR.setTime(new Date());
        int year = CALENDAR.get(Calendar.YEAR);
        int month = CALENDAR.get(Calendar.MONTH) + 1;
        int day = CALENDAR.get(Calendar.DAY_OF_MONTH);
        return year == this.year && month == this.month && day == this.day;
    }

    public boolean isAfterDays(int days) {
        if (isInvalid()) {
            return false;
        }

        // 若干天后
        CALENDAR.setTime(new Date(System.currentTimeMillis() + 86400L * 1000 * days));
        int year = CALENDAR.get(Calendar.YEAR);
        int month = CALENDAR.get(Calendar.MONTH) + 1;
        int day = CALENDAR.get(Calendar.DAY_OF_MONTH);

        return year == this.year && month == this.month && day == this.day;
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
        if (year == null || month == null || day == null || areaId == null || roomId == null) {
            return false;
        }

        return getTimeBeginTimeEnum() != null && getTimeEndTimeEnum() != null && getAreaIdEnum() != null;
    }

    public boolean isInvalid() {
        return isValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BookOnceInfo that = (BookOnceInfo) o;
        return Objects.equals(year, that.year) && Objects.equals(month, that.month) && Objects.equals(day, that.day) && Objects.equals(timeBegin, that.timeBegin) && Objects.equals(timeEnd, that.timeEnd) && Objects.equals(areaId, that.areaId) && Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day, timeBegin, timeEnd, areaId, roomId);
    }
}

