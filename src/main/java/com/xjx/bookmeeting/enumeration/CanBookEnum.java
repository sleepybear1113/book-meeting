package com.xjx.bookmeeting.enumeration;

/**
 * 是否可以预定 or 过期的枚举
 *
 * @author xjx
 * @date 2021/11/19 3:25
 */
public enum CanBookEnum {
    /**
     * 可以预定
     */
    CAN_BOOK(1),

    /**
     * 就绪
     */
    READY(0),

    /**
     * 过期
     */
    EXPIRED(-1),
    ;

    CanBookEnum(Integer type) {
    }

    public static boolean isExpire(CanBookEnum canBookEnum) {
        if (canBookEnum == null) {
            return true;
        }
        return EXPIRED.equals(canBookEnum);
    }
}
