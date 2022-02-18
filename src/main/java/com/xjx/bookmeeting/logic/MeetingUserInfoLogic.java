package com.xjx.bookmeeting.logic;

import com.xjx.bookmeeting.actions.QueryUserInfoAction;
import com.xjx.bookmeeting.dto.UserDto;
import com.xjx.bookmeeting.dto.UserInfo;
import com.xjx.bookmeeting.login.UserCookieInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2022/2/18 2:22
 */
@Component
@Slf4j
public class MeetingUserInfoLogic {

    @Resource
    private UserLogic userLogic;

    public List<UserInfo> queryUserInfo(String queryKey, Integer userId) {
        UserDto userDtoInfo = userLogic.getUserInfoWithReLoginAndFrontException(userId);
        return QueryUserInfoAction.queryUserInfo(queryKey, new UserCookieInfo(userDtoInfo.getCookie(), userDtoInfo.getLoginIdWeaver()));
    }
}
