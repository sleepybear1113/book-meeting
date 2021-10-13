package com.xjx.bookmeeting;

import com.xjx.bookmeeting.actions.BookRoomAction;
import com.xjx.bookmeeting.actions.GetFloorAction;
import com.xjx.bookmeeting.actions.GetRoomBookedAction;
import com.xjx.bookmeeting.actions.GetSpareRoomAction;
import com.xjx.bookmeeting.dto.BookRoomResult;
import com.xjx.bookmeeting.dto.Floor;
import com.xjx.bookmeeting.dto.MeetingBooked;
import com.xjx.bookmeeting.dto.Room;
import com.xjx.bookmeeting.enumeration.AreaTypeEnum;
import com.xjx.bookmeeting.enumeration.TimeEnum;
import com.xjx.bookmeeting.login.MeetingLogin;
import com.xjx.bookmeeting.login.UserCookieInfo;
import com.xjx.bookmeeting.service.UserService;
import com.xjx.bookmeeting.utils.FileUtils;
import com.xjx.bookmeeting.utils.OtherUtils;
import com.xjx.bookmeeting.utils.http.HttpHelper;
import com.xjx.bookmeeting.utils.http.HttpResponseHelper;
import com.xjx.bookmeeting.utils.login.UserInfo;
import org.apache.http.HttpHost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.List;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/8/30 1:05
 */
public class Main {
    public static final String ID = "134017";
    public static Cookie cookie = new BasicClientCookie("JSESSIONID", "aaaP2tMwPulFcaxTWVzXx");
    public static UserCookieInfo userCookieInfo = new UserCookieInfo(cookie.getValue(), ID);

    public static void main(String[] args) {
        base64();
    }

    public static void base64() {
        String s = "123zxc123";
        String encrypt = OtherUtils.encrypt(s);
        String decrypt = OtherUtils.decrypt(encrypt);
        System.out.println(encrypt);
        System.out.println(decrypt);
    }

    public static void makeFile() {
        FileUtils.writeToFile("aaa", "a/e.txt");
    }

    public static void listFiles() {
        FileUtils.listFilesInDir(UserService.USER_PATH);
    }

    public static void book() {
        BookRoomAction.FormData formData = new BookRoomAction.FormData();
        formData.setTimeRange(2021, 10, 10, TimeEnum.T_1000, TimeEnum.T_1030);
        formData.setAreaId(AreaTypeEnum.HANGZHOU);
        formData.setJoinUserIds(ID);
        formData.setMeetingRoomId(1689L);

        BookRoomResult book = BookRoomAction.book(userCookieInfo, formData);
    }

    public static void proxy() {
        String url = "https://www.jd.com";
        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
        httpHelper.setHttpHost(new HttpHost("lh", 18888, "https"));
        HttpResponseHelper response = httpHelper.request();
        System.out.println(response);
    }

    public static void getBookedRoom() {
        GetRoomBookedAction.Query query = new GetRoomBookedAction.Query();
        query.setRoomId(1679L);
        query.setTodayDate();
        List<MeetingBooked> bookedRoom = GetRoomBookedAction.getBookedRoom(query, cookie);
        System.out.println(bookedRoom);
    }

    public static void getRooms() {
        GetSpareRoomAction.Query query = new GetSpareRoomAction.Query();
        query.setDate(System.currentTimeMillis() + 86400000 * 2);
        query.setAreaId(AreaTypeEnum.HANGZHOU.getAreaId());
        query.setBuildingId(32);
        query.setFloorId(1130L);
        List<Room> rooms = GetSpareRoomAction.getRooms(query, userCookieInfo);
        System.out.println(rooms);
    }

    public static void getFloor() {
        List<Floor> floors = GetFloorAction.getFloor(userCookieInfo, AreaTypeEnum.HANGZHOU);
        System.out.println(floors);
    }

    public static void login() {
    }
}
