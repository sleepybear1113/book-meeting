package com.xjx.bookmeeting.actions.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/12/25 21:46
 */
@Data
public class SignInRoomResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 413126791224285067L;

    public static final String TRUE_RESULT_TYPE = "true";
    public static final String SIGN_IN_REPEAT = "重复签到";

    @JSONField(name = "result")
    private String result;

    @JSONField(name = "resultType")
    private String resultType;

    public boolean signInSuccess() {
        if (TRUE_RESULT_TYPE.equals(resultType)) {
            return true;
        }

        return StringUtils.isNotBlank(result) && result.contains(SIGN_IN_REPEAT);
    }
}
