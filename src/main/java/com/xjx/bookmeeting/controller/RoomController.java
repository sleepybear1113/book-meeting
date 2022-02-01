package com.xjx.bookmeeting.controller;

import com.xjx.bookmeeting.dto.BookMeetingInfoDto;
import com.xjx.bookmeeting.dto.Floor;
import com.xjx.bookmeeting.dto.Room;
import com.xjx.bookmeeting.dto.UserDto;
import com.xjx.bookmeeting.helper.CookieHelper;
import com.xjx.bookmeeting.logic.RoomLogic;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/7 16:59
 */
@RestController
@Slf4j
public class RoomController {
    @Resource
    private RoomLogic roomLogic;

    @RequestMapping("/room/getAllFloors")
    public List<Floor> getAllFloors(Integer areaId) {
        UserDto userDto = CookieHelper.getLoginCookieWithFrontException();
        return roomLogic.getAllFloors(userDto.getId(), areaId);
    }

    @RequestMapping("/room/getSpareRoom")
    public List<Room> getSpareRoom(@RequestBody Floor floor) {
        UserDto userDto = CookieHelper.getLoginCookieWithFrontException();
        return roomLogic.getFloorRooms(floor, userDto.getId());
    }

    @RequestMapping("/room/bookRoom")
    public Boolean bookRoom(String day, String hourBegin, String hourEnd, String minuteBegin, String minuteEnd, Long roomId, Integer areaId, String meetingName, String roomName, String bookTime, String weeks, Integer autoSignIn) {
        UserDto userDto = CookieHelper.getLoginCookieWithFrontException();
        return roomLogic.bookRoom(userDto.getId(), day, hourBegin, hourEnd, minuteBegin, minuteEnd, roomId, areaId, meetingName, roomName, bookTime, weeks, autoSignIn);
    }

    @RequestMapping("/room/getBookedRooms")
    public List<BookMeetingInfoDto> getBookedRooms() {
        UserDto userDto = CookieHelper.getLoginCookieWithFrontException();
        return roomLogic.getBookedRooms(userDto.getId());
    }

    @RequestMapping("/room/cancelBookRoom")
    public Boolean cancelBookRoom(Long id) {
        UserDto userDto = CookieHelper.getLoginCookieWithFrontException();
        return roomLogic.cancelBookRoom(userDto.getId(), id);
    }
}
