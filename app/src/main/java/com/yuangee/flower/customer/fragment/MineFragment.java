package com.yuangee.flower.customer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.PersonalCenterActivity;
import com.yuangee.flower.customer.base.RxLazyFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by developerLzh on 2017/8/21 0021.
 */

public class MineFragment extends RxLazyFragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tv_av)
    TextView tvAv;

    @BindView(R.id.app_bar_layout)
    AppBarLayout appbarLayout;

    @BindView(R.id.notification_icon)
    ImageView notificationIcon;

    @OnClick(R.id.notification_icon)
    void toMessage() {

    }

    @OnClick(R.id.mine_top)
    void toPersonal() {
        startActivity(new Intent(getActivity(), PersonalCenterActivity.class));
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initView();
        isPrepared = false;
    }

    private void initView() {
//        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
//                if (i == 0) {
//                    //展开状态
//                    tvAv.setTextColor(getResources().getColor(R.color.colorPrimary));
//                    notificationIcon.setImageResource(R.drawable.ic_notifications_primary);
//                } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
//                    //折叠状态
//                    tvAv.setTextColor(getResources().getColor(R.color.white));
//                    notificationIcon.setImageResource(R.drawable.ic_notifications_white);
//                }
//            }
//        });
    }
}
