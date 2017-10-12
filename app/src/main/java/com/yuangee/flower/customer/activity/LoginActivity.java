package com.yuangee.flower.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by developerLzh on 2017/8/21 0021.
 */

public class LoginActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.get_code)
    Button getCode;
    @BindView(R.id.edit_code)
    EditText editCode;
    @BindView(R.id.login_btn)
    Button loginBtn;

    @OnClick(R.id.login_btn)
    void login() {
        App.me().getSharedPreferences().edit().putBoolean("login", true).apply();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

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
}
