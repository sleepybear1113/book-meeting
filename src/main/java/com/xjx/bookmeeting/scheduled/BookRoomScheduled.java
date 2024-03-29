package com.xjx.bookmeeting.scheduled;

import com.xjx.bookmeeting.logic.BookMeetingLogic;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/10 1:31
 */
@Configuration
@EnableScheduling
public class BookRoomScheduled {
    @Resource
    private BookMeetingLogic bookMeetingLogic;

    /**
     * 每天 2:00 定时任务
     */
    @Scheduled(cron = "0 0 2 * * ?")
    private void configureTasks() {
        bookMeetingLogic.bookAllUsers();
    }
}
