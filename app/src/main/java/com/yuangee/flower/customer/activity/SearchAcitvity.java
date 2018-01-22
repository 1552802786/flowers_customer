package com.yuangee.flower.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.GoodsAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.fragment.AddAnimateListener;
import com.yuangee.flower.customer.fragment.BackPressedHandler;
import com.yuangee.flower.customer.fragment.shopping.ShoppingContract;
import com.yuangee.flower.customer.fragment.shopping.ShoppingModel;
import com.yuangee.flower.customer.fragment.shopping.ShoppingPresenter;
import com.yuangee.flower.customer.result.PageResult;
import com.yuangee.flower.customer.util.DisplayUtil;
import com.yuangee.flower.customer.util.PhoneUtil;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.AddCartAnimation;
import com.yuangee.flower.customer.widget.CustomEmptyView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * Created by admin on 2018/1/18.
 */

public class SearchAcitvity extends RxBaseActivity implements ShoppingContract.View, PullLoadMoreRecyclerView.PullLoadMoreListener, AddAnimateListener {
    @BindView(R.id.search_frame)
    LinearLayout searchFrame;

    @BindView(R.id.edit_suggest)
    EditText editSuggest;

    @BindView(R.id.clear_edit)
    ImageView clearEdit;

    @BindView(R.id.menu_cart_icon)
    ImageView cartMenu;

    @BindView(R.id.cancel_action)
    TextView cancelAction;

    @BindView(R.id.good_recycler)
    PullLoadMoreRecyclerView plRecycler;

    @BindView(R.id.empty_layout)
    CustomEmptyView emptyView;

    @BindView(R.id.tag_container)
    TagContainerLayout tagContainerLayout;

    @BindView(R.id.root_view)
    RelativeLayout rootView;

    private ShoppingPresenter presenter;
    private GoodsAdapter adapter;
    private SearchAcitvity addAnimateListener;
    private int selectedNum;

    private ArrayList<Goods> goodsList;
    private int page = 0;
    private int limit = 10;
    private String params = "";//关键字
    private String bespeak;

    @OnClick(R.id.left_back)
    void finishSeachActivity() {
        finish();
    }

    @OnClick(R.id.menu_cart_icon)
    void gotoShopingCart() {
        Intent it = new Intent(SearchAcitvity.this, MainActivity.class);
        startActivity(it);
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_layout;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initSearchFrame();
        initTag();
        presenter = new ShoppingPresenter(this);
        presenter.setMV(new ShoppingModel(this), this);
        initRecyclerView();
        addAnimateListener = this;
        bespeak = getIntent().getStringExtra("bespeak");
        if (getIntent().hasExtra("params")){
            this.params=getIntent().getStringExtra("params");
            editSuggest.setText(params);
            findWares(params);
            hideSoft();
        }
    }

    public void initRecyclerView() {

        plRecycler.setFooterViewText("加载中..");
        if (getIntent().hasExtra("bespeak")) {
            adapter = new GoodsAdapter(this, 1, mRxManager);
        } else {
            adapter = new GoodsAdapter(this, 0, mRxManager);
        }
        adapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(SearchAcitvity.this, WaresDetailActivity.class);
                intent.putExtra("goods", adapter.getData().get(position));
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
        plRecycler.getRecyclerView().setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        goodsList = new ArrayList<>();

        adapter.setData(goodsList);

        plRecycler.setOnPullLoadMoreListener(this);

        plRecycler.setVerticalScrollBarEnabled(true);

    }

    private void initSearchFrame() {
        editSuggest.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoft();
                    String params = editSuggest.getText().toString();
                    findWares(params);
                    String str = App.me().getSharedPreferences().getString("history", "");
                    SharedPreferences.Editor editor = App.me().getSharedPreferences().edit();
                    if (StringUtils.isBlank(str)) {
                        editor.putString("history", params);
                    } else {
                        boolean exist = false;
                        if (str.contains(",")) {
                            String[] array = str.split(",");
                            for (String s : array) {
                                if (s.equals(params)) {
                                    exist = true;
                                }
                            }
                        } else {
                            if (str.equals(params)) {
                                exist = true;
                            }
                        }
                        if (!exist) {
                            str += "," + params;
                            editor.putString("history", str);
                        }
                    }
                    editor.apply();
                    return true;
                }
                return false;
            }
        });
        editSuggest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != editable) {
                    String s = editable.toString();
                    if (StringUtils.isEmpty(s)) {
                        clearEdit.setVisibility(View.GONE);
                    } else {
                        clearEdit.setVisibility(View.VISIBLE);
                    }
                } else {
                    clearEdit.setVisibility(View.GONE);
                }
            }
        });
        cancelAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneUtil.hideKeyboard(SearchAcitvity.this);
            }
        });
        clearEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editSuggest.setText("");
            }
        });
    }

    public void initTag() {
        tagContainerLayout.removeAllTags();
        showTag();
        searchFrame.setVisibility(View.VISIBLE);
        editSuggest.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) editSuggest.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editSuggest, 0);
    }

    private void showTag() {
        String str = App.me().getSharedPreferences().getString("history", "");
        if (StringUtils.isNotBlank(str)) {
            if (str.contains(",")) {
                String[] array = str.split(",");
                for (String s : array) {
                    tagContainerLayout.addTag(s);
                }
            } else {
                tagContainerLayout.addTag(str);
            }
            tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
                @Override
                public void onTagClick(int position, String text) {
                    editSuggest.setText(text);
                    editSuggest.setSelection(text.length());
                }

                @Override
                public void onTagLongClick(int position, String text) {

                }

                @Override
                public void onTagCrossClick(int position) {

                }
            });
        }
    }

    private void hideSoft() {
        // 先隐藏键盘
        ((InputMethodManager) editSuggest.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(SearchAcitvity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
                    presenter.searchGoodsData(params, page, limit, bespeak);
                }
            });
        } else {
            emptyView.setEmptyImage(R.drawable.ic_filed);
            emptyView.setEmptyText("貌似出了点问题，\n点我重试");
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page = 0;
                    presenter.searchGoodsData(params, page, limit, bespeak);
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


    public void findWares(String params) {
        this.params = params;
        page = 0;
        presenter.searchGoodsData(params, page, limit, bespeak);
    }

    @Override
    public void onRefresh() {
        this.params = "";
        page = 0;
        goodsList.clear();
        plRecycler.setRefreshing(true);
        presenter.searchGoodsData(params, page, limit, bespeak);
    }

    @Override
    public void onLoadMore() {
        page++;
        presenter.searchGoodsData(params, page, limit, bespeak);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void showAddAnimate(ImageView startView, int selectedNum) {
        AddCartAnimation.AddToCart(startView, cartMenu, this, rootView, 1);
    }
}
