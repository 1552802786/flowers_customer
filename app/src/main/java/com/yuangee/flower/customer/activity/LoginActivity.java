package com.yuangee.flower.customer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.PhoneUtil;
import com.yuangee.flower.customer.util.StatusBarUtil;
import com.yuangee.flower.customer.util.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/8/21 0021.
 */

public class LoginActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.edit_phone)
    EditText editPhone;

    @BindView(R.id.edit_code)
    EditText editCode;

    @BindView(R.id.get_code)
    Button getCode;

    @BindView(R.id.logo_img)
    ImageView logoImg;

    @BindView(R.id.login_btn)
    Button loginBtn;

    private int count = 60;

    @OnClick(R.id.get_code)
    void getCode() {
        PhoneUtil.hideKeyboard(this);
        getVerfityCode(editPhone.getText().toString());
    }

    @OnClick(R.id.login_btn)
    void login() {
        login(editPhone.getText().toString(), editCode.getText().toString());
    }

    private Timer timer;
    private TimerTask timerTask;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
//        StatusBarUtil.setTransStatusBar(this);
    }

    @Override
    public void initToolBar() {
        mToolbar.setNavigationIcon(R.drawable.ic_close);
        mToolbar.setTitle("登录");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void login(String phone, String code) {
        Observable<Object> observable = ApiManager.getInstance().api
                .login(phone, code)
                .map(new HttpResultFunc<>(LoginActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(LoginActivity.this, true, false, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                App.me().getSharedPreferences().edit().putBoolean("login", true).apply();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        })));
    }

    private void getVerfityCode(String phone) {
        Observable<Object> observable = ApiManager.getInstance().api
                .sendSmsLogin(phone)
                .map(new HttpResultFunc<>(LoginActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(LoginActivity.this, true, true, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                ToastUtil.showMessage(LoginActivity.this, "获取验证码成功");
                if (timer != null) {
                    timer.cancel();
                }
                if (timerTask != null) {
                    timerTask.cancel();
                }
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        count--;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (count != 0) {
                                    getCode.setEnabled(false);
                                    getCode.setText(count + "s");
                                } else {
                                    count = 60;
                                    timer.cancel();
                                    timerTask.cancel();
                                    getCode.setEnabled(true);
                                    getCode.setText("获取验证码");
                                }
                            }
                        });
                    }
                };
                timer.schedule(timerTask, 0, 1000);
            }
        })));
    }
}
