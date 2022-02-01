package com.xjx.bookmeeting.enumeration;

/**
 * 不同办公地点的枚举
 *
 * @author XJX
 * @date 2021/8/31 1:55
 */
public enum AreaTypeEnum {
    /**
     * 区域
     */
    BEIJING(1),
    SHANGHAI(2),
    GUANGZHOU(3),
    HANGZHOU(5),
    SHENZHEN(8),
    NINGBO(13),
    QUZHOU(15),
    ;
    private final Integer areaId;

    AreaTypeEnum(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public static AreaTypeEnum getAreaTypeEnum(Integer areaId) {
        if (areaId == null) {
            return null;
        }

        for (AreaTypeEnum value : AreaTypeEnum.values()) {
            if (value.getAreaId().equals(areaId)) {
                return value;
            }
        }

        return null;
    }
}
