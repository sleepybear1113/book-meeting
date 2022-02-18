package com.xjx.bookmeeting.bo;

import com.xjx.bookmeeting.exception.FrontException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2022/2/18 9:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinPeople implements Serializable {
    @Serial
    private static final long serialVersionUID = -6369149173409720023L;

    private String name;
    private Integer userId;

    public static List<JoinPeople> parse(String peopleInfoListString) {
        if (StringUtils.isBlank(peopleInfoListString)) {
            return new ArrayList<>();
        }

        List<JoinPeople> list = new ArrayList<>();
        String[] peopleInfoList = peopleInfoListString.split(",");
        for (String peopleInfo : peopleInfoList) {
            String[] p = peopleInfo.split("@");
            if (p.length < 2) {
                throw new FrontException("与会人员格式错误");
            }

            String name = p[0];
            String uid = p[1];
            if (!StringUtils.isNumeric(uid)) {
                throw new FrontException("与会人员格式错误，userId 需要为数字");
            }
            list.add(new JoinPeople(name, Integer.valueOf(uid)));
        }
        return list;
    }

    public static String listToString(List<JoinPeople> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        StringBuilder res = new StringBuilder();
        for (JoinPeople joinPeople : list) {
            res.append(joinPeople.toString()).append(",");
        }
        return res.toString();
    }

    @Override
    public String toString() {
        return name + "@" + userId;
    }
}
