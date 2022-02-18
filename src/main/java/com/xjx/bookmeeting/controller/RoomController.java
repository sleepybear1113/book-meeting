package com.xjx.bookmeeting.controller;

import com.xjx.bookmeeting.actions.dto.Floor;
import com.xjx.bookmeeting.actions.dto.Room;
import com.xjx.bookmeeting.dto.BookMeetingInfoDto;
import com.xjx.bookmeeting.dto.UserDto;
import com.xjx.bookmeeting.helper.CookieHelper;
import com.xjx.bookmeeting.logic.RoomLogic;
import com.xjx.bookmeeting.query.BookMeetingInfoQuery;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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
    public Boolean bookRoom(@RequestBody BookMeetingInfoQuery bookMeetingInfoDtoQuery, HttpServletRequest request) {
        UserDto userDto = CookieHelper.getLoginCookieWithFrontException();
        return roomLogic.bookRoom(userDto.getId(), bookMeetingInfoDtoQuery);
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
