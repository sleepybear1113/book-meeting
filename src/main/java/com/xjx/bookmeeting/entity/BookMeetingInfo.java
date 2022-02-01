package com.xjx.bookmeeting.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.util.Objects;

/**
 * 会议预定信息表类
 *
 * @author xjx
 * @date 2021/10/9 23:53
 */
@Data
@TableName("book_meeting_info")
public class BookMeetingInfo extends BaseDomain {
    @Serial
    private static final long serialVersionUID = 1777036795428134524L;

    /**
     * 参见字段 {@link User#id}
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 按照具体时间预定有下述三个 year、month、day 字段，无 week 字段
     */
    @TableField("year")
    private Integer year;
    @TableField("month")
    private Integer month;
    @TableField("day")
    private Integer day;

    /**
     * 按照周预定，有 week 字段，无 year、month、day 字段
     */
    @TableField("week")
    private Integer week;

    /**
     * 预定开始时间 xx:xx 格式
     */
    @TableField("time_begin")
    private String timeBegin;
    /**
     * 预定结束时间 xx:xx 格式
     */
    @TableField("time_end")
    private String timeEnd;

    /**
     * 公司会议室地区，参见 {@link com.xjx.bookmeeting.enumeration.AreaTypeEnum}
     */
    @TableField("area_id")
    private Integer areaId;
    /**
     * 会议室 id
     */
    @TableField("room_id")
    private Long roomId;
    /**
     * 会议室名
     */
    @TableField("room_name")
    private String roomName;
    /**
     * 预定会议名
     */
    @TableField("meeting_name")
    private String meetingName;

    /**
     * 可预订时间字符串，例如 8days、6months
     */
    @TableField("book_time")
    private String bookTime;

    /**
     * 是否自动签到
     */
    @TableField("auto_sign_in")
    private Integer autoSignIn;

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

    @Override
    public String tableSql() {
        return """
                CREATE TABLE IF NOT EXISTS book_meeting_info
                (
                    id           INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    create_time  BIGINT                            NOT NULL,
                    modify_time  BIGINT                            NOT NULL,
                    user_id      INTEGER                           NOT NULL,
                    year         INTEGER,
                    month        INTEGER,
                    day          INTEGER,
                    week         INTEGER,
                    time_begin   TEXT                              NOT NULL,
                    time_end     TEXT                              NOT NULL,
                    area_id      INTEGER                           NOT NULL,
                    room_id      INTEGER                           NOT NULL,
                    meeting_name TEXT,
                    room_name    TEXT,
                    book_time    TEXT                              NOT NULL,
                    auto_sign_in INTEGER
                )""";
    }
}

