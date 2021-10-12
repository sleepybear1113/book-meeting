package com.xjx.bookmeeting.controller;

import com.xjx.bookmeeting.logic.ScheduledLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/10 1:13
 */
@RestController
public class ScheduledController {
    @Autowired
    private ScheduledLogic scheduledLogic;

    @RequestMapping("/scheduled/bookOnceAll")
    public void bookOnceAll() {
        scheduledLogic.bookAllUsers();
    }
}
