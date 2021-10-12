package com.xjx.bookmeeting.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author XJX
 */
@Data
public class MeetingBooked {
    @JSONField(name = "user_telePhone")
    private String userTelePhone;

    @JSONField(name = "editable")
    private Integer editable;

    @JSONField(name = "user_name")
    private String userName;

    @JSONField(name = "transferable")
    private boolean transferable;

    @JSONField(name = "end_time")
    private String endTime;

    @JSONField(name = "begin_time")
    private String beginTime;

    @JSONField(name = "meetingstatus")
    private String meetingStatus;

    @JSONField(name = "remark")
    private String remark;

    @JSONField(name = "dept")
    private String dept;

    @JSONField(name = "roomFunctionValue")
    private String roomFunctionValue;

    @JSONField(name = "syncPoPoCalendar")
    private String syncPoPoCalendar;

    @JSONField(name = "roomId")
    private Long roomId;

    @JSONField(name = "roomName")
    private String roomName;

    @JSONField(name = "meetingNumber")
    private Integer meetingNumber;

    @JSONField(name = "customType")
    private Integer customType;

    @JSONField(name = "editMeeting")
    private Integer editMeeting;

    @JSONField(name = "user_mobile")
    private String userMobile;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "roomFunction")
    private String roomFunction;

    @JSONField(name = "accounts")
    private String accounts;

    @JSONField(name = "id")
    private Long id;

    @JSONField(name = "email")
    private String email;
}