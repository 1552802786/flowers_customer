package com.yuangee.flower.customer.result;

/**
 * Created by developerLzh on 2017/9/4 0004.
 */

public class BaseResult<T> {
    private boolean success; //True成功，false失败

    private String message; //返回信息

    private T data; //成功返回用户信息，不成功不返回

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return message;
    }

    public T getData() {
        return data;
    }
}
