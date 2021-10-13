package com.xjx.bookmeeting.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/7 20:29
 */
@Data
public class BaseDomain implements Serializable {
    private static final long serialVersionUID = -8465232923281938510L;

    private Long id;
    private Long createTime;
    private Long modifyTime;

    public void fillAllTime() {
        long now = System.currentTimeMillis();
        this.createTime = now;
        modifyTime = now;
    }

    public void fillModifyTime() {
        modifyTime = System.currentTimeMillis();
    }
}
