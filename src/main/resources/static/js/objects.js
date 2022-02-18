/**
 * 这个文件是用来存放相关对象的
 */

/**
 * 基础类
 */
class BaseDomain {
    constructor(props) {
        if (props != null) {
            this.id = props.id;
            this.createTime = props.createTime;
            this.modifyTime = props.modifyTime;
        }
    }
}

/**
 * 楼层对象
 */
class Floor extends BaseDomain {
    constructor(props) {
        super(props);
        if (props == null) {
            return;
        }

        this.areaId = props.areaId;
        this.buildingId = props.buildingId;
        this.buildingName = props.buildingName;
        this.floorName = props.floorName;
    }
}

/**
 * 会议室对象
 */
class Room extends BaseDomain {
    constructor(props) {
        super(props);

        if (props == null) {
            return;
        }
        this.floorId = props.floorId;
        this.buildingId = props.buildingId;
        this.areaId = props.areaId;
        this.roomName = props.roomName;
        this.floorName = props.floorName;
        this.roomNumPeople = props.roomNumPeople;
        this.meetingBooked = props.meetingBooked;
        this.bookTime = props.bookTime;
    }
}

/**
 * 用户信息
 */
class User extends BaseDomain {
    constructor(props) {
        super(props);

        if (props == null) {
            return;
        }

        this.username = props.username;
        this.password = props.password;
        this.authType = props.authType;
        this.cookie = props.cookie;
        this.loginIdWeaver = props.loginIdWeaver;

    }
}

/**
 * 预定信息
 */
class BookMeetingInfo extends BaseDomain {
    constructor(props) {
        super(props);

        if (props == null) {
            return;
        }
        this.year = props.year;
        this.month = props.month;
        this.day = props.day;
        this.timeBegin = props.timeBegin;
        this.timeEnd = props.timeEnd;
        this.areaId = props.areaId;
        this.roomId = props.roomId;
        this.meetingName = props.meetingName;
        this.roomName = props.roomName;
        this.week = props.week;
        this.autoSignIn = props.autoSignIn;
        this.bookTime = props.bookTime;

        let joinPeople = props.joinPeople;
        if (joinPeople == null || joinPeople === "") {
            this.joinPeopleList = [];
        } else {
            this.joinPeopleList = joinPeople.split(",");
        }
    }
}

class UserInfo extends BaseDomain {
    constructor(props) {
        super(props);

        if (props == null) {
            return;
        }
        this.claimComp = props.claimComp;
        this.claimCompName = props.claimCompName;
        this.costCenter = props.costCenter;
        this.dep1Id = props.dep1Id;
        this.dep1Name = props.dep1Name;
        this.dep1NameEx = props.dep1NameEx;
        this.dep2Id = props.dep2Id;
        this.dep2Name = props.dep2Name;
        this.depFullName = props.depFullName;
        this.dept3Id = props.dept3Id;
        this.dept3Name = props.dept3Name;
        this.email = props.email;
        this.isGameDept = props.isGameDept;
        this.jobTitle = props.jobTitle;
        this.jobTitleId = props.jobTitleId;
        this.lastName = props.lastName;
        this.loginId = props.loginId;
        this.sex = props.sex;
        this.userId = props.userId;
    }
}