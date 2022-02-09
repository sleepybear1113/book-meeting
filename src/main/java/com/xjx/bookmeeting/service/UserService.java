package com.xjx.bookmeeting.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xjx.bookmeeting.dto.UserDto;
import com.xjx.bookmeeting.entity.BookMeetingInfo;
import com.xjx.bookmeeting.entity.User;
import com.xjx.bookmeeting.entity.UserCookie;
import com.xjx.bookmeeting.mapper.BookMeetingInfoMapper;
import com.xjx.bookmeeting.mapper.UserCookieMapper;
import com.xjx.bookmeeting.mapper.UserMapper;
import com.xjx.bookmeeting.utils.OtherUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/7 19:41
 */
@Service
@Slf4j
public class UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserCookieMapper userCookieMapper;
    @Resource
    private BookMeetingInfoMapper bookMeetingInfoMapper;

    public UserDto getById(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        return OtherUtils.convert(user, UserDto.class);
    }

    /**
     * 保存用户信息，新增 or 修改
     *
     * @param userDto userDto
     */
    public UserDto saveUserInfo(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        if (!userDto.isValid()) {
            return null;
        }

        // 获取 user + cookie
        UserDto existUserDto = getUserDto(userDto);
        Integer userId;

        // 写/更新 user
        User user = new User();
        if (existUserDto == null) {
            user = OtherUtils.convert(userDto, User.class);
            user.fillAllTime();
            userMapper.insert(user);
            userId = user.getId();
        } else {
            userId = existUserDto.getId();
            user.setId(userId);

            String inputPsw = userDto.getPassword();
            if (StringUtils.isNotBlank(inputPsw) && !inputPsw.equals(existUserDto.getPassword())) {
                user.setPassword(inputPsw);
                user.fillModifyTime();
                userMapper.updateById(user);
            }
        }
        userDto.setId(userId);

        // 写/更新 cookie
        UserCookie entity = new UserCookie();
        Integer userCookieId = userDto.getUserCookieId();
        entity.setUserId(userCookieId);
        entity.setCookie(userDto.getCookie());
        entity.setUserId(userId);
        if (userCookieId == null) {
            entity.fillAllTime();
            userCookieMapper.insert(entity);
        } else {
            entity.fillModifyTime();
            userCookieMapper.updateById(entity);
        }

        return getUserDto(userDto.getId());
    }

    /**
     * 只通过 id 或者 username + authType 查询
     *
     * @param userDto 查询入参
     * @return UserDto
     */
    public UserDto getUserDto(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        if (userDto.getId() == null) {
            if (StringUtils.isBlank(userDto.getUsername()) || StringUtils.isBlank(userDto.getAuthType())) {
                return null;
            }
        }

        User userQuery = new User();
        userQuery.setUsername(userDto.getUsername());
        userQuery.setAuthType(userDto.getAuthType());
        userQuery.setId(userDto.getId());

        List<User> users = userMapper.selectList(new QueryWrapper<>(userQuery));
        if (CollectionUtils.isEmpty(users)) {
            return null;
        }
        User user = users.get(0);
        userDto = OtherUtils.convert(user, UserDto.class);
        Integer id = user.getId();

        UserCookie userCookieQuery = new UserCookie();
        userCookieQuery.setUserId(id);

        UserCookie userCookie = null;
        List<UserCookie> userCookieList = userCookieMapper.selectList(new QueryWrapper<>(userCookieQuery));
        if (CollectionUtils.isNotEmpty(userCookieList)) {
            if (userCookieList.size() == 1) {
                userCookie = userCookieList.get(0);
            } else {
                // 如果出现多个，那么需要删除旧的 cookie
                userCookieList.sort(Comparator.comparingInt(UserCookie::getId));
                int size = userCookieList.size();
                userCookie = userCookieList.get(size - 1);
                userCookieList.remove(size - 1);
                for (UserCookie delete : userCookieList) {
                    userCookieMapper.deleteById(delete.getId());
                    log.info("删除用户[{}]多余 cookie", user.getUsername());
                }
            }
        }

        if (userCookie != null) {
            userDto.setCookie(userCookie.getCookie());
            userDto.setUserCookieId(userCookie.getId());
        }

        return userDto;
    }

    public UserDto getUserDto(Integer userId) {
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        return getUserDto(userDto);
    }

    public boolean deleteLocalUser(Integer id) {
        if (id == null) {
            return false;
        }

        userMapper.deleteById(id);

        UserCookie userCookie = new UserCookie();
        userCookie.setUserId(id);
        userCookieMapper.delete(new QueryWrapper<>(userCookie));

        BookMeetingInfo bookMeetingInfo = new BookMeetingInfo();
        bookMeetingInfo.setUserId(id);
        bookMeetingInfoMapper.delete(new QueryWrapper<>(bookMeetingInfo));
        return true;
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userMapper.selectList(new QueryWrapper<>());
        if (CollectionUtils.isEmpty(users)) {
            return new ArrayList<>();
        }

        List<UserDto> res = new ArrayList<>();
        for (User user : users) {
            UserDto query = new UserDto();
            query.setId(user.getId());
            UserDto userDto = getUserDto(query);
            if (userDto != null) {
                res.add(userDto);
            }
        }

        return res;
    }
}
