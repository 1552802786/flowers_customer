package com.yuangee.flower.customer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.alipay.sdk.app.AuthTask;
import com.google.gson.Gson;
import com.shawnlin.preferencesmanager.PreferencesManager;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.Address;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.PersonUtil;
import com.yuangee.flower.customer.util.PhoneUtil;
import com.yuangee.flower.customer.util.SignUtils;
import com.yuangee.flower.customer.util.StatusBarUtil;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/8/21 0021.
 */

public class LoginActivity extends RxBaseActivity {
    private String alipay_app_id = "2017090808616256";
    private String private_key = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMPJkfTSl6ypBowGP2RsgOm+nz988ljTU5z35JBnwPSwobJ/rN3RC/s9JKjQpUk41p71t+qlOpMlmpHwU1O6etXRe8cHicX4PZGWIJzqnF1o3n7tBAOccPmSiK/kZdgp/Yg5utPvNuYm4o1mz/ARVnOLtcakSLSpcNG/aeEflmJ7AgMBAAECgYBAWKgaoXft7CNVs9vzwmFf8SFfeU4g+VtalHJPL3pQMRkDlEiZIlJgwQiiXuhjt0V6OuG2QZWNtOcnHagVNY2W0xVkBKevzGWoLTbAPHh6CGU5vauxiFygcCC1zKnOsYga+RMXS469LJ8oNu/hqNoBxU2/njzafBMMYYiFpOobEQJBAOnGCHDEmByrOutHXouN5vSxIL72SJrhQRhnQc/DGG4Ni3fdwMZnMK20CLbtYFLje5wje3OVrxsPxaT8CvCpVjMCQQDWZvd74IWnfwehge2SVWaZphw3kZW7lw5pW8bA1hoDALRBDHUloCufPCEJ4VeTnjZOiqoJOOP0+sSR4/nATKqZAkEAi7T7yljTBx8VwRIP4JrXUZihlz4cOeMwQeNDo2RWrz6NAP+Xe3qjzstvAdNu41prvu49kt/7m9KbLXQrHZQ1nQJBAIm2Y3pHEbIvTsh3exBGGHvSjUdIFMQEV7Zmw7fzDYwmNKGfjdNYGQzTg2kkO6tOsRUrzeHUj983/3Cx3SaeV+kCQQCTdlTMrtA4CWy7Y7padGOfORFJC7BlskupXCCEfuB5894thlQgo509zJ9jJ6dw+F9diN6CRN4w+KGh1fpLjINC";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.edit_phone)
    EditText editPhone;

    @BindView(R.id.edit_code)
    EditText editCode;

    @BindView(R.id.get_sms_code)
    Button getCode;

    @BindView(R.id.logo_img)
    ImageView logoImg;

    @BindView(R.id.login_btn)
    Button loginBtn;

    @BindView(R.id.agree_checkbox)
    CheckBox agreeCheckbox;

    private int count = 60;
    private IWXAPI wx_api;

    @OnClick(R.id.get_sms_code)
    void getCode() {
        PhoneUtil.hideKeyboard(this);
        getVerfityCode(editPhone.getText().toString());
    }

    @OnClick(R.id.login_btn)
    void login() {
        if (StringUtils.isBlank(editPhone.getText().toString())) {
            ToastUtil.showMessage(LoginActivity.this, "请输入电话号码");
            return;
        }
        if (StringUtils.isBlank(editCode.getText().toString())) {
            ToastUtil.showMessage(LoginActivity.this, "请输入验证码");
            return;
        }
        if (!agreeCheckbox.isChecked()) {
            ToastUtil.showMessage(LoginActivity.this, "请同意用户协议");
            return;
        }
        login(editPhone.getText().toString(), editCode.getText().toString());
    }

