package com.xjx.bookmeeting.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xjx.bookmeeting.dto.BookMeetingInfoDto;
import com.xjx.bookmeeting.entity.BookMeetingInfo;
import com.xjx.bookmeeting.mapper.BookMeetingInfoMapper;
import com.xjx.bookmeeting.utils.OtherUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2022/1/30 11:53
 */
@Service
public class BookMeetingInfoService {
    @Resource
    private BookMeetingInfoMapper bookMeetingInfoMapper;

    public List<BookMeetingInfoDto> getUserBookMeetings(Integer userId) {
        BookMeetingInfo bookMeetingInfo = new BookMeetingInfo();
        bookMeetingInfo.setUserId(userId);
        List<BookMeetingInfo> meetingInfoList = bookMeetingInfoMapper.selectList(new QueryWrapper<>(bookMeetingInfo));
        if (CollectionUtils.isEmpty(meetingInfoList)) {
            return new ArrayList<>();
        }

        List<BookMeetingInfoDto> res = new ArrayList<>();
        for (BookMeetingInfo meetingInfo : meetingInfoList) {
            res.add(OtherUtils.convert(meetingInfo, BookMeetingInfoDto.class));
        }
        return res;
    }

    public void addBookInfos(Integer userId, List<BookMeetingInfoDto> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(m -> m.setUserId(userId));

        for (BookMeetingInfoDto bookMeetingInfoDto : list) {
            BookMeetingInfo insert = OtherUtils.convert(bookMeetingInfoDto, BookMeetingInfo.class);
            insert.fillAllTime();
            bookMeetingInfoMapper.insert(insert);
        }
    }

    public void deleteBookInfos(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        bookMeetingInfoMapper.deleteBatchIds(ids);
    }
}
