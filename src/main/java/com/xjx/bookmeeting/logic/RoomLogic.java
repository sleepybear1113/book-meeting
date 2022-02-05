package com.xjx.bookmeeting.logic;

import com.xjx.bookmeeting.actions.GetFloorAction;
import com.xjx.bookmeeting.actions.GetSpareRoomAction;
import com.xjx.bookmeeting.dto.BookMeetingInfoDto;
import com.xjx.bookmeeting.dto.Floor;
import com.xjx.bookmeeting.dto.Room;
import com.xjx.bookmeeting.dto.UserDto;
import com.xjx.bookmeeting.enumeration.AreaTypeEnum;
import com.xjx.bookmeeting.enumeration.TimeEnum;
import com.xjx.bookmeeting.exception.FrontException;
import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.service.BookMeetingInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 会议室获取逻辑
 *
 * @author xjx
 * @date 2021/10/7 16:59
 */
@Component
@Slf4j
public class RoomLogic {
    private static final Calendar CALENDAR = Calendar.getInstance();

    @Resource
    private UserLogic userLogic;
    @Resource
    private BookMeetingInfoService bookMeetingInfoService;

    /**
     * 获取某个区域/建筑下的全部楼层信息
     *
     * @param userId userId
     * @param areaId 区域
     * @return List<Floor>
     */
    public List<Floor> getAllFloors(Integer userId, Integer areaId) {
        UserDto userDtoInfo = userLogic.getUserInfoWithReLoginAndFrontException(userId);
        List<Floor> floors = GetFloorAction.getFloor(new UserCookieInfo(userDtoInfo.getCookie(), userDtoInfo.getLoginIdWeaver()), AreaTypeEnum.getAreaTypeEnum(areaId));
        log.info(String.valueOf(floors));
        return floors;
    }

    /**
     * 获取某层楼下的会议室信息
     *
     * @param floor  楼
     * @param userId userId
     * @return List<Room>
     */
    public List<Room> getFloorRooms(Floor floor, Integer userId) {
        if (floor == null) {
            return new ArrayList<>();
        }

        UserDto userDtoInfo = userLogic.getUserInfoWithReLoginAndFrontException(userId);

        GetSpareRoomAction.Query query = new GetSpareRoomAction.Query();
        query.setDate(System.currentTimeMillis() + 86400000 * 2);
        query.setAreaId(AreaTypeEnum.getAreaTypeEnum(floor.getAreaId()).getAreaId());
        query.setBuildingId(floor.getBuildingId());
        query.setFloorId(floor.getId());
        return GetSpareRoomAction.getRooms(query, new UserCookieInfo(userDtoInfo.getCookie(), userDtoInfo.getLoginIdWeaver()));
    }

    /**
     * 预定会议室
     *
     * @param userId      userId
     * @param dayString   某天 year-month-day，和 weekStrings 二选一
     * @param hourBegin   小时开始时间
     * @param hourEnd     小时结束时间
     * @param minuteBegin 分钟开始时间
     * @param minuteEnd   分钟结束时间
     * @param roomId      会议室 id
     * @param areaId      区域 id
     * @param meetingName 会议名
     * @param roomName    会议室名字
     * @param bookTime    预定期限
     * @param weekStrings 周期预定的周期数组，和 dayString 二选一
     * @param autoSignIn  是否自动预定
     * @return Boolean
     */
    public Boolean bookRoom(Integer userId, String dayString, String hourBegin, String hourEnd, String minuteBegin, String minuteEnd, Long roomId, Integer areaId, String meetingName, String roomName, String bookTime, String weekStrings, Integer autoSignIn) {
        if (userId == null || roomId == null || areaId == null || StringUtils.isBlank(bookTime)) {
            throw new FrontException("参数错误");
        }

        // 开始组装预定信息
        // 包含至少一个 null 或者全是非 null 数字
        List<Integer> weeks = BookMeetingInfoDto.weekToList(weekStrings);
        if (StringUtils.isBlank(weekStrings)) {
            if (StringUtils.isBlank(dayString) || StringUtils.isBlank(hourBegin) || StringUtils.isBlank(hourEnd) || StringUtils.isBlank(minuteBegin) || StringUtils.isBlank(minuteEnd)) {
                throw new FrontException("参数错误");
            }
        } else {
            if (!BookMeetingInfoDto.isWeekStringValid(weekStrings)) {
                throw new FrontException("参数错误");
            }
            dayString = "0-0-0";
        }

        String[] days = dayString.split("-");
        int length = 3;
        if (days.length != length) {
            throw new FrontException("日期格式错误");
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
            throw new FrontException("时间起止错误");
        }
        TimeEnum timeBeginTimeEnum = TimeEnum.getByTime(timeBegin);
        TimeEnum timeEndTimeEnum = TimeEnum.getByTime(timeEnd);
        if (timeBeginTimeEnum == null || timeEndTimeEnum == null) {
            throw new FrontException("时间起止错误");
        }

        // 查询自己的和他人的预定列表
        List<BookMeetingInfoDto> bookMeetingInfoList = bookMeetingInfoService.getUserBookMeetings(null);
        List<BookMeetingInfoDto> currentUserBookList = new ArrayList<>();
        List<BookMeetingInfoDto> otherUserBookList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(bookMeetingInfoList)) {
            for (BookMeetingInfoDto info : bookMeetingInfoList) {
                if (userId.equals(info.getUserId())) {
                    currentUserBookList.add(info);
                } else {
                    otherUserBookList.add(info);
                }
            }
        }

