package com.yuangee.flower.customer.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.UserScoreEntity;
import com.yuangee.flower.customer.util.JsonUtil;
import com.yuangee.flower.customer.widget.SwipeRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import butterknife.BindView;

/**
 * 作者：Rookie on 2018/9/19 11:06
 */
public class UserAccountDetailActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.account_detail_list)
    SwipeRecyclerView recyclerView;
    @Override
    public int getLayoutId() {
        return R.layout.lactivity_user_account_detai;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);

    }
    private void queryScore() {
        String url = Config.BASE_URL + "rest/member/listMemberIntegral";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("memberId", App.getPassengerId() + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject object = JSONObject.parseObject(result);
                List<UserScoreEntity> entities = JsonUtil.jsonToArray(object.getString("data"), UserScoreEntity[].class);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

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
