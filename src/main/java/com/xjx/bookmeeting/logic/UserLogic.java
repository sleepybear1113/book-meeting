package com.xjx.bookmeeting.logic;

import com.xjx.bookmeeting.actions.TestLoginValidAction;
import com.xjx.bookmeeting.domain.User;
import com.xjx.bookmeeting.exception.FrontException;
import com.xjx.bookmeeting.login.MeetingLogin;
import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.service.UserService;
import com.xjx.bookmeeting.utils.OtherUtils;
import com.xjx.bookmeeting.utils.login.AuthTypeEnum;
import com.xjx.bookmeeting.utils.login.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/7 17:00
 */
@Component
@Slf4j
public class UserLogic {
    @Autowired
    private UserService userService;

    public boolean testLoginValid(User user) {
        if (user == null) {
            return false;
        }
        UserCookieInfo userCookieInfo = new UserCookieInfo(user.getCookie(), user.getLoginIdWeaver());
        return TestLoginValidAction.testLogin(userCookieInfo);
    }

    public boolean testLoginValidWithReLogin(User user) {
        boolean loginValid = testLoginValid(user);
        if (loginValid) {
            log.info(user.getUsername() + " 登录信息有效");
            // 登录信息仍有效
            return true;
        }

        User userInfo = userService.getUserInfo(user);
        if (userInfo == null || !userInfo.isValid()) {
            // 登录信息无效、用户信息不全
            return false;
        }

        // 重登陆、写信息
        log.info("cookie 失效，重新登录，username = " + user.getUsername());
        login(user);
        return true;
    }

    public void login(User user) {
        if (user == null) {
            return;
        }

        if (user.isValid()) {
            login(user.getUsername(), user.withDecryptGetPassword(), user.getAuthType());
        }
    }

    public User login(String username, String password, String authType) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(authType)) {
            FrontException.throwCommonFrontException("输入错误");
        }
        AuthTypeEnum authTypeEnum = AuthTypeEnum.getAuthTypeEnum(authType);
        if (authTypeEnum == null) {
            FrontException.throwCommonFrontException("认证方式错误");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setAuthType(authTypeEnum);
        userInfo.setCorpId(username);
        userInfo.setCorpPw(password);

        UserCookieInfo userCookieInfo = MeetingLogin.loginAndGetCookie(userInfo);
        boolean valid = UserCookieInfo.isValid(userCookieInfo);
        if (valid) {
            log.info("user " + username + " login success!");
            User user = new User();
            user.setUsername(username);
            user.withEncryptSetPassword(password);
            user.setAuthType(authType);
            User existUser = getUserInfo(user);
            if (existUser == null) {
                saveUserInfo(user, userCookieInfo);
            } else {
                OtherUtils.copyNoNullProperties(user, existUser);
                saveUserInfo(existUser, userCookieInfo);
            }
            return user;
        }

        throw FrontException.loginFail();
    }

    public void saveUserInfo(User user, UserCookieInfo userCookieInfo) {
        if (!user.isValid()) {
            FrontException.throwCommonFrontException("输入错误");
        }

        if (userCookieInfo != null && userCookieInfo.isValid()) {
            user.setCookie(userCookieInfo.getCookie().getValue());
            user.setLoginIdWeaver(userCookieInfo.getLoginIdWeaver());
        }
        userService.saveUserInfo(user);
        log.info("用户 " + user.getUsername() + " 保存至本地");

        User userInfo = userService.getUserInfo(user);
        log.info(userInfo.toString());
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public User getUserInfo(User user) {
        return userService.getUserInfo(user);
    }

    public User getUserInfoWithReLoginAndFrontException(User user) {
        User userInfo = getUserInfo(user);
        if (userInfo == null) {
            FrontException.throwCommonFrontException("本地未有用户信息，请先登录");
        }

        boolean loginValid = testLoginValidWithReLogin(userInfo);
        if (!loginValid) {
            FrontException.throwCommonFrontException("登录失败，请检查用户信息");
        }
        return getUserInfo(user);
    }

    public boolean deleteUser(User user) {
        return userService.deleteLocalUser(user);
    }
}
