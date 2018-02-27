package com.yuangee.flower.customer.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 百度位置检索
 * Created by admin on 2018/2/3.
 */

public class CustomerPlaceSearchActivity extends RxBaseActivity {

    @BindView(R.id.edit_suggest)
    EditText editSuggest;
    @BindView(R.id.clear_edit)
    ImageView clearEdit;

    @BindView(R.id.result_list)
    ListView result_list;

    private PoiSearch mPoiSearch;
    private String city;
    private SuggestionSearch mSuggestionSearch;
    private Context mContext;
    private ResultAdapter adapter;

    @OnClick(R.id.clear_edit)
    void clearInput() {
        editSuggest.setText("");
    }

    @OnClick(R.id.left_back)
    void leftBack() {
        finish();
    }

    private List<PoiInfo> data = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_place_search;
    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

        public void onGetPoiResult(PoiResult result) {
            //获取POI检索结果
            if (result.getAllPoi() == null) {
                return;
            }
            data.clear();
            data.addAll(result.getAllPoi());
            adapter.notifyDataSetChanged();
            for (PoiInfo info : result.getAllPoi()) {
                Log.e("百度", info.name + "--" + info.location.latitude + "--" + info.location.longitude);
            }
        }

        public void onGetPoiDetailResult(PoiDetailResult result) {
            //获取Place详情页检索结果

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };
    OnGetSuggestionResultListener suggestionResultListener = new OnGetSuggestionResultListener() {
        public void onGetSuggestionResult(SuggestionResult res) {

            if (res == null || res.getAllSuggestions() == null) {
                return;
                //未找到相关结果
            }
            data.clear();
            for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                PoiInfo p = new PoiInfo();
                p.name = info.key;
                p.location = info.pt;
                data.add(p);
            }
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void initViews(Bundle savedInstanceState) {
        city = getIntent().getStringExtra("city");
        initSearch();
        mPoiSearch = PoiSearch.newInstance();
        mSuggestionSearch = SuggestionSearch.newInstance();

        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        mSuggestionSearch.setOnGetSuggestionResultListener(suggestionResultListener);
        mContext = this;
        adapter = new ResultAdapter();
        result_list.setAdapter(adapter);
        result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent it=new Intent();
                it.putExtra("result",adapter.getItem(i));
                setResult(RESULT_OK,it);
                finish();
            }
        });
    }

    private void initSearch() {
        editSuggest.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    mPoiSearch.searchInCity((new PoiCitySearchOption())
                            .city(city)
                            .keyword(editSuggest.getText().toString())
                            .pageNum(20));
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
                    // 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                    mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                            .keyword(editable.toString())
                            .city(city));
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
    }

    class ResultAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public PoiInfo getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView;
            if (view == null) {
                textView = new TextView(mContext);
                textView.setPadding(0, 24, 0, 24);
                view = textView;
            } else {
                textView = (TextView) view;
            }
            textView.setText(getItem(i).name);
            return view;
        }
    }
}
