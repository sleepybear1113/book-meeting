package com.xjx.bookmeeting.logic;

import com.xjx.bookmeeting.actions.BookRoomAction;
import com.xjx.bookmeeting.domain.BookOnceInfo;
import com.xjx.bookmeeting.domain.User;
import com.xjx.bookmeeting.dto.BookRoomResult;
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
        for (User allUser : allUsers) {
            bookOnceInSchedule(allUser);
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
        List<BookOnceInfo> bookOnceInfoList = user.getBookOnceInfoList();
        if (CollectionUtils.isEmpty(bookOnceInfoList)) {
            return;
        }

        log.info("开始进行预定，用户：" + user.getUsername());
        for (BookOnceInfo bookOnceInfo : bookOnceInfoList) {
            if (!bookOnceInfo.isAfterDays(7)) {
                continue;
            }
            OtherUtils.sleep(2000);

            BookRoomAction.FormData formData = new BookRoomAction.FormData();
            formData.setTimeRange(bookOnceInfo.getYear(), bookOnceInfo.getMonth(), bookOnceInfo.getDay(), bookOnceInfo.getTimeBeginTimeEnum(), bookOnceInfo.getTimeEndTimeEnum());
            formData.setAreaId(bookOnceInfo.getAreaIdEnum());
            formData.setJoinUserIds(loginIdWeaver);
            formData.setMeetingRoomId(bookOnceInfo.getRoomId());
            if (StringUtils.isNotBlank(bookOnceInfo.getName())) {
                formData.setName(bookOnceInfo.getName());
            }

            BookRoomResult book;
            try {
                book = BookRoomAction.book(user.getUserCookieInfo(), formData);
            } catch (FrontException e) {
                log.info(e.getMessage());
                continue;
            } catch (Exception e) {
                log.info(e.getMessage(), e);
                continue;
            }
            log.info(book.toString());
        }
        log.info("===============");
    }

}
