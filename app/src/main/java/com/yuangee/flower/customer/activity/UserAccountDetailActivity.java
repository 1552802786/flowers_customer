package com.yuangee.flower.customer.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;

import butterknife.BindView;

/**
 * 作者：Rookie on 2018/9/19 11:06
 */
public class UserAccountDetailActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public int getLayoutId() {
        return R.layout.lactivity_user_account_detai;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void initToolBar() {
        mToolbar.setNavigationIcon(R.drawable.ic_close);
        mToolbar.setTitle("账户明细");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
