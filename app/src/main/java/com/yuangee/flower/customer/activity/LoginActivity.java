package com.yuangee.flower.customer.activity;


import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.alipay.sdk.app.AuthTask;
import com.shawnlin.preferencesmanager.PreferencesManager;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.PersonUtil;
import com.yuangee.flower.customer.util.PhoneUtil;
import com.yuangee.flower.customer.util.SignUtils;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/8/21 0021.
 */

public class LoginActivity extends RxBaseActivity {
    private String alipay_app_id = "2017110709782860";
    private String private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCuPhz/ZvvwdMCHoZPxd/SMd085zP9J+rMiMKnCikebm7y3Q0dDNsYHXNRkyaB2y2f+8uFh51ZikxFoh3svwhBxdL4rPPAwDDunq0m2GPBS0wT7t88z/gK+5IU4iWyADeXoa/3w8RzKCNA/fLU/u56BwsxnTY0j/Wdz7I/qU1K8ZB9GdAyrGu0QQ8wOuT3mD/gysJmJd0yBiOErRA9oUXC+mK+6VOXNv+fFnb8VEON0+MPw2nEqb4UdklDV7o6Pg8/idwm0XJ8GFTzx74qkH5dsHa0+CTNoBvyDynilzxolv3OUQv3j/owxima364v6XnDyB+bLhEIdIPLElK54K+lXAgMBAAECggEABekHdMZaf2KGcVNSwFsQSX92iYBhnHHLh7pc18N3AFk9An5euXHvL6q/bZfFQKJPqb81U+vT604PxhrqW8laY27RUIgloYuYrmxJ+MpJxJVx0tP+TEYuc6kut6c5rfJKvSKEn5qeHmBEKBMj1sCXdNFVu59OzUy4KJ96ljkzUj6I2JoUoqPXoPysrWBnX1Z4QyP3Q5tUmkHmYyfkwDraNed8FRo9YfsPudbgciBhE00qGd+xfE+grl2LjdptnHmxRbu4NGHEk8dcL4/fyNon68cdrbldN+RKPQ6M5EHQhGKB6vG0i4N321UJSMDEuY6vJjYr8iyXqDlpRMEjIzcxaQKBgQDWmWCHg1oD7aoyODRVNFK4ePgeFe3n51Q0B8kVTk5mKUa0f7F3KmiIowZOILsuSfqUnL+yiiHbsyFmFVzX/J2GlLbntZXIrtvXYTTbfFRzeyvUKxsToZarlvF6KrgvP39euMHTPjx7AMNuWBimrbl+Zvnv0zJbcI9Y4YI8peOeLQKBgQDP25v1Ae0Z3guIU+szHgHuH0Pk3yR9LchaGh6+qjzNADk5O4XHvwsrcJm2Kj/rVGw7L4Hi5qckSr8BKZjMshKm4SfmGbospcU0ZrBlI6eVCR++fZApEtiiw4eVBEdPhqo5T6ECbnrbFqX2q9K5u0QESi7EYARNiGaW/Sqj/YtcEwKBgGF6T/+eKaBJL5saLqNZXg3PXR0FZwiE6pmrw6o65J/BNg29RxZTHCKcsruAYKX5Eqq4vNbTqeeK6aveHks5wzAjkyWTNRNwYgnmbMUaJ55BH7qDTgxPxQnySYPtrbZAiJG1KM3UolJZGWyg9amYlf6Vtgg7Dc+9RE/iN29nBwqNAoGADiOYJkBtSncqPanHtAB0jSgfTDBJoSI+ILLqKHoYDLHZMqVz4jSCo//S1Otm0bE6c4Q1x6N89yEGgSO9Ox9Z2XQzbJmPan4UPg5GLXsRaTfnquOLrN+VUO0QCfjyiNxWM/PQtgOh48lUnMTvXqb4Z3pMWBtX75Y5tEegYOtcUXUCgYEAzJ/ccQQbQs9Z9OH9zyimoqszr9F1We0D/HkyBLi2yyMCW4Cvn6hr2yy4JbN5y+Ln1LLDeVYEImTD+VGM2V4PmUDg0hjIVwihXVoRU0I0jaaYyLZZ6EVxDB6ZLRjYsA89T7KmjI+TtHL2q1TsQaIWDHcXK2X5UNM7H+US6LR9JIQ";
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

    @OnClick(R.id.alipay_login)
    void alipayLoginMethod() {
        alipayLogin();
    }

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
        wx_api = WXAPIFactory.createWXAPI(this, Constants.WX_CUSTOMER_APP_ID);
        wx_api.registerApp(Constants.WX_CUSTOMER_APP_ID);
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

        } else {
            wehcatLogin(refresh_token);
        }
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    private void wehcatLogin(String refresh_token) {
        JSONObject object = new JSONObject();
        try {
            object.put("appid", Constants.WX_CUSTOMER_APP_ID);
            object.put("refresh_token", refresh_token);
            object.put("grant_type", "refresh_token");
            String str = post("https://api.weixin.qq.com/sns/oauth2/refresh_token", object.toString());
            JSONObject obj = new JSONObject(str);
            //是否超时
            if (object.has("errcode") && object.getString("errcode").equalsIgnoreCase("42002")) {
                //发起登录请求
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                wx_api.sendReq(req);
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        requetsInfo += "&pid=" + "\"" + Constants.PARTNER + "\"";
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
