package com.xjx.bookmeeting.logic;

import com.xjx.bookmeeting.actions.GetFloorAction;
import com.xjx.bookmeeting.actions.GetSpareRoomAction;
import com.xjx.bookmeeting.domain.BookMeetingInfo;
import com.xjx.bookmeeting.domain.User;
import com.xjx.bookmeeting.dto.Floor;
import com.xjx.bookmeeting.dto.Room;
import com.xjx.bookmeeting.enumeration.AreaTypeEnum;
import com.xjx.bookmeeting.enumeration.TimeEnum;
import com.xjx.bookmeeting.exception.FrontException;
import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.utils.OtherUtils;
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

    public Boolean bookRoom(User user, String dayString, String hourBegin, String hourEnd, String minuteBegin, String minuteEnd, Long roomId, Integer areaId, String meetingName, String roomName, String bookTime, String weekStrings) {
        if (StringUtils.isBlank(user.getUsername()) || roomId == null || areaId == null || StringUtils.isBlank(bookTime)) {
            FrontException.throwCommonFrontException("参数错误");
        }

        // 包含至少一个 null 或者全是非 null 数字
        List<Integer> weeks = BookMeetingInfo.weekToList(weekStrings);
        if (StringUtils.isBlank(weekStrings)) {
            if (StringUtils.isBlank(dayString) || StringUtils.isBlank(hourBegin) || StringUtils.isBlank(hourEnd) || StringUtils.isBlank(minuteBegin) || StringUtils.isBlank(minuteEnd)) {
                FrontException.throwCommonFrontException("参数错误");
            }
        } else {
            if (!BookMeetingInfo.isWeekStringValid(weekStrings)) {
                FrontException.throwCommonFrontException("参数错误");
            }
            dayString = "0-0-0";
        }

        String[] days = dayString.split("-");
        int length = 3;
        if (days.length != length) {
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
        List<BookMeetingInfo> bookMeetingInfoList = userInfo.getBookMeetingInfoList();
        if (CollectionUtils.isEmpty(bookMeetingInfoList)) {
            bookMeetingInfoList = new ArrayList<>();
            userInfo.setBookMeetingInfoList(bookMeetingInfoList);
        }

        for (Integer week : weeks) {
            BookMeetingInfo add = new BookMeetingInfo();
            add.setYear(year);
            add.setMonth(month);
            add.setDay(day);
            add.setTimeBegin(timeBegin);
            add.setTimeEnd(timeEnd);
            add.setRoomId(roomId);
            add.setAreaId(areaId);
            add.setId(System.currentTimeMillis());
            add.setRoomName(roomName);
            add.setBookTime(bookTime);
            add.setWeek(week);
            add.fillAllTime();
            if (StringUtils.isNotBlank(meetingName)) {
                add.setMeetingName(meetingName);
            }
            if (bookMeetingInfoList.contains(add)) {
                FrontException.throwCommonFrontException("预约信息重复");
            }
            bookMeetingInfoList.add(add);

            OtherUtils.sleep(10L);
        }

        userLogic.saveUserInfo(userInfo, null);
        return true;
    }

    public Boolean cancelBookRoom(User user, Long id) {
        if (id == null) {
            FrontException.throwCommonFrontException("输入错误");
        }
        User userInfo = userLogic.getUserInfo(user);
        if (userInfo == null) {
            FrontException.throwCommonFrontException("本地用户信息不存在");
        }
        List<BookMeetingInfo> bookMeetingInfoList = userInfo.getBookMeetingInfoList();
        if (CollectionUtils.isEmpty(bookMeetingInfoList)) {
            FrontException.throwCommonFrontException("无对应此 id");
        }

        int size = bookMeetingInfoList.size();
        bookMeetingInfoList.removeIf(b -> id.equals(b.getId()));
        if (size <= bookMeetingInfoList.size()) {
            FrontException.throwCommonFrontException("无对应此 id");
        }

        userInfo.setBookMeetingInfoList(bookMeetingInfoList);
        userLogic.saveUserInfo(userInfo, null);
        return true;
    }
}
