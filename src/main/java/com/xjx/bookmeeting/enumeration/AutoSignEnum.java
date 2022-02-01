package com.xjx.bookmeeting.enumeration;

/**
 * 签到类型
 *
 * @author xjx
 * @date 2022/1/30 12:11
 */
public enum AutoSignEnum {
    /**
     * 不签到
     */
    NONE(0),
    /**
     * 立即签到
     */
    IMMEDIATELY(1),
    ;
    private final Integer type;

    AutoSignEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
