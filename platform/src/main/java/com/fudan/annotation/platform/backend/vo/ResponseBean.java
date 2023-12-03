package com.fudan.annotation.platform.backend.vo;

import com.fudan.annotation.platform.backend.enums.ResponseCodeMsg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.fudan.annotation.platform.backend.enums.ResponseCodeMsg.ERROR;
import static com.fudan.annotation.platform.backend.enums.ResponseCodeMsg.SUCCESS;

/**
 * description: responsebean
 *
 * @author Richy create: 2021-12-10 16:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBean<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public static <T> ResponseBean<T> success() {
        return new ResponseBean<>(SUCCESS.getCode(), SUCCESS.getMsg(), null);
    }

    public static <T> ResponseBean<T> success(T data) {
        return new ResponseBean<>(SUCCESS.getCode(), SUCCESS.getMsg(), data);
    }

    public static <T> ResponseBean<T> success(Integer code, String msg, T data) {
        return new ResponseBean<>(code, msg, data);
    }

    public static <T> ResponseBean<T> success(ResponseCodeMsg codeMsg, T data) {
        return new ResponseBean<>(codeMsg.getCode(), codeMsg.getMsg(), data);
    }

    public static <T> ResponseBean<T> error() {
        return new ResponseBean<>(ERROR.getCode(), ERROR.getMsg(), null);
    }

    public static <T> ResponseBean<T> error(ResponseCodeMsg codeMsg) {
        return new ResponseBean<>(codeMsg.getCode(), codeMsg.getMsg(), null);
    }
}
