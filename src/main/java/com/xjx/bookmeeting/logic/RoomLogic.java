package com.xjx.bookmeeting.logic;

import com.xjx.bookmeeting.actions.GetFloorAction;
import com.xjx.bookmeeting.actions.GetSpareRoomAction;
import com.xjx.bookmeeting.dao.BookOnceInfo;
import com.xjx.bookmeeting.dao.User;
import com.xjx.bookmeeting.dto.Floor;
import com.xjx.bookmeeting.dto.Room;
import com.xjx.bookmeeting.enumeration.AreaTypeEnum;
import com.xjx.bookmeeting.enumeration.TimeEnum;
import com.xjx.bookmeeting.exception.FrontException;
import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/7 16:59
 */
@Component
@Slf4j
public class RoomLogic {
    private static final Calendar CALENDAR = Calendar.getInstance();

    @Autowired
    private UserService userService;
    @Autowired
    private UserLogic userLogic;

    public List<Floor> getAllFloors(String username, String authType, Integer areaId) {
        User user = new User();
        user.setUsername(username);
        user.setAuthType(authType);
        User userInfo = userService.getUserInfo(user);
        if (userInfo == null) {
            FrontException.throwCommonFrontException("本地未有用户信息，请先登录");
        }

        boolean loginValid = userLogic.testLoginValidWithReLogin(userInfo);
        if (!loginValid) {
            FrontException.throwCommonFrontException("登录失败，请检查用户信息");
        }

        // 经过这里本地保存的是最新的有效登录信息
        userInfo = userService.getUserInfo(user);
        List<Floor> floors = GetFloorAction.getFloor(new UserCookieInfo(userInfo.getCookie(), userInfo.getLoginIdWeaver()), AreaTypeEnum.getAreaTypeEnum(areaId));
        log.info(String.valueOf(floors));
        return floors;
    }

    public List<Room> roomLogic(Floor floor, String username, String authType) {
        if (floor == null) {
            return new ArrayList<>();
        }

        User user = new User();
        user.setUsername(username);
        user.setAuthType(authType);
        User userInfo = userService.getUserInfo(user);
        if (userInfo == null) {
            FrontException.throwCommonFrontException("本地未有用户信息，请先登录");
        }

        boolean loginValid = userLogic.testLoginValidWithReLogin(userInfo);
        if (!loginValid) {
            FrontException.throwCommonFrontException("登录失败，请检查用户信息");
        }

        // 经过这里本地保存的是最新的有效登录信息
        userInfo = userService.getUserInfo(user);

        GetSpareRoomAction.Query query = new GetSpareRoomAction.Query();
        query.setDate(System.currentTimeMillis() + 86400000 * 2);
        query.setAreaId(AreaTypeEnum.getAreaTypeEnum(floor.getAreaId()).getAreaId());
        query.setBuildingId(floor.getBuildingId());
        query.setFloorId(floor.getId());
        return GetSpareRoomAction.getRooms(query, new UserCookieInfo(userInfo.getCookie(), userInfo.getLoginIdWeaver()));
    }

    public Boolean bookRoom(String username, String authType, String dayString, String hourBegin, String hourEnd, String minuteBegin, String minuteEnd, Long roomId, Integer areaId) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(authType) || StringUtils.isBlank(dayString) || StringUtils.isBlank(hourBegin) || StringUtils.isBlank(hourEnd) || StringUtils.isBlank(minuteBegin) || StringUtils.isBlank(minuteEnd) || roomId == null || areaId == null) {
            FrontException.throwCommonFrontException("参数错误");
        }

        String[] days = dayString.split("-");
        if (days.length != 3) {
            FrontException.throwCommonFrontException("日期格式错误");
        }
        int year = Integer.parseInt(days[0]);
        int month = Integer.parseInt(days[1]);
        int day = Integer.parseInt(days[2]);

        CALENDAR.setTime(new Date());
        CALENDAR.set(Calendar.YEAR, year);
        CALENDAR.set(Calendar.MONTH, month - 1);
        CALENDAR.set(Calendar.DAY_OF_MONTH, day);
        long bookTime = CALENDAR.getTime().getTime();
        if (bookTime < 2 * 86400 * 1000 + System.currentTimeMillis()) {
//            FrontException.throwCommonFrontException("不能预定过近的时间");
        }

        String timeBegin = hourBegin + minuteBegin;
        String timeEnd = hourEnd + minuteEnd;
        if (Long.parseLong(timeBegin) >= Long.parseLong(timeEnd)) {
            FrontException.throwCommonFrontException("时间起止错误");
        }
        TimeEnum timeBeginTimeEnum = TimeEnum.getByTime(timeBegin);
        TimeEnum timeEndTimeEnum = TimeEnum.getByTime(timeEnd);
        if (timeBeginTimeEnum == null || timeEndTimeEnum == null) {
            FrontException.throwCommonFrontException("时间起止错误");
        }

        User user = new User();
        user.setUsername(username);
        user.setAuthType(authType);
        User userInfo = userService.getUserInfo(user);
        if (userInfo == null) {
            FrontException.throwCommonFrontException("本地未有用户信息，请先登录");
        }

        boolean loginValid = userLogic.testLoginValidWithReLogin(userInfo);
        if (!loginValid) {
            FrontException.throwCommonFrontException("登录失败，请检查用户信息");
        }

        // 经过这里本地保存的是最新的有效登录信息
        userInfo = userService.getUserInfo(user);
        List<BookOnceInfo> bookOnceInfoList = userInfo.getBookOnceInfoList();
        if (CollectionUtils.isEmpty(bookOnceInfoList)) {
            bookOnceInfoList = new ArrayList<>();
            userInfo.setBookOnceInfoList(bookOnceInfoList);
        }
        BookOnceInfo add = new BookOnceInfo();
        add.setYear(year);
        add.setMonth(month);
        add.setDay(day);
        add.setTimeBegin(timeBegin);
        add.setTimeEnd(timeEnd);
        add.setRoomId(roomId);
        add.setAreaId(areaId);
        if (bookOnceInfoList.contains(add)) {
            FrontException.throwCommonFrontException("预约信息重复");
        }
        bookOnceInfoList.add(add);

        userLogic.saveUserInfo(userInfo, null);
        return true;
    }
}
