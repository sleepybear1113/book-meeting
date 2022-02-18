package com.xjx.bookmeeting.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2022/2/17 12:35
 */
@Data
public class UserInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 8453413254373064658L;

    private String claimComp;
    private String claimCompName;
    private String costCenter;
    private Integer dep1Id;
    private String dep1Name;
    private String dep1NameEx;
    private Integer dep2Id;
    private String dep2Name;
    private String depFullName;
    private Integer dept3Id;
    private String dept3Name;
    private String email;
    private Boolean isGameDept;
    private String jobTitle;
    private Integer jobTitleId;
    private String lastName;
    private String loginId;
    private String sex;
    private Integer userId;

    @JSONField(name = "userInfo")
    private List<UserInfo> userInfoList;
}
