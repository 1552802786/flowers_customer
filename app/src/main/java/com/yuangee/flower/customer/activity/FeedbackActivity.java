package com.yuangee.flower.customer.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liuzihao on 2017/12/5.
 */

public class FeedbackActivity extends RxBaseActivity {
    @BindView(R.id.edit_text)
    EditText editText;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void initToolBar() {
        toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("意见反馈");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void send(View view){
        String text = editText.getText().toString();
        if(StringUtils.isNotBlank(text)){
            Member member = DbHelper.getInstance().getMemberLongDBManager().load(App.getPassengerId());
            Observable<Object> observable = ApiManager.getInstance().api.feedback(member.name,member.phone,text)
                    .map(new HttpResultFunc<>(this))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            mRxManager.add(observable.subscribe(new MySubscriber<Object>(this, true,
                    true, new NoErrSubscriberListener<Object>() {
                @Override
                public void onNext(Object o) {
                    ToastUtil.showMessage(FeedbackActivity.this,"感谢您的宝贵意见");
                    finish();
                }
            })));
        } else {
            ToastUtil.showMessage(this,"您什么都没有输入");
        }
    }
}
