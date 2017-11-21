package com.yuangee.flower.customer.fragment.reserve;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.GoodsActivity;
import com.yuangee.flower.customer.activity.MainActivity;
import com.yuangee.flower.customer.activity.MessageActivity;
import com.yuangee.flower.customer.adapter.GoodsAdapter;
import com.yuangee.flower.customer.adapter.Type2Adapter;
import com.yuangee.flower.customer.adapter.Type3Adapter;
import com.yuangee.flower.customer.base.RxLazyFragment;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.entity.GenreSub;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.fragment.AddAnimateListener;
import com.yuangee.flower.customer.fragment.BackPressedHandler;
import com.yuangee.flower.customer.fragment.ToSpecifiedFragmentListener;
import com.yuangee.flower.customer.fragment.shopping.ShoppingModel;
import com.yuangee.flower.customer.fragment.shopping.ShoppingPresenter;
import com.yuangee.flower.customer.result.PageResult;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by developerLzh on 2017/8/21 0021.
 */

public class ReserveFragment extends RxLazyFragment implements ReserveContract.View, PullLoadMoreRecyclerView.PullLoadMoreListener, BackPressedHandler {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.menu_icon)
    ImageView menuIcon;

    @BindView(R.id.tv_av)
    TextView tvSearch;

    @BindView(R.id.notification_icon)
    ImageView noIcon;

    @BindView(R.id.good_recycler)
    PullLoadMoreRecyclerView plRecycler;

    @BindView(R.id.empty_layout)
    CustomEmptyView emptyView;

    @BindView(R.id.id_drawerLayout)
    DrawerLayout myDrawerLayout;

    @OnClick(R.id.notification_icon)
    void toMessage() {
        startActivity(new Intent(getActivity(), MessageActivity.class));
    }

    @OnClick(R.id.menu_icon)
    void openDawer() {
        myDrawerLayout.openDrawer(Gravity.LEFT);
        initDrawer();
    }

    @BindView(R.id.type_recycler)
    RecyclerView typeRecycler;

    @BindView(R.id.detail_type_recycler)
    RecyclerView detailRecycler;

    @OnClick(R.id.tv_av)
    void toSearch() {
        if (toSpecifiedFragmentListener != null) {
            toSpecifiedFragmentListener.toFragment(-1);
        }
    }

    private ToSpecifiedFragmentListener toSpecifiedFragmentListener;

    private AddAnimateListener addAnimateListener;

    ReservePresenter presenter;

    GoodsAdapter adapter;
    Type2Adapter typeAdapter;

    Type3Adapter detailTypeAdapter;

    public List<Goods> goodsList;

    private List<Genre> types = new ArrayList<>();
    private List<GenreSub> detailTypes = new ArrayList<>();

    private int page = 0;
    private int limit = 10;

    private String genreName = "";
    private String genreSubName = "";
    private String params = "";//关键字

    public void setToSpecifiedFragmentListener(ToSpecifiedFragmentListener toSpecifiedFragmentListener) {
        this.toSpecifiedFragmentListener = toSpecifiedFragmentListener;
    }

    public void setAddAnimateListener(AddAnimateListener addAnimateListener) {
        this.addAnimateListener = addAnimateListener;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_shopping;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
        presenter = new ReservePresenter(getActivity());
        presenter.setMV(new ReserveModel(getActivity()), this);
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initRecyclerView();
        isPrepared = false;
    }

    private void initDrawer() {

        typeAdapter = new Type2Adapter(getActivity());
        typeRecycler.setAdapter(typeAdapter);
        LinearLayoutManager linManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        typeRecycler.setLayoutManager(linManager);
        typeAdapter.setOnItemClickListener(new Type2Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                createDetailType(position);
                detailTypeAdapter.setData(detailTypes);
                if (types.get(position).clicked) {
                    genreName = types.get(position).genreName;
                } else {
                    genreName = "";
                }
            }
        });
        createType();
        typeAdapter.setData(types);


        detailTypeAdapter = new Type3Adapter(getActivity());
        detailRecycler.setAdapter(detailTypeAdapter);
        GridLayoutManager gridManager = new GridLayoutManager(getActivity(), 3);
        detailRecycler.setLayoutManager(gridManager);
        detailTypeAdapter.setOnItemClickListener(new Type3Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                myDrawerLayout.closeDrawer(Gravity.LEFT);
                if (detailTypes.get(position).clicked) {
                    genreSubName = detailTypes.get(position).name;
                } else {
                    genreSubName = "";
                }
            }
        });
        myDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                page = 0;
                presenter.getGoodsData(genreName, genreSubName, params, page, limit);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        detailTypeAdapter.setData(detailTypes);
    }

    private void createType() {
        types.clear();
        for (Genre genre : MainActivity.genreList) {
            types.add(genre);
        }
    }

    private void createDetailType(int position) {
        detailTypes.clear();
        if (MainActivity.genreList.size() > 0) {
            detailTypes.addAll(MainActivity.genreList.get(position).genreSubs);
        }
    }

    @Override
    protected void initRecyclerView() {

        plRecycler.setFooterViewText("加载中..");

        adapter = new GoodsAdapter(getActivity(), 1, mRxManager);
        adapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), GoodsActivity.class);
                intent.putExtra("change",false);
                intent.putExtra("goods",adapter.getData().get(position));
                startActivity(intent);
            }
        });
        adapter.setmOnAddClickListener(new GoodsAdapter.OnAddClickListener() {
            @Override
            public void onAddClick(ImageView view, int selectedNum) {
                if (null != addAnimateListener) {
                    addAnimateListener.showAddAnimate(view, selectedNum);
                }
                //添加完一项后重新获取数据
//                presenter.getGoodsData(genreName,genreSubName,params,page,limit);
            }
        });


        plRecycler.setAdapter(adapter);
        plRecycler.getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        goodsList = new ArrayList<>();

        adapter.setData(goodsList);

        plRecycler.setOnPullLoadMoreListener(this);

        plRecycler.setRefreshing(true);

        plRecycler.setVerticalScrollBarEnabled(true);

        presenter.getGoodsData(genreName, genreSubName, params, page, limit);

    }

    @Override
    public void onDetach() {
        presenter.onDestroy();
        super.onDetach();
    }

    @Override
    public void showGoods(int page, int limit, PageResult<Goods> pageResult) {
        if (page == 0) {
            goodsList.clear();
        }
        goodsList.addAll(pageResult.rows);
        for (Goods goods : goodsList) {
            goods.selectedNum = 1;//默认选中的个数为1
        }
        if (goodsList.size() == 0) {
            showEmptyView(0);
        } else {
            hideEmptyView();
        }
        plRecycler.setVisibility(View.VISIBLE);
        plRecycler.setRefreshing(false);
        plRecycler.setPullLoadMoreCompleted();
        if (pageResult.total > (page + 1) * limit) {
            plRecycler.setHasMore(true);
        } else {
            plRecycler.setHasMore(false);
        }
        adapter.setData(goodsList);
    }

    @Override
    public void showEmptyView(int tag) {
        plRecycler.setRefreshing(false);
        plRecycler.setVisibility(View.GONE);
        plRecycler.setPullLoadMoreCompleted();
        emptyView.setVisibility(View.VISIBLE);
        if (tag == 0) {
            emptyView.setEmptyImage(R.drawable.ic_filed);
            emptyView.setEmptyText("没能获取到任何数据");
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page = 0;
                    presenter.getGoodsData(genreName, genreSubName, params, page, limit);
                }
            });
        } else {
            emptyView.setEmptyImage(R.drawable.ic_filed);
            emptyView.setEmptyText("貌似出了点问题，\n点我重试");
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page = 0;
                    presenter.getGoodsData(genreName, genreSubName, params, page, limit);
                }
            });
        }
    }

    @Override
    public void hideEmptyView() {
        plRecycler.setRefreshing(false);
        plRecycler.setVisibility(View.VISIBLE);
        plRecycler.setPullLoadMoreCompleted();
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        page = 0;
        goodsList.clear();
        plRecycler.setRefreshing(true);
        presenter.getGoodsData(genreName, genreSubName, params, page, limit);
    }

    @Override
    public void onLoadMore() {
        page++;
        presenter.getGoodsData(genreName, genreSubName, params, page, limit);
    }

    @Override
    public boolean onBackPressed() {
        if (myDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            myDrawerLayout.closeDrawer(Gravity.LEFT);
            return true;
        } else {
            return false;
        }
    }

    public void findWares(String params) {
        page = 0;
        presenter.getGoodsData(genreName, genreSubName, params, page, limit);
    }

}
