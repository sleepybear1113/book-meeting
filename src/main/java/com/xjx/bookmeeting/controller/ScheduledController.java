package com.xjx.bookmeeting.controller;

import com.xjx.bookmeeting.logic.BookMeetingLogic;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务外部接口调用类
 *
 * @author xjx
 * @date 2021/10/10 1:13
 */
@RestController
public class ScheduledController {
    @Resource
    private BookMeetingLogic bookMeetingLogic;

    @RequestMapping("/scheduled/bookAll")
    public void bookAll() {
        bookMeetingLogic.bookAllUsers();
    }
}
