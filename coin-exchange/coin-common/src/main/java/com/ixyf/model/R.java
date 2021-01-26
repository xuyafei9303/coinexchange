package com.ixyf.model;

import com.ixyf.constant.Constants;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一的返回对象
 */
public class R<T> implements Serializable {

    private static final long serialVersionUID = -3777161004022639733L;

    public static final int SUCCESS = Constants.SUCCESS;

    public static final int FAIL = Constants.FAIL;

    private int code;

    private String msg;

    private T data;

    /**
     * 成功 200
     * @param <T>
     * @return
     */
    public static <T> R<T> ok() {
        return restResult(null, SUCCESS, null);
    }

    /**
     * 成功 200 + data
     * @param data
     * @param <T>
     * @return
     */
    public static <T> R<T> ok(T data) {
        return restResult(data, SUCCESS, null);
    }

    /**
     * 成功 200 + data + msg
     * @param data
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, SUCCESS, msg);
    }

    /**
     * 失败 + 500
     * @param <T>
     * @return
     */
    public static <T> R<T> fail() {
        return restResult(null, FAIL, null);
    }

    /**
     * 失败 500 + msg
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> R<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    /**
     * 失败 500 + data
     * @param data
     * @param <T>
     * @return
     */
    public static <T> R<T> fail(T data) {
        return restResult(data, FAIL, null);
    }

    /**
     * 失败 500 + msg
     * @param code
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> R<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    private static <T> R<T> restResult(T data, int code, String msg) {

        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
