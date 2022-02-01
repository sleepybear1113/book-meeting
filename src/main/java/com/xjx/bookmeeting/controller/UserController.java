package com.xjx.bookmeeting.controller;

import com.xjx.bookmeeting.dto.UserDto;
import com.xjx.bookmeeting.helper.CookieHelper;
import com.xjx.bookmeeting.logic.UserLogic;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/7 17:31
 */
@RestController
@Slf4j
public class UserController {
    @Resource
    private UserLogic userLogic;

    @RequestMapping("/user/login")
    public Boolean login(String username, String password, String authType) {
        UserDto userDto = userLogic.login(username, password, authType);
        CookieHelper.writeLoginCookie(userDto);
        return true;
    }

    @RequestMapping("/user/getUserInfo")
    public UserDto getUserInfo() {
        UserDto userDto = CookieHelper.getLoginCookie();
        if (userDto == null) {
            return null;
        }
        return userLogic.getUserInfo(userDto);
    }

    @RequestMapping("/user/deleteUser")
    public Boolean deleteUser() {
        UserDto userDto = CookieHelper.getLoginCookieWithFrontException();
        return userLogic.deleteUser(userDto.getId());
    }
}
