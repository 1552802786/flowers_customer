package com.yuangee.flower.customer.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.entity.Setting;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.permission.RxPermissions;
import com.yuangee.flower.customer.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/4/19.
 */

public class SplashActivity extends RxBaseActivity {

    RxPermissions rxPermissions;

    @BindView(R.id.root_view)
    LinearLayout rootView;

    @Override
    public int getLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_splash;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);

        rxPermissions = new RxPermissions(this);

        if (!rxPermissions.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
                || !rxPermissions.isGranted(Manifest.permission.READ_PHONE_STATE)) {
            showDialog();
        } else {
            delayToLogin();
        }
    }

    private void delayToLogin() {
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        if (!App.me().getSharedPreferences().getBoolean("login", false)) {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        }
                        finish();
                        break;
                }
                return false;
            }
        });
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    private void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("为了能够正常的使用APP，请授予以下权限")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPer();
                    }
                })
                .setCancelable(false)
                .create();
        dialog.show();
    }

    private void requestPer() {
        rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            Snackbar.make(rootView, "授权成功", Snackbar.LENGTH_SHORT).show();
                            delayToLogin();
                        } else {
                            Snackbar.make(rootView, "授权失败", Snackbar.LENGTH_SHORT).show();
                            showDialog();
                        }
                    }
                });
    }

    private void getSetting() {
        Observable<Setting> observable = ApiManager.getInstance().api.getSetting()
                .map(new HttpResultFunc<Setting>(SplashActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false,
                false, new HaveErrSubscriberListener<Setting>() {
            @Override
            public void onNext(Setting setting) {
                delayToLogin();
            }

            @Override
            public void onError(int code) {
                ToastUtil.showMessage(SplashActivity.this,"配置信息获取失败");
                delayToLogin();
            }
        })));
    }
}
