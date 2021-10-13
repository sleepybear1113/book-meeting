package com.xjx.bookmeeting.logic;

import com.xjx.bookmeeting.actions.GetFloorAction;
import com.xjx.bookmeeting.actions.GetSpareRoomAction;
import com.xjx.bookmeeting.domain.BookOnceInfo;
import com.xjx.bookmeeting.domain.User;
import com.xjx.bookmeeting.dto.Floor;
import com.xjx.bookmeeting.dto.Room;
import com.xjx.bookmeeting.enumeration.AreaTypeEnum;
import com.xjx.bookmeeting.enumeration.TimeEnum;
import com.xjx.bookmeeting.exception.FrontException;
import com.xjx.bookmeeting.login.UserCookieInfo;
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
    private UserLogic userLogic;

    public List<Floor> getAllFloors(User user, Integer areaId) {
        User userInfo = userLogic.getUserInfoWithReLoginAndFrontException(user);
        List<Floor> floors = GetFloorAction.getFloor(new UserCookieInfo(userInfo.getCookie(), userInfo.getLoginIdWeaver()), AreaTypeEnum.getAreaTypeEnum(areaId));
        log.info(String.valueOf(floors));
        return floors;
    }

    public List<Room> roomLogic(Floor floor, User user) {
        if (floor == null) {
            return new ArrayList<>();
        }

        User userInfo = userLogic.getUserInfoWithReLoginAndFrontException(user);

        GetSpareRoomAction.Query query = new GetSpareRoomAction.Query();
        query.setDate(System.currentTimeMillis() + 86400000 * 2);
        query.setAreaId(AreaTypeEnum.getAreaTypeEnum(floor.getAreaId()).getAreaId());
        query.setBuildingId(floor.getBuildingId());
        query.setFloorId(floor.getId());
        return GetSpareRoomAction.getRooms(query, new UserCookieInfo(userInfo.getCookie(), userInfo.getLoginIdWeaver()));
    }

    public Boolean bookRoom(User user, String dayString, String hourBegin, String hourEnd, String minuteBegin, String minuteEnd, Long roomId, Integer areaId, String name) {
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(dayString) || StringUtils.isBlank(hourBegin) || StringUtils.isBlank(hourEnd) || StringUtils.isBlank(minuteBegin) || StringUtils.isBlank(minuteEnd) || roomId == null || areaId == null) {
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

        User userInfo = userLogic.getUserInfoWithReLoginAndFrontException(user);
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
        if (StringUtils.isNotBlank(name)) {
            add.setName(name);
        }
        if (bookOnceInfoList.contains(add)) {
            FrontException.throwCommonFrontException("预约信息重复");
        }
        bookOnceInfoList.add(add);

        userLogic.saveUserInfo(userInfo, null);
        return true;
    }
}
