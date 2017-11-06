package com.yuangee.flower.customer.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.MessageAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Message;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/6 0006.
 */

public class MessageActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.message_recycler)
    PullLoadMoreRecyclerView recyclerView;

    @BindView(R.id.empty)
    CustomEmptyView emptyView;

    private MessageAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initRecyclerView();
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("消息列表");
        setSupportActionBar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void initRecyclerView() {
        adapter = new MessageAdapter(this, mRxManager);

        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasMore(false);
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getMessage();
            }

            @Override
            public void onLoadMore() {

            }
        });
        getMessage();
        recyclerView.setRefreshing(true);
    }

    private void getMessage() {
        Observable<List<Message>> observable = ApiManager.getInstance().api
                .findAllMessage(App.getPassengerId())
                .map(new HttpResultFunc<List<Message>>(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, new HaveErrSubscriberListener<List<Message>>() {
            @Override
            public void onNext(List<Message> list) {
                recyclerView.setPullLoadMoreCompleted();
                if (null != list && list.size() != 0) {
                    adapter.setMessages(list);
                    hideEmpty();
                } else {
                    showEmpty();
                    adapter.setMessages(new ArrayList<Message>());
                }
            }

            @Override
            public void onError(int code) {
                recyclerView.setPullLoadMoreCompleted();
                showEmpty();
            }
        })));
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setEmptyText("没有任何消息");
        emptyView.setEmptyImage(R.drawable.ic_filed);
    }

    private void hideEmpty() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }
}
