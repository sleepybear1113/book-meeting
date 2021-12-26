/**
 * 这个文件是用来存放相关对象的
 */

/**
 * 楼层对象
 * @param o
 * @returns {null}
 * @constructor
 */
function Floor(o) {
    if (o == null) {
        return null;
    }
    this.id = o.id;
    this.createTime = o.createTime;
    this.modifyTime = o.modifyTime;

    this.areaId = o.areaId;
    this.buildingId = o.buildingId;
    this.buildingName = o.buildingName;
    this.floorName = o.floorName;
}

/**
 * 会议室对象
 * @param o
 * @returns {null}
 * @constructor
 */
function Room(o) {
    if (o == null) {
        return null;
    }
    this.id = o.id;
    this.createTime = o.createTime;
    this.modifyTime = o.modifyTime;

    this.floorId = o.floorId;
    this.buildingId = o.buildingId;
    this.areaId = o.areaId;
    this.roomName = o.roomName;
    this.floorName = o.floorName;
    this.roomNumPeople = o.roomNumPeople;
    this.meetingBooked = o.meetingBooked;
    this.bookTime = o.bookTime;
}

/**
 * 用户信息
 * @param o
 * @returns {null}
 * @constructor
 */
function User(o) {
    if (o == null) {
        return null;
    }
    this.id = o.id;
    this.createTime = o.createTime;
    this.modifyTime = o.modifyTime;

    this.username = o.username;
    this.password = o.password;
    this.authType = o.authType;
    this.cookie = o.cookie;
    this.loginIdWeaver = o.loginIdWeaver;

    if (o.bookMeetingInfoList != null) {
        let bookMeetingInfoList = o.bookMeetingInfoList;
        let list = [];
        for (let i = 0; i < bookMeetingInfoList.length; i++) {
            list.push(new BookOnceInfo(bookMeetingInfoList[i]));
        }
        this.bookMeetingInfoList = list;
    }
}

/**
 * 预定信息
 * @param o
 * @returns {null}
 * @constructor
 */
function BookOnceInfo(o) {
    if (o == null) {
        return null;
    }
    this.id = o.id;
    this.createTime = o.createTime;
    this.modifyTime = o.modifyTime;

    this.year = o.year;
    this.month = o.month;
    this.day = o.day;
    this.timeBegin = o.timeBegin;
    this.timeEnd = o.timeEnd;
    this.areaId = o.areaId;
    this.roomId = o.roomId;
    this.meetingName = o.meetingName;
    this.roomName = o.roomName;
    this.week = o.week;
    this.autoSignIn = o.autoSignIn;
}