package com.yuangee.flower.customer.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;

import butterknife.BindView;

/**
 * 协议界面
 * Created by admin on 2018/1/22.
 */

public class CustomerAgreementActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.web_string_text)
    WebView webView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_agreement;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        String html = getIntent().getStringExtra("web_string");
        webView.getSettings().setDefaultTextEncodingName("UTF -8");

        webView.loadData(html, "text/html; charset=UTF-8", null);
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(getIntent().getStringExtra("title_agreement"));
        setSupportActionBar(toolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
