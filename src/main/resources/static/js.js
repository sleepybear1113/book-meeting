let floors = {};
let rooms = {};

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
        // 接口数据
        let data = res.data;
        console.log(data);
        if (!processErrorResult(data)) {
            return;
        }

        let success = data.result;
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
        // 接口数据
        let data = res.data;
        console.log(data);
        if (!processErrorResult(data)) {
            return;
        }

        let floorResult = data.result;
        if (floorResult == null || floorResult.length === 0) {
            alert("没有楼层信息");
            return;
        }

        floors = {};
        let buildingFloorTableResultBody = document.getElementById("building-floor-table-result-body");
        let html = "";
        for (let i = 0; i < floorResult.length; i++) {
            let tmp = floorResult[i];
            let floor = new Floor(tmp.areaId, tmp.buildingId, tmp.id, tmp.buildingName, tmp.floorName);
            floors[floor.id] = floor;

            html += `
        <tr>
            <th>${floor.id}</th>
            <th>${floor.buildingName}</th>
            <th>${floor.floorName}</th>
            <th><button onclick="getSpareRoom(${floor.id})">查看</button></th>
        </tr>`;

        }
        buildingFloorTableResultBody.innerHTML = html;
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
        // 接口数据
        let data = res.data;
        console.log(data);
        if (!processErrorResult(data)) {
            return;
        }

        let result = data.result;

        let roomTableResultBody = document.getElementById("room-table-result-body");
        let html = "";
        for (let i = 0; i < result.length; i++) {
            let roomTmp = result[i];
            let room = new Room(roomTmp.id, roomTmp.floorId, roomTmp.buildingId, roomTmp.areaId, roomTmp.roomName, roomTmp.floorName, roomTmp.roomNumPeople, roomTmp.meetingBooked, roomTmp.bookTime);
            rooms[room.id] = room;
            html += `
        <tr>
            <th>${room.id}</th>
            <th>${room.roomName}</th>
            <th>${room.floorName}</th>
            <th>${room.roomNumPeople}</th>
            <th>${room.bookTime}</th>
            <th><button id="fill-book-info" onclick="fillBookInfo(${room.id})">预定</button></th>
        </tr>`;
        }

        roomTableResultBody.innerHTML = html;
    });
}

function fillBookInfo(id) {
    let room = rooms[id];
    if (room == null) {
        alert("空的会议室信息");
        return;
    }

    let timeOnce = document.getElementById("time-once");
    let bookDays = getBookDays(room.bookTime);
    if (bookDays === 0) {
        timeOnce.setAttribute("readOnly", true);
        timeOnce.value = "";
    } else {
        timeOnce.removeAttribute("readOnly");
    }
    let date = new Date();
    let year = date.getFullYear();
    let month = date.getMonth();
    let day = date.getDate();
    let minDate = new Date(year, month, day + bookDays);

    let month1 = minDate.getMonth() + 1;
    let minMonth = (month1 < 10 ? "0" : "") + String(month1);
    let minDay = (minDate.getDate() < 10 ? "0" : "") + String(minDate.getDate());

    timeOnce.min = minDate.getFullYear() + "-" + minMonth + "-" + minDay;

    let bookInfo = document.getElementById("room-book-table-result-body");
    bookInfo.innerHTML = `
            <tr>
                <th id="book-room-id">${room.id}</th>
                <th id="book-area-id">${room.areaId}</th>
                <th>${room.roomName}</th>
                <th>${room.floorName}</th>
                <th>${room.roomNumPeople}</th>
                <th>${room.bookTime}</th>
            </tr>`;
}

function processErrorResult(result) {
    if (result == null) {
        alert("返回值为空");
        return false;
    }

    let code = result.code;
    let message = result.message;
    if (code === 0) {
        return true;
    }

    if (code > -10) {
        alert(message);
    } else {
        alert("系统错误！\n" + message);
    }
    return false;
}

function changeVisible(type) {
    let bookOnce = document.getElementById("book-once");
    let bookTimed = document.getElementById("book-timed");
    if (type === 1) {
        bookOnce.style.display = "";
        bookTimed.style.display = "none";
    } else if (type === 2) {
        bookOnce.style.display = "none";
        bookTimed.style.display = "";
    }
}


function bookOnce() {
    let day = document.getElementById("time-once").value;
    let hourBegin = document.getElementById("once-hour-begin-select").value;
    let hourEnd = document.getElementById("once-hour-end-select").value;
    let minuteBegin = document.getElementById("once-minute-begin-select").value;
    let minuteEnd = document.getElementById("once-minute-end-select").value;
    let roomIdItem = document.getElementById("book-room-id");
    let areaIdItem = document.getElementById("book-area-id");

    let bookOnceMeetingOnce = document.getElementById("book-once-meeting-once").value;

    if (day == null || day === "" || roomIdItem == null || areaIdItem === "") {
        alert("输入错误");
        return;
    }

    let url = "/room/bookRoom";
    axios.get(url, {
        params: {
            day: day,
            hourBegin: hourBegin,
            hourEnd: hourEnd,
            minuteBegin: minuteBegin,
            minuteEnd: minuteEnd,
            roomId: roomIdItem.innerText,
            areaId: areaIdItem.innerText,
            name: bookOnceMeetingOnce
        }
    }).then(res => {
        // 接口数据
        let data = res.data;
        console.log(data);
        if (!processErrorResult(data)) {
            return;
        }

        let result = data.result;
        if (result) {
            alert("成功");
        } else {
            alert("失败");
        }
    });
}

function Floor(areaId, buildingId, id, buildingName, floorName) {
    this.areaId = areaId;
    this.buildingId = buildingId;
    this.buildingName = buildingName;
    this.id = id;
    this.floorName = floorName;
}

function Room(id, floorId, buildingId, areaId, roomName, floorName, roomNumPeople, meetingBooked, bookTime) {
    this.id = id;
    this.floorId = floorId;
    this.buildingId = buildingId;
    this.areaId = areaId;
    this.roomName = roomName;
    this.floorName = floorName;
    this.roomNumPeople = roomNumPeople;
    this.meetingBooked = meetingBooked;
    this.bookTime = bookTime;
}

function getBookDays(bookStr) {
    if (bookStr == null) {
        return 0;
    }
    let hasDays = bookStr.indexOf("days") > -1;
    let hasMonths = bookStr.indexOf("months") > -1;
    if (hasDays && !hasMonths) {
        return parseInt(bookStr.replace("days", ""));
    }

    if (!hasDays && hasMonths) {
        return parseInt(bookStr.replace("months", "")) * 30;
    }

    return 0;
}