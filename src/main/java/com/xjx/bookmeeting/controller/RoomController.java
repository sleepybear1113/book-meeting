package com.xjx.bookmeeting.controller;

import com.xjx.bookmeeting.domain.User;
import com.xjx.bookmeeting.dto.Floor;
import com.xjx.bookmeeting.dto.Room;
import com.xjx.bookmeeting.helper.CookieHelper;
import com.xjx.bookmeeting.logic.RoomLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private RoomLogic roomLogic;

    @RequestMapping("/room/getAllFloors")
    public List<Floor> getAllFloors(Integer areaId) {
        User user = CookieHelper.getLoginCookieWithFrontException();
        return roomLogic.getAllFloors(user, areaId);
    }

    @RequestMapping("/room/getSpareRoom")
    public List<Room> getSpareRoom(@RequestBody Floor floor) {
        User user = CookieHelper.getLoginCookieWithFrontException();
        return roomLogic.roomLogic(floor, user);
    }

    @RequestMapping("/room/bookRoom")
    public Boolean bookRoom(String day, String hourBegin, String hourEnd, String minuteBegin, String minuteEnd, Long roomId, Integer areaId, String meetingName, String roomName, String bookTime, String weeks) {
        User user = CookieHelper.getLoginCookieWithFrontException();
        return roomLogic.bookRoom(user, day, hourBegin, hourEnd, minuteBegin, minuteEnd, roomId, areaId, meetingName, roomName, bookTime, weeks);
    }

    @RequestMapping("/room/cancelBookRoom")
    public Boolean cancelBookRoom(Long id) {
        User user = CookieHelper.getLoginCookieWithFrontException();
        return roomLogic.cancelBookRoom(user, id);
    }
}
