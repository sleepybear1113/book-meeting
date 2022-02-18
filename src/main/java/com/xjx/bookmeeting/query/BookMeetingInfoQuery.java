package com.xjx.bookmeeting.query;

import com.xjx.bookmeeting.dto.BookMeetingInfoDto;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2022/2/19 3:24
 */
@Data
public class BookMeetingInfoQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = 9041618059857411628L;

    private String dayString;

    /**
     * 多个 week {@link BookMeetingInfoDto#week}
     */
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
}
