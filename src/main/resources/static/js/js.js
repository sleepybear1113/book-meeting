let floors = {};
let rooms = {};

function deleteUserConfirm() {
    let b = confirm("确认删除本地用户？");
    if (b === true) {
        deleteUser();
    }
}

function fillUserInfo(user) {
    if (user == null) {
        return;
    }
    document.getElementById("username").value = user.username;
    let authType = user.authType;
    if (authType != null) {
        document.getElementById(authType).checked = true;
    }
}

function fillBookedInfo(bookMeetingInfoList) {
    if (bookMeetingInfoList == null) {
        return;
    }

    let bookedRoomTableResult = document.getElementById("booked-rooms-table-result-body");
    let html = "";
    for (let i = 0; i < bookMeetingInfoList.length; i++) {
        let bookMeetingInfo = bookMeetingInfoList[i];
        let timeStr = "";
        let week = bookMeetingInfo.week;
        if (week == null) {
            timeStr = bookMeetingInfo.year + "/" + bookMeetingInfo.month + "/" + bookMeetingInfo.day;
        } else {
            timeStr = "每周" + getWeekString(week);
        }

        timeStr += " " + bookMeetingInfo.timeBegin + "-" + bookMeetingInfo.timeEnd;
        html += `
        <tr>
            <th style="border: 1px solid">${bookMeetingInfo.id}</th>
            <th style="border: 1px solid">${bookMeetingInfo.roomName}</th>
            <th style="border: 1px solid">${bookMeetingInfo.meetingName || "会议"}</th>
            <th style="border: 1px solid">${timeStr}</th>
            <th style="border: 1px solid">${bookMeetingInfo.autoSignIn === 1}</th>
            <th><button onclick="cancelBookRoom(${bookMeetingInfo.id})">取消</button></th>
            <th style="border: 1px solid">${bookMeetingInfo.joinPeopleList.join(",")}</th>
        </tr>`;
    }
    bookedRoomTableResult.innerHTML = html;
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
        timeOnce.setAttribute("readOnly", "true");
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
                <th id="book-room-name">${room.roomName}</th>
                <th id="book-room-floor-name">${room.floorName}</th>
                <th>${room.roomNumPeople}</th>
                <th id="book-time">${room.bookTime}</th>
            </tr>`;
}

function fillRooms(response) {
    if (!processErrorResult(response)) {
        return;
    }

    let roomResult = response.result;
    if (roomResult == null || roomResult.length === 0) {
        alert("无结果");
    }

    let roomTableResultBody = document.getElementById("room-table-result-body");
    let html = "";
    for (let i = 0; i < roomResult.length; i++) {
        let room = new Room(roomResult[i]);
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
}

function fillFloors(response) {
    let floorResult = response.result;
    if (floorResult == null || floorResult.length === 0) {
        alert("没有楼层信息");
        return;
    }

    floors = {};
    let buildingFloorTableResultBody = document.getElementById("building-floor-table-result-body");
    let html = "";
    for (let i = 0; i < floorResult.length; i++) {
        let floor = new Floor(floorResult[i]);
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

function switchToAnnual() {
    let bookAnnual = document.getElementById("annual");
    bookAnnual.checked = true;
}

function switchToOnce() {
    let once = document.getElementById("once");
    once.checked = true;
}

function getWeeks() {
    let weeks = "";
    let weeksElements = document.getElementsByName("book-annual-check-box");
    for (let i = 0; i < weeksElements.length; i++) {
        let e = weeksElements[i];
        if (e.checked) {
            weeks += e.value;
        }
    }
    return weeks;
}

function getWeekString(week) {
    if (week == null) {
        return "未知";
    }

    switch (week) {
        case 1:
            return "一";
        case 2:
            return "二";
        case 3:
            return "三";
        case 4:
            return "四";
        case 5:
            return "五";
        case 6:
            return "六";
        case 7:
            return "日";
    }
    return "未知";
}

function fillQueryUserInfo(userInfoList) {
    if (userInfoList == null || userInfoList.length === 0) {
        return;
    }

    let resultBody = document.getElementById("user-info-display-table-result-body");
    let html = "";
    for (let i = 0; i < userInfoList.length; i++) {
        let item = userInfoList[i];
        let userInfo = new UserInfo(item);
        let userIdName = String(userInfo.userId) + "@" + userInfo.lastName;
        console.log(userIdName);
        html += `<tr>
                   <th style="border: 1px solid">${userInfo.lastName}</th>
                   <th style="border: 1px solid">${userInfo.dep1Name}</th>
                   <th style="border: 1px solid">${userInfo.dep2Name}</th>
                   <th style="border: 1px solid">${userInfo.dept3Name}</th>
                   <th style="border: 1px solid">${userInfo.userId}</th>
                   <th style="border: 1px solid">${userInfo.email}</th>
                   <th><button onclick="addJoinPeople(${userInfo.userId}, '${userInfo.lastName}')">添加</button></th>
               </tr>`
    }

    resultBody.innerHTML = html;
}

function addJoinPeople(userId, username) {
    let joinList = document.getElementById("join-people-list");
    let children = joinList.children;
    for (let i = 0; i < children.length; i++) {
        let child = children[i];
        if (parseInt(child.id) === parseInt(userId)) {
            return;
        }
    }

    joinList.innerHTML += `<button id="${userId}" onclick="deleteJoinPeople(${userId})">${username}@${userId}</button>`
}

function deleteJoinPeople(userId) {
    let joinList = document.getElementById("join-people-list");
    let children = joinList.children;
    for (let i = 0; i < children.length; i++) {
        let child = children[i];
        if (parseInt(child.id) === parseInt(userId)) {
            joinList.removeChild(child);
            return;
        }
    }
}

function getJoinPeople() {
    let joinPeople = [];
    let joinListDiv = document.getElementById("join-people-list");
    for (let i = 0; i < joinListDiv.children.length; i++) {
        let child = joinListDiv.children[i];
        joinPeople.push(child.innerText);
    }
    return joinPeople.join(",");
}