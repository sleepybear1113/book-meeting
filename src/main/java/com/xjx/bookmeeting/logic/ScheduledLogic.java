package com.xjx.bookmeeting.logic;

import com.xjx.bookmeeting.actions.BookRoomAction;
import com.xjx.bookmeeting.actions.SignInRoomAction;
import com.xjx.bookmeeting.domain.BookMeetingInfo;
import com.xjx.bookmeeting.domain.User;
import com.xjx.bookmeeting.dto.BookRoomResult;
import com.xjx.bookmeeting.dto.SignInRoomResponse;
import com.xjx.bookmeeting.enumeration.CanBookEnum;
import com.xjx.bookmeeting.exception.FrontException;
import com.xjx.bookmeeting.utils.OtherUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/10 0:24
 */
@Component
@Slf4j
public class ScheduledLogic {
    @Autowired
    private UserLogic userLogic;

    public void bookAllUsers() {
        List<User> allUsers = userLogic.getAllUsers();
        if (CollectionUtils.isEmpty(allUsers)) {
            return;
        }
        for (User user : allUsers) {
            try {
                bookOnceInSchedule(user);
            } catch (Exception e) {
                log.warn("用户" + user.getUsername() + "预定失败");
            }
            OtherUtils.sleep(2000);
        }
    }

    public void bookOnceInSchedule(User user) {
        if (user == null) {
            return;
        }

        boolean loginValid = userLogic.testLoginValidWithReLogin(user);
        if (!loginValid) {
            FrontException.throwCommonFrontException("登录失败，请检查用户信息");
        }

        // 经过这里本地保存的是最新的有效登录信息
        user = userLogic.getUserInfo(user);
        String loginIdWeaver = user.getLoginIdWeaver();

        boolean removeExpired = user.removeExpired();
        if (removeExpired) {
            userLogic.saveUserInfo(user, null);
        }
        List<BookMeetingInfo> bookMeetingInfoList = user.getBookMeetingInfoList();
        if (CollectionUtils.isEmpty(bookMeetingInfoList)) {
            return;
        }

        log.info("开始进行预定，用户：" + user.getUsername());
        for (BookMeetingInfo bookMeetingInfo : bookMeetingInfoList) {
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
            formData.setJoinUserIds(loginIdWeaver);
            formData.setMeetingRoomId(bookMeetingInfo.getRoomId());
            if (StringUtils.isNotBlank(bookMeetingInfo.getMeetingName())) {
                formData.setName(bookMeetingInfo.getMeetingName());
            }

            try {
                // 预定会议室
                BookRoomResult book = BookRoomAction.book(user.getUserCookieInfo(), formData);
                log.info(book.toString());
                OtherUtils.sleep(1000L);

                if (Boolean.TRUE.equals(bookMeetingInfo.getAutoSignIn())) {
                    // 签到会议室
                    SignInRoomResponse signInRoomResponse = SignInRoomAction.signIn(user.getUserCookieInfo(), book, bookMeetingInfo.getAreaIdEnum().getAreaId(), user.getEmail());
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
