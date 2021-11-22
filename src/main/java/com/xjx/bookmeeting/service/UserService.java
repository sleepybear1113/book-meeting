package com.xjx.bookmeeting.service;

import com.xjx.bookmeeting.domain.User;
import com.xjx.bookmeeting.utils.FileUtils;
import com.xjx.bookmeeting.utils.OtherUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/7 19:41
 */
@Service
public class UserService {
    public static final String USER_PATH = "user/";

    public void saveUserInfo(User user) {
        if (user == null) {
            return;
        }
        if (!user.isValid()) {
            return;
        }
        String filePath = getFilePath(user.getUsername(), user.getAuthType());
        User existUser = getUserInfo(user);
        if (existUser != null) {
            OtherUtils.copyNoNullProperties(user, existUser);
            existUser.fillModifyTime();
            FileUtils.writeToFile(existUser, filePath);
        } else {
            user.fillAllTime();
            FileUtils.writeToFile(user, filePath);
        }
    }

    public User getUserInfo(User user) {
        if (user == null || StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getAuthType())) {
            return null;
        }

        String filePath = getFilePath(user.getUsername(), user.getAuthType());
        return FileUtils.readFile(filePath, User.class);
    }

    public boolean deleteLocalUser(User user) {
        if (user == null) {
            return false;
        }

        String filePath = getFilePath(user.getUsername(), user.getAuthType());
        return FileUtils.deleteFile(filePath);
    }

    private String getFilePath(String username, String authType) {
        return USER_PATH + username + "-" + authType + ".json";
    }

    public List<User> getAllUsers() {
        List<String> userFiles = FileUtils.listFilesInDir(USER_PATH);
        ArrayList<User> res = new ArrayList<>();
        if (CollectionUtils.isEmpty(userFiles)) {
            return res;
        }

        for (String userFile : userFiles) {
            User user = FileUtils.readFile(userFile, User.class);
            if (user == null) {
                continue;
            }
            res.add(user);
        }
        return res;
    }
}
