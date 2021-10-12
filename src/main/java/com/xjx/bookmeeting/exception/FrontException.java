package com.xjx.bookmeeting.exception;

import com.xjx.bookmeeting.utils.returns.ResultCodeConstant;

import java.io.Serializable;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/9/3 9:40
 */
public class FrontException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 2332850776216529844L;

    private Integer code;

    public FrontException(String message) {
        super(message);
        code = ResultCodeConstant.CodeEnum.COMMON_ERROR.getCode();
    }

    public FrontException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static void throwCommonFrontException(String message) {
        throw new FrontException(message);
    }

    public static FrontException loginFail() {
        return new FrontException(ResultCodeConstant.CodeEnum.LOGIN_FAIL.getCode(), "登录失效");
    }
}
