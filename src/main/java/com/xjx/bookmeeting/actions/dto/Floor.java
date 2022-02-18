package com.xjx.bookmeeting.actions.dto;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author XJX
 */
@Data
@ToString
public class Floor implements Serializable {
    @Serial
    private static final long serialVersionUID = 6671116191396459576L;

    @JSONField(name = "buildingName")
    private String buildingName;

    @JSONField(name = "areaId")
    private Integer areaId;

    @JSONField(name = "code")
    private String code;

    @JSONField(name = "floorNotice")
    private String floorNotice;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "floorDesc")
    private String floorDesc;

    @JSONField(name = "floorName")
    private String floorName;

    @JSONField(name = "id")
    private Long id;

    @JSONField(name = "buildingId")
    private Integer buildingId;

    public static TypeReference<MeetingResponse<Floor>> getTypeReference() {
        return new TypeReference<>() {
        };
    }
}