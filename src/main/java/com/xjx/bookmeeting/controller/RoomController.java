package com.xjx.bookmeeting.controller;

import com.xjx.bookmeeting.dto.Floor;
import com.xjx.bookmeeting.dto.Room;
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
    public List<Floor> getAllFloors(String username, String authType, Integer areaId) {
        return roomLogic.getAllFloors(username, authType, areaId);
    }

    @RequestMapping("/room/getSpareRoom")
    public List<Room> getSpareRoom(@RequestBody Floor floor, String username, String authType) {
        return roomLogic.roomLogic(floor, username, authType);
    }

    @RequestMapping("/room/bookRoom")
    public Boolean bookRoom(String username, String authType, String day, String hourBegin, String hourEnd, String minuteBegin, String minuteEnd, Long roomId, Integer areaId) {
        return roomLogic.bookRoom(username, authType, day, hourBegin, hourEnd, minuteBegin, minuteEnd, roomId, areaId);
    }
}
