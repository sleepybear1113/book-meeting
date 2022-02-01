package com.xjx.bookmeeting.logic;

import com.xjx.bookmeeting.actions.TestLoginValidAction;
import com.xjx.bookmeeting.dto.UserDto;
import com.xjx.bookmeeting.exception.FrontException;
import com.xjx.bookmeeting.login.MeetingLogin;
import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.service.UserService;
import com.xjx.bookmeeting.utils.login.AuthTypeEnum;
import com.xjx.bookmeeting.utils.login.UserInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户逻辑类
 *
 * @author xjx
 * @date 2021/10/7 17:00
 */
@Component
@Slf4j
public class UserLogic {
    @Resource
    private UserService userService;

    public boolean testLoginValid(UserDto userDto) {
        if (userDto == null) {
            return false;
        }
        UserCookieInfo userCookieInfo = new UserCookieInfo(userDto.getCookie(), userDto.getLoginIdWeaver());
        return TestLoginValidAction.testLogin(userCookieInfo);
    }

    public boolean testLoginValidWithReLogin(UserDto userDto) {
        boolean loginValid = testLoginValid(userDto);
        if (loginValid) {
            log.info(userDto.getUsername() + " 登录信息有效");
            // 登录信息仍有效
            return true;
        }

        UserDto userDtoInfo = userService.getUserDto(userDto);
        if (userDtoInfo == null || !userDtoInfo.isValid()) {
            // 登录信息无效、用户信息不全
            return false;
        }

        // 重登陆、写信息
        log.info("cookie 失效，重新登录，username = " + userDto.getUsername());
        login(userDto);
        return true;
    }

    public void login(UserDto userDto) {
        if (userDto == null) {
            return;
        }

        if (userDto.isValid()) {
            login(userDto.getUsername(), userDto.withDecryptGetPassword(), userDto.getAuthType());
        }
    }

    /**
     * 通过账号密码登录，，然后更新/新增用户到数据库
     *
     * @param username username
     * @param password password
     * @param authType authType
     * @return UserDto
     */
    public UserDto login(String username, String password, String authType) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(authType)) {
            throw new FrontException("输入错误");
        }
        AuthTypeEnum authTypeEnum = AuthTypeEnum.getAuthTypeEnum(authType);
        if (authTypeEnum == null) {
            throw new FrontException("认证方式错误");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setAuthType(authTypeEnum);
        userInfo.setCorpId(username);
        userInfo.setCorpPw(password);

        UserCookieInfo userCookieInfo = MeetingLogin.loginAndGetCookie(userInfo);
        boolean valid = UserCookieInfo.isValid(userCookieInfo);
        if (valid) {
            log.info("userDto " + username + " login success!");
            UserDto userDto = new UserDto();
            userDto.setUsername(username);
            userDto.withEncryptSetPassword(password);
            userDto.setAuthType(authType);
            return upsetUserInfo(userDto, userCookieInfo);
        }

        throw FrontException.loginFail();
    }

    public UserDto upsetUserInfo(UserDto userDto, UserCookieInfo userCookieInfo) {
        if (!userDto.isValid()) {
            throw new FrontException("输入错误");
        }

        if (userCookieInfo != null && userCookieInfo.isValid()) {
            userDto.setCookie(userCookieInfo.getCookie().getValue());
            userDto.setLoginIdWeaver(userCookieInfo.getLoginIdWeaver());
        }
        UserDto update = userService.saveUserInfo(userDto);
        log.info("用户 " + userDto.getUsername() + " 保存至本地");
        return update;
    }

    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    public UserDto getUserInfo(UserDto userDto) {
        return userService.getUserDto(userDto);
    }

    public UserDto getUserInfoWithReLoginAndFrontException(Integer userId) {
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        return getUserInfoWithReLoginAndFrontException(userDto);
    }

    public UserDto getUserInfoWithReLoginAndFrontException(UserDto userDto) {
        UserDto userDtoInfo = getUserInfo(userDto);
        if (userDtoInfo == null) {
            throw new FrontException("本地未有用户信息，请先登录");
        }

        boolean loginValid = testLoginValidWithReLogin(userDtoInfo);
        if (!loginValid) {
            throw new FrontException("登录失败，请检查用户信息");
        }
        return getUserInfo(userDto);
    }

    public boolean deleteUser(Integer id) {
        return userService.deleteLocalUser(id);
    }
}
