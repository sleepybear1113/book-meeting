package com.xjx.bookmeeting.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xjx.bookmeeting.mapper.UserMapper;
import com.xjx.bookmeeting.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2022/1/29 21:46
 */
@RestController
@Slf4j
public class TestController {

    @Resource
    private UserMapper userMapper;

    @RequestMapping("testDb")
    public Boolean testDb() {
        User user = userMapper.selectById(1);
        log.info(user.toString());
        return true;
    }

    @RequestMapping("testDb2")
    public Boolean testDb2() {
        User entity = new User();
        entity.setUsername("t");
        entity.setPassword("q");
        entity.setAuthType("1");
        entity.setLoginIdWeaver("2");
        entity.fillAllTime();
        int insert = userMapper.insert(entity);
        System.out.println(insert);
        return true;
    }

    @RequestMapping("test1")
    public void test1() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        User entity = new User();
        entity.setUsername("t");
        entity.setAuthType("1");
        queryWrapper.setEntity(entity);
        List<User> users = userMapper.selectList(queryWrapper);
        log.info(String.valueOf(users));
    }
}
