package com.yuangee.flower.customer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.widget.ExpandableHeightGridView;
import com.yuangee.flower.customer.widget.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by liuzihao on 2017/12/20.
 */

public class WaresDetailActivity extends RxBaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activiy_wares_detail;
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.wares_img)
    ImageView waresImg;
    @BindView(R.id.short_name_view)
    ExpandableHeightGridView gridView;
    @BindView(R.id.long_name_view)
    ExpandableHeightListView listView;
    private Goods goods;
    private List<String> shortInfos = new ArrayList<>();
    private List<String> longInfos = new ArrayList<>();
    private Context mContext;

    @Override
    public void initViews(Bundle savedInstanceState) {
        mContext = this;
        goods = (Goods) getIntent().getSerializableExtra("goods");
        if (goods == null) {
            finish();
            return;
        }
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_no_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        if (!TextUtils.isEmpty(goods.image)) {
            Glide.with(this)
                    .load(Config.BASE_URL + goods.image)
                    .apply(options)
                    .into(waresImg);
        } else {
            waresImg.setImageResource(R.drawable.ic_no_img);
        }
        if (length(goods.name) > 10) {
            longInfos.add("商品名称+" + goods.name);
        } else {
            shortInfos.add("商品名称+" + goods.name);
        }
        if (length(goods.spec) > 10) {
            longInfos.add("规格+" + goods.spec);
        } else {
            shortInfos.add("规格+" + goods.spec);
        }
        if (length(goods.grade) > 10) {
            longInfos.add("等级+" + goods.grade);
        } else {
            shortInfos.add("等级+" + goods.grade);
        }
        if (length(String.valueOf(goods.unitPrice)) > 10) {
            longInfos.add("单价+" + goods.unitPrice);
        } else {
            shortInfos.add("单价+" + goods.unitPrice);
        }
        if (goods.depict != null) {
            String[] depicts = goods.depict.substring(1, goods.depict.length() - 1).trim().split(",");
            for (int j = 0; j < depicts.length; j++) {
                String[] kv = depicts[j].trim().split(":");
                if (TextUtils.isEmpty(kv[1])) {
                    shortInfos.add(kv[0].trim().substring(1, kv[0].trim().length() - 1) + "+" + kv[1].trim().substring(1, kv[1].trim().length() - 1));
                } else {
                    if (length(kv[1]) > 10) {
                        longInfos.add(kv[0].trim().substring(1, kv[0].trim().length() - 1) + "+" + kv[1].trim().substring(1, kv[1].trim().length() - 1));
                    } else {
                        shortInfos.add(kv[0].trim().substring(1, kv[0].trim().length() - 1) + "+" + kv[1].trim().substring(1, kv[1].trim().length() - 1));
                    }
                }
            }
        }
        if (length(goods.startDeliver) > 10) {
            longInfos.add("发货时间+" + goods.startDeliver);
        } else {
            shortInfos.add("发货时间+" + goods.startDeliver);
        }
        shortInfos.add("购买单位+" + goods.unit);
        shortInfos.add("可售量+" + goods.salesVolume);
        if (length(goods.genreSubName) > 10) {
            longInfos.add("类名+" + goods.genreName);
        } else {
            shortInfos.add("类名+" + goods.genreName);
        }
        if (length(goods.memo) > 10) {
            longInfos.add("备注+" + (TextUtils.isEmpty(goods.memo) ? "<暂无>" : goods.memo));
        } else {
            shortInfos.add("备注+" + (TextUtils.isEmpty(goods.memo) ? "<暂无>" : goods.memo));
        }
        gridView.setExpanded(true);
        InfoAdapter shortAdapter = new InfoAdapter(3);
        gridView.setAdapter(shortAdapter);
        InfoAdapter longAdapter = new InfoAdapter(0);
        listView.setAdapter(longAdapter);
    }

    public static int length(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("商品详情");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private class InfoAdapter extends BaseAdapter {
        private int flag = -1;

        public InfoAdapter(int flag) {
            this.flag = flag;
        }

        @Override
        public int getCount() {
            if (flag == 0) {
                return longInfos.size();
            } else {
                return shortInfos.size();
            }
        }

        @Override
        public String getItem(int i) {
            if (flag == 0) {
                return longInfos.get(i);
            } else {
                return shortInfos.get(i);
            }
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.wares_info_item, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.initData(getItem(i));
            return view;
        }

        class ViewHolder {
            TextView keyLabel;
            TextView value;

            public ViewHolder(View v) {
                keyLabel = v.findViewById(R.id.label_name);
                value = v.findViewById(R.id.value_name);
            }

            public void initData(String str) {
                String[] infos = str.split("\\+");
                if (flag == 0) {
                    keyLabel.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    value.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    keyLabel.setPadding(16, 0, 0, 0);
                    value.setPadding(16, 0, 0, 0);
                }
                if (infos.length > 0) {
                    keyLabel.setText(infos[0]);
                }
                if (infos.length < 2) {
                    value.setText("无");
                } else {
                    value.setText(infos[1]);
                }
            }
        }
    }
}
