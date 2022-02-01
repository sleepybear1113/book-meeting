function initUserInfo() {
    let url = "/user/getUserInfo";
    axios.get(url).then(res => {
        let response = res.data;
        if (!processErrorResult(response)) {
            return;
        }

        let user = new User(response.result);
        if (user == null) {
            return;
        }
        fillUserInfo(user);
        getUserBookedRooms();
    });
}

function getUserBookedRooms() {
    let url = "/room/getBookedRooms";
    axios.get(url).then(res => {
        let response = res.data;
        if (!processErrorResult(response)) {
            return;
        }

        if (response.result == null || response.result.length === 0) {
            return;
        }

        let bookMeetingInfoList = [];
        for (let i = 0; i < response.result.length; i++) {
            bookMeetingInfoList.push(new BookMeetingInfo(response.result[i]));
        }
        fillBookedInfo(bookMeetingInfoList);
    });
}

function cancelBookRoom(id) {
    let url = "/room/cancelBookRoom";
    axios.get(url, {
        params: {
            id: id
        }
    }).then(res => {
        let response = res.data;
        if (!processErrorResult(response)) {
            return;
        }

        if (response.result) {
            getUserBookedRooms();
        } else {
            alert("失败");
        }
    });
}

function testLogin() {
    let username = document.getElementById("username").value;
    let password = document.getElementById("password").value;

    let authType = "corp";
    if (document.getElementById("mesg").checked === true) {
        authType = "mesg"
    } else if (document.getElementById("yixin").checked === true) {
        authType = "yixin"
    } else if (document.getElementById("ezxr").checked === true) {
        authType = "ezxr"
    }

    if (username == null || password == null || username.trim().length === 0 || password.trim().length === 0) {
        alert("用户名或密码为空");
        return;
    }

    let url = "/user/login";
    axios.get(url, {
        params: {
            username: username,
            password: password,
            authType: authType
        }
    }).then(res => {
        let response = res.data;
        if (!processErrorResult(response)) {
            return;
        }

        let success = response.result;
        if (success) {
            alert("成功");
        } else {
            alert("失败")
        }
    });
}

function deleteUser() {
    let url = "/user/deleteUser";
    axios.get(url).then(res => {
        let response = res.data;
        if (!processErrorResult(response)) {
            return;
        }

        let success = response.result;
        if (success) {
            alert("成功");
        } else {
            alert("失败")
        }
    });
}

function getAllFloors() {
    let areas = document.getElementsByName("area");
    let areaId = 5;
    for (let i = 0; i < areas.length; i++) {
        let area = areas[i];
        if (area.checked) {
            areaId = area.value;
            break;
        }
    }

    let url = "/room/getAllFloors";
    axios.get(url, {
        params: {
            areaId: areaId
        }
    }).then(res => {
        let response = res.data;
        if (!processErrorResult(response)) {
            return;
        }

        fillFloors(response);
    });
}

function getSpareRoom(floorId) {
    if (floorId == null) {
        alert("空楼层信息");
        return;
    }

    let floor = floors[floorId];
    if (floor == null) {
        alert("空楼层信息");
        return;
    }

    let url = "/room/getSpareRoom";
    axios.post(url, floor).then(res => {
        let response = res.data;
        fillRooms(response);
    });
}

function bookRoom() {
    let day = document.getElementById("time-once").value;
    let hourBegin = document.getElementById("once-hour-begin-select").value;
    let hourEnd = document.getElementById("once-hour-end-select").value;
    let minuteBegin = document.getElementById("once-minute-begin-select").value;
    let minuteEnd = document.getElementById("once-minute-end-select").value;
    let roomIdItem = document.getElementById("book-room-id").innerText;
    let areaIdItem = document.getElementById("book-area-id").innerText;
    let bookRoomName = document.getElementById("book-room-name").innerText;
    let bookRoomFloorName = document.getElementById("book-room-floor-name").innerText;
    let bookOnceMeetingOnce = document.getElementById("book-once-meeting-once").value;
    let bookTime = document.getElementById("book-time").innerText;
    let annualChecked = document.getElementById("annual").checked;
    let autoSignIn = document.getElementById("auto-sign-in-checkbox").checked;

    let weekOptions = getWeeks();
    if (annualChecked) {
        if (weekOptions.length === 0) {
            alert("未选择时间");
            return;
        }
    } else {
        if (day == null || day === "" || roomIdItem == null || areaIdItem === "") {
            alert("输入错误");
            return;
        }
        weekOptions = "";
    }

    let url = "/room/bookRoom";
    axios.get(url, {
        params: {
            day: day,
            hourBegin: hourBegin,
            hourEnd: hourEnd,
            minuteBegin: minuteBegin,
            minuteEnd: minuteEnd,
            roomId: roomIdItem,
            areaId: areaIdItem,
            meetingName: bookOnceMeetingOnce,
            roomName: bookRoomFloorName + bookRoomName,
            bookTime: bookTime,
            weeks: weekOptions,
            autoSignIn: autoSignIn === true ? 1 : 0,
        }
    }).then(res => {
        let response = res.data;
        console.log(response);
        if (!processErrorResult(response)) {
            return;
        }

        let result = response.result;
        if (result) {
            alert("成功");
            getUserBookedRooms();
        } else {
            alert("失败");
        }
    });
}
