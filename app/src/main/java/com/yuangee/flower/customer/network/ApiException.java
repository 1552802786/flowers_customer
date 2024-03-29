package com.yuangee.flower.customer.network;

import android.content.Context;

/**
 * Created by Administrator on 2016/9/26.
 */
public class ApiException extends RuntimeException {

    private int code = -100;

    public ApiException(Context context, int resultCode) {
        this(getApiExceptionMessage(context, resultCode));
        this.code = resultCode;
    }


    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     */
    private static String getApiExceptionMessage(Context context, int code) {
        return "网络请求错误：code(" + code + ")";
    }

    /**
     * 服务器自定义错误码
     */
    public int getErrCode() {
        return code;
    }
}
