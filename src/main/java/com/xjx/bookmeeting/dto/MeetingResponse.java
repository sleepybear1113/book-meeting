package com.xjx.bookmeeting.dto;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.xjx.bookmeeting.exception.FrontException;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * @author XJX
 */
@Data
@Slf4j
@ToString
public class MeetingResponse<T> implements Serializable {
    private static final long serialVersionUID = 6058753305258783016L;

    public static final String TRUE_RESULT_TYPE = "true";

    private static final String LOGIN_FAILED = "登陆超时";

    @JSONField(name = "result")
    private List<T> result;

    @JSONField(name = "resultType")
    private String resultType;

    @JSONField(name = "error")
    private String error;

    @JSONField(name = "fromUrl")
    private String fromUrl;

    @JSONField(name = "status")
    private Integer status;

    @JSONField(name = "totalPage")
    private Integer totalPage;

    @JSONField(name = "totalPage")
    private Integer totalResults;

    public static <T> boolean isSuccess(MeetingResponse<T> meetingResponse) {
        if (meetingResponse == null) {
            log.warn("MeetingResponse is null");
            return false;
        }
        String error = meetingResponse.error;
        boolean b = error == null || meetingResponse.getStatus() == null;
        if (!b) {
            log.warn("MeetingResponse is not success, {}", meetingResponse);
            if (error.contains(LOGIN_FAILED)) {
                FrontException.throwCommonFrontException(LOGIN_FAILED);
            }
        }
        return b;
    }

    public static <T> TypeReference<MeetingResponse<T>> getObjectTypeReference() {
        return new TypeReference<MeetingResponse<T>>() {
        };
    }
}