        // 检查冲突和预定
        for (Integer week : weeks) {
            BookMeetingInfoDto add = new BookMeetingInfoDto();
            add.setYear(year);
            add.setMonth(month);
            add.setDay(day);
            add.setTimeBegin(timeBegin);
            add.setTimeEnd(timeEnd);
            add.setRoomId(roomId);
            add.setAreaId(areaId);
            add.setRoomName(roomName);
            add.setBookTime(bookTime);
            add.setWeek(week);
            add.setAutoSignIn(autoSignIn);
            if (StringUtils.isNotBlank(meetingName)) {
                add.setMeetingName(meetingName);
            }

            // 检查和自己的预定是否有冲突的
            for (BookMeetingInfoDto info : currentUserBookList) {
                if (BookMeetingInfoDto.isConflict(add, info)) {
                    String format = "与自己的预约信息重复\n%s";
                    throw new FrontException(String.format(format, info.bookInfo()));
                }
            }

            // 检查和他人的预定是否有冲突的
            for (BookMeetingInfoDto info : otherUserBookList) {
                if (BookMeetingInfoDto.isConflict(add, info)) {
                    UserDto userInfo = userLogic.getById(info.getUserId());
                    if (userInfo == null) {
                        bookMeetingInfoService.deleteBookInfos(Collections.singletonList(info.getId()));
                        log.warn("[删除] 有预定信息，但是无对应用户信息，bookId = " + info.getId() + "，信息：" + info.bookInfo());
                        continue;
                    }
                    String format = "与他人的预约信息重复\n人员：%s\n%s";
                    throw new FrontException(String.format(format, userInfo.getUsername(), info.bookInfo()));
                }
            }

            bookMeetingInfoService.addBookInfos(userId, Collections.singletonList(add));
        }

        return true;
    }

    /**
     * 获取用户名下预定的会议室
     *
     * @param userId userId
     * @return List<BookMeetingInfoDto>
     */
    public List<BookMeetingInfoDto> getBookedRooms(Integer userId) {
        return bookMeetingInfoService.getUserBookMeetings(userId);
    }

    /**
     * 取消预定任务
     *
     * @param userId userId
     * @param bookId bookId
     * @return Boolean
     */
    public Boolean cancelBookRoom(Integer userId, Long bookId) {
        if (bookId == null) {
            throw new FrontException("输入错误");
        }

        List<BookMeetingInfoDto> bookMeetingInfoList = bookMeetingInfoService.getUserBookMeetings(userId);
        if (CollectionUtils.isEmpty(bookMeetingInfoList)) {
            throw new FrontException("无对应此 id");
        }

        for (BookMeetingInfoDto bookMeetingInfoDto : bookMeetingInfoList) {
            if (bookId.equals(bookMeetingInfoDto.getId())) {
                bookMeetingInfoService.deleteBookInfos(Collections.singletonList(bookId));
                return true;
            }
        }

        throw new FrontException("无对应此 id");
    }
}
