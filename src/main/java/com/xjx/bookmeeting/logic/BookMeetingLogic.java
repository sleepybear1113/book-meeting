package com.xjx.bookmeeting.logic;

import com.xjx.bookmeeting.actions.BookRoomAction;
import com.xjx.bookmeeting.actions.SignInRoomAction;
import com.xjx.bookmeeting.actions.dto.BookRoomResult;
import com.xjx.bookmeeting.actions.dto.SignInRoomResponse;
import com.xjx.bookmeeting.dto.BookMeetingInfoDto;
import com.xjx.bookmeeting.dto.UserDto;
import com.xjx.bookmeeting.enumeration.AutoSignEnum;
import com.xjx.bookmeeting.enumeration.CanBookEnum;
import com.xjx.bookmeeting.exception.FrontException;
import com.xjx.bookmeeting.service.BookMeetingInfoService;
import com.xjx.bookmeeting.utils.OtherUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 会议预定逻辑
 *
 * @author xjx
 * @date 2022/2/1 12:39
 */
@Component
@Slf4j
public class BookMeetingLogic {

    @Resource
    private UserLogic userLogic;
    @Resource
    private BookMeetingInfoService bookMeetingInfoService;

    /**
     * 获取全部用户，并且预定用户的会议室
     */
    public void bookAllUsers() {
        List<UserDto> allUserDtos = userLogic.getAllUsers();
        if (CollectionUtils.isEmpty(allUserDtos)) {
            return;
        }
        for (UserDto userDto : allUserDtos) {
            try {
                bookUserMeeting(userDto);
            } catch (Exception e) {
                log.warn("用户 " + userDto.getUsername() + " 预定失败", e);
            }
            OtherUtils.sleep(2000);
        }
    }

    public void bookUserMeeting(UserDto userDto) {
        if (userDto == null) {
            return;
        }

        boolean loginValid = userLogic.testLoginValidWithReLogin(userDto);
        if (!loginValid) {
            throw new FrontException("登录失败，请检查用户信息");
        }

        // 经过这里本地保存的是最新的有效登录信息
        userDto = userLogic.getUserInfo(userDto);
        Integer userId = userDto.getId();
        String loginIdWeaver = userDto.getLoginIdWeaver();

        List<BookMeetingInfoDto> bookMeetingInfoList = bookMeetingInfoService.getUserBookMeetings(userId);
        if (CollectionUtils.isEmpty(bookMeetingInfoList)) {
            return;
        }

        // 删除过期的预定
        bookMeetingInfoList.removeIf(o -> {
            if (o.isInvalid() || CanBookEnum.isExpire(o.canBook())) {
                bookMeetingInfoService.deleteBookInfos(Collections.singletonList(o.getId()));
                return true;
            }
            return false;
        });
        if (CollectionUtils.isEmpty(bookMeetingInfoList)) {
            return;
        }

        log.info("开始进行预定，用户：" + userDto.getUsername());
        for (BookMeetingInfoDto bookMeetingInfo : bookMeetingInfoList) {
            if (bookMeetingInfo == null) {
                continue;
            }
            if (!CanBookEnum.CAN_BOOK.equals(bookMeetingInfo.canBook())) {
                continue;
            }
            OtherUtils.sleep(2000);

            BookRoomAction.FormData formData = new BookRoomAction.FormData();
            formData.setTimeRange(bookMeetingInfo.getYear(), bookMeetingInfo.getMonth(), bookMeetingInfo.getDay(), bookMeetingInfo.getTimeBeginTimeEnum(), bookMeetingInfo.getTimeEndTimeEnum());
            formData.setAreaId(bookMeetingInfo.getAreaIdEnum());
            formData.setJoinUserIds(loginIdWeaver + BookMeetingInfoDto.parseJoinPeople(bookMeetingInfo.getJoinPeople()));
            formData.setMeetingRoomId(bookMeetingInfo.getRoomId());
            if (StringUtils.isNotBlank(bookMeetingInfo.getMeetingName())) {
                formData.setName(bookMeetingInfo.getMeetingName());
            }

            try {
                // 预定会议室
                BookRoomResult book = BookRoomAction.book(userDto.getUserCookieInfo(), formData);
                log.info(book.toString());
                OtherUtils.sleep(1000L);

                if (AutoSignEnum.IMMEDIATELY.getType().equals(bookMeetingInfo.getAutoSignIn())) {
                    // 签到会议室
                    SignInRoomResponse signInRoomResponse = SignInRoomAction.signIn(userDto.getUserCookieInfo(), book, bookMeetingInfo.getAreaIdEnum().getAreaId(), userDto.getEmail());
                    if (signInRoomResponse != null) {
                        log.info(signInRoomResponse.toString());
                    }
                }
            } catch (FrontException e) {
                log.info(e.getMessage());
            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
        }
        log.info("===============");
    }
}
