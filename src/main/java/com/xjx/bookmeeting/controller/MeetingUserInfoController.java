package com.xjx.bookmeeting.controller;

import com.xjx.bookmeeting.dto.UserDto;
import com.xjx.bookmeeting.dto.UserInfo;
import com.xjx.bookmeeting.helper.CookieHelper;
import com.xjx.bookmeeting.logic.MeetingUserInfoLogic;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2022/2/18 2:20
 */
@RestController
public class MeetingUserInfoController {

    @Resource
    private MeetingUserInfoLogic meetingUserInfoLogic;

    @RequestMapping("/meetingUser/queryUserInfo")
    public List<UserInfo> queryUserInfo(String queryKey) {
        UserDto userDto = CookieHelper.getLoginCookieWithFrontException();
        return meetingUserInfoLogic.queryUserInfo(queryKey, userDto.getId());
    }
}
