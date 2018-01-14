package com.yuangee.flower.customer.network;

import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.LoginActivity;
import com.yuangee.flower.customer.util.AppManager;
import com.yuangee.flower.customer.util.ToastUtil;

import org.json.JSONException;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/9/26.
 */

public class MySubscriber<T> extends Subscriber<T> implements ProgressDismissListener {

    private Context context;

    private ProgressHandler progressHandler;

    private NoErrSubscriberListener<T> noErrSubscriberListener;
    private HaveErrSubscriberListener<T> haveErrSubscriberListener;

    /**
     * @param needShowProgress        是否显示加载框
     * @param dialogCancelable        加载框是否可以取消
     * @param noErrSubscriberListener 不需要错误码的回调事件
     */
    public MySubscriber(
            Context context,
            boolean needShowProgress,
            boolean dialogCancelable,
            NoErrSubscriberListener<T> noErrSubscriberListener) {
        this.context = context;
        this.noErrSubscriberListener = noErrSubscriberListener;
        if (needShowProgress) {
            progressHandler = new ProgressHandler(context, dialogCancelable, this);
        }
    }

    /**
     * @param needShowProgress          是否显示加载框
     * @param dialogCancelable          加载框是否可以取消
     * @param haveErrSubscriberListener 需要错误码回调的事件
     */
    public MySubscriber(
            Context context,
            boolean needShowProgress,
            boolean dialogCancelable,
            HaveErrSubscriberListener<T> haveErrSubscriberListener) {
        this.context = context;
        this.haveErrSubscriberListener = haveErrSubscriberListener;
        if (needShowProgress) {
            progressHandler = new ProgressHandler(context, dialogCancelable, this);
        }
    }

    @Override
    public void onCompleted() {
        Log.e("MySubscriber", "mission complete");

        if (null != progressHandler) {
            progressHandler.sendEmptyMessage(ProgressHandler.DISMISS_DIALOG);
        } else {
            this.onProgressDismiss();
        }

    }

    /**
     * 处理错误信息
     */
    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException) {
            ToastUtil.showMessage(context, context.getString(R.string.response_error) + ((HttpException) e).code());//400、500、404之类的响应码错误
            if (null != haveErrSubscriberListener) {
                haveErrSubscriberListener.onError(-100);
            }
        } else if (e instanceof SocketTimeoutException || e instanceof SocketException) {
            ToastUtil.showMessage(context, context.getString(R.string.out_time));//连接超时错误
            if (null != haveErrSubscriberListener) {
                haveErrSubscriberListener.onError(-100);
            }
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            ToastUtil.showMessage(context, context.getString(R.string.parse_error));//解析错误
            if (null != haveErrSubscriberListener) {
                haveErrSubscriberListener.onError(-100);
            }
        } else if (e instanceof ApiException) {
            ToastUtil.showMessage(context, e.getMessage());//服务器定义的错误

            if(e.getMessage().contains("token")){
                AppManager.getAppManager().finishAllActivity();
                App.me().getSharedPreferences().edit().clear().apply();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }

            if (null != haveErrSubscriberListener) {
                haveErrSubscriberListener.onError(((ApiException) e).getErrCode());
            }
        }

        if (null != progressHandler) {
            progressHandler.sendEmptyMessage(ProgressHandler.DISMISS_DIALOG);
        } else {
            this.onProgressDismiss();
        }

    }

    @Override
    public void onNext(T t) {
        if (haveErrSubscriberListener != null) {
            haveErrSubscriberListener.onNext(t);
        }
        if (noErrSubscriberListener != null) {
            noErrSubscriberListener.onNext(t);
        }
    }

    /**
     * 在开始订阅时，如果需要显示加载框
     */
    @Override
    public void onStart() {
        super.onStart();
        if (null != progressHandler) {
            progressHandler.sendEmptyMessage(ProgressHandler.SHOW_DIALOG);
        }
    }

    /**
     * 在加载框消失时是整个流程的最后一步
     * 好像可以防止内存泄露
     */
    @Override
    public void onProgressDismiss() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}
