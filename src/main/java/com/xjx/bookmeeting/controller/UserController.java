package com.xjx.bookmeeting.controller;

import com.xjx.bookmeeting.domain.User;
import com.xjx.bookmeeting.helper.CookieHelper;
import com.xjx.bookmeeting.logic.UserLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserLogic userLogic;

    @RequestMapping("/user/login")
    public Boolean login(String username, String password, String authType) {
        User user = userLogic.login(username, password, authType);
        CookieHelper.writeLoginCookie(user);
        return true;
    }

    @RequestMapping("/user/getUserInfo")
    public User getUserInfo() {
        User user = CookieHelper.getLoginCookie();
        return userLogic.getUserInfo(user);
    }
}