    private Timer timer;
    private TimerTask timerTask;
    private final int SDK_AUTH_FLAG = 10005;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case SDK_AUTH_FLAG:
                    Log.e("alipay_code", (String) message.obj);
                    String[] resuls = ((String) message.obj).split("&");
                    for (int i = 0; i < resuls.length; i++) {
                        if (resuls[i].contains("auth_code")) {
                            String auth_code = resuls[i].substring(11, resuls[i].length() - 1);
                            break;
                        }
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
//        StatusBarUtil.setTransStatusBar(this);
//        wx_api = WXAPIFactory.createWXAPI(this, Constants.WX_CUSTOMER_APP_ID);
//        wx_api.registerApp(Constants.WX_CUSTOMER_APP_ID);
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
        Observable<Member> observable = ApiManager.getInstance().api
                .login(phone, code, JPushInterface.getRegistrationID(LoginActivity.this))
                .map(new HttpResultFunc<Member>(LoginActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(LoginActivity.this, true, false, new NoErrSubscriberListener<Member>() {
            @Override
            public void onNext(Member o) {
                SharedPreferences.Editor editor = App.me().getSharedPreferences().edit();

                editor.putLong("id", o.id);
                if (null != o.memberToken) {
                    editor.putLong("deathDate", o.memberToken.deathDate);
                    editor.putString("token", o.memberToken.token);
                }

                editor.putBoolean("login", true);

                editor.apply();

                ApiManager.getInstance().addHeader();//添加统一请求头

                PersonUtil.getMemberInfo(mRxManager, LoginActivity.this, App.getPassengerId(), new PersonUtil.OnGetMember() {
                    @Override
                    public void onSuccess(Member member) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finishActivity();
                    }

                    @Override
                    public void onFailed() {
                        ToastUtil.showMessage(LoginActivity.this, "获取会员信息失败");
                    }
                });
            }
        })));
    }

    private void finishActivity() {
        if (null != timer) {
            timer.cancel();
        }
        if (null != timerTask) {
            timerTask.cancel();
        }
        finish();
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
                                    if (getCode != null) {
                                        getCode.setEnabled(false);
                                        getCode.setText(count + "s");
                                    }
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

    private void weixinLoginProcess() {
        final String refresh_token = PreferencesManager.getString("refresh_token", null);
        if (TextUtils.isEmpty(refresh_token)) {
            //发起登录请求
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test";
            wx_api.sendReq(req);
            IntentFilter filer = new IntentFilter("weChat_login");
//            LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    wehcatLogin(intent.getStringExtra("token"));
//                }
//            }, filer);
        } else {
//            wehcatLogin(refresh_token);
        }
    }

//    private void wehcatLogin(String refresh_token) {
//        RequestParams params = new RequestParams("https://api.weixin.qq.com/sns/oauth2/refresh_token");
//        params.addBodyParameter("appid", Constants.WX_CUSTOMER_APP_ID);
//        params.addBodyParameter("refresh_token", refresh_token);
//        params.addBodyParameter("grant_type", "refresh_token");
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                com.wn.wnbase.application.Log.e("weixin", result);
//                JSONObject object = null;
//                try {
//                    object = new JSONObject(result);
//                    //是否超时
//                    if (object.has("errcode") && object.getString("errcode").equalsIgnoreCase("42002")) {
//                        //发起登录请求
//                        final SendAuth.Req req = new SendAuth.Req();
//                        req.scope = "snsapi_userinfo";
//                        req.state = "wechat_sdk_demo_test";
//                        wx_api.sendReq(req);
//                    } else {
//                        mUserAccountManager.doWeChatLogin(object.getString("access_token"), object.getString("openid")
//                                , new WeakReference<HttpFeedManager.HttpManagerListener>(LoginActivity.this));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
//    }

    public String sign(String content) {
        return SignUtils.sign(content, private_key);
    }

    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

    private String getSignString() {
        String requetsInfo = "apiname=" + "\"com.alipay.account.auth\"";
        requetsInfo += "&method=" + "\"alipay.open.auth.sdk.code.get\"";
        requetsInfo += "&app_id=" + "\"" + alipay_app_id + "\"";
        requetsInfo += "&app_name=" + "\"mc\"";
        requetsInfo += "&biz_type=" + "\"openservice\"";
//        requetsInfo += "&pid=" + "\"" + Constants.PARTNER + "\"";
        requetsInfo += "&product_id=" + "\"APP_FAST_LOGIN\"";
        requetsInfo += "&scope=" + "\"kuaijie\"";
        requetsInfo += "&target_id=" + "\"kkkkk091129\"";
        requetsInfo += "&auth_type=" + "\"AUTHACCOUNT\"";
        return requetsInfo;
    }

    private void alipayLogin() {
        // 对订单做RSA 签名
        String sign = sign(getSignString());
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 完整的符合支付宝参数规范的订单信息
        final String authInfo = getSignString() + "&sign=\"" + sign + "\"&" + getSignType();
        Log.e("alipay", authInfo);
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                AuthTask alipay = new AuthTask(LoginActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.auth(authInfo, true);
                Message msg = new Message();
                msg.what = SDK_AUTH_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}
