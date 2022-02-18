package com.xjx.bookmeeting.actions.dto;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author XJX
 */
@Data
public class Room {
    @JSONField(name = "videoTel")
    private String videoTel;

    @JSONField(name = "code")
    private String code;

    @JSONField(name = "roomNumPeople")
    private Integer roomNumPeople;

    @JSONField(name = "syncPoPoCalendar")
    private Integer syncPoPoCalendar;

    @JSONField(name = "floorId")
    private Integer floorId;

    @JSONField(name = "isLock")
    private Integer isLock;

    @JSONField(name = "roomStatus")
    private Integer roomStatus;

    @JSONField(name = "floorDesc")
    private String floorDesc;

    @JSONField(name = "roomLockCopy")
    private String roomLockCopy;

    @JSONField(name = "floorName")
    private String floorName;

    @JSONField(name = "id")
    private Long id;

    @JSONField(name = "brNo")
    private Integer brNo;

    @JSONField(name = "roomStyle")
    private String roomStyle;

    @JSONField(name = "meetingBooked")
    private List<MeetingBooked> meetingBooked;

    @JSONField(name = "result")
    private List<MeetingBooked> result;

    @JSONField(name = "roomNotice")
    private String roomNotice;

    @JSONField(name = "roomDesc")
    private String roomDesc;

    @JSONField(name = "bookTime")
    private String bookTime;

    @JSONField(name = "roomFunctionValue")
    private String roomFunctionValue;

    @JSONField(name = "enableInfrared")
    private Integer enableInfrared;

    @JSONField(name = "roomName")
    private String roomName;

    @JSONField(name = "buildingId")
    private Integer buildingId;

    @JSONField(name = "buildingName")
    private String buildingName;

    @JSONField(name = "areaId")
    private Integer areaId;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "roomFunction")
    private String roomFunction;

    @JSONField(name = "isSpare")
    private Boolean isSpare;

    @JSONField(name = "roomTel")
    private String roomTel;

    @JSONField(name = "resultType")
    private Boolean resultType;

    public static TypeReference<MeetingResponse<Room>> getTypeReference() {
        return new TypeReference<>() {
        };
    }
}