package com.yuangee.flower.customer.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Goods;

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

    @BindView(R.id.wares_name)
    TextView wares_name;
    @BindView(R.id.length)
    TextView length;
    @BindView(R.id.grade)
    TextView grade;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.unit)
    TextView unit;
    @BindView(R.id.sale_no)
    TextView sale_no;
    @BindView(R.id.cate_name)
    TextView cate_name;
    @BindView(R.id.sub_name)
    TextView subName;
    @BindView(R.id.shop_name)
    TextView shop_name;

    private Goods goods;

    @Override
    public void initViews(Bundle savedInstanceState) {
        goods = (Goods) getIntent().getSerializableExtra("goods");
        if (goods == null) {
            finish();
            return;
        }
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_no_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(this)
                .load(Config.BASE_URL + goods.image)
                .apply(options)
                .into(waresImg);
        wares_name.setText(goods.name);
        length.setText(goods.spec);
        grade.setText(goods.grade);
        price.setText("" + goods.unitPrice);
        unit.setText(goods.unit);
        sale_no.setText("" + goods.salesVolume);
        cate_name.setText(goods.genreName);
        shop_name.setText(goods.shopName);
        subName.setText(goods.genreSubName);
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
}
