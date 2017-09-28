package com.yuangee.flower.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.luck.picture.lib.*;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DebugUtil;
import com.luck.picture.lib.tools.DoubleUtils;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.GridImageAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.widget.FullyGridLayoutManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by developerLzh on 2017/9/28 0028.
 */

public class GoodsActivity extends RxBaseActivity {

    @BindView(R.id.photo_recycler)
    RecyclerView photoRecycler;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private GridImageAdapter adapter;
    private List<LocalMedia> selectList = new ArrayList<>();

    private int maxSelectNum = 5;


    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            PictureSelector.create(GoodsActivity.this)
                    .openGallery(PictureMimeType.ofImage())
                    .theme(R.style.picture_default_style)
                    .maxSelectNum(maxSelectNum)
                    .minSelectNum(1)
                    .selectionMode(PictureConfig.MULTIPLE)
                    .previewImage(true)
                    .isCamera(true)
                    .enableCrop(false)
                    .compress(true)
                    .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
                    .glideOverride(160, 160)
                    .previewEggs(true)
                    .hideBottomControls(true)
                    .selectionMedia(selectList)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择
                    selectList = PictureSelector.obtainMultipleResult(data);
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    DebugUtil.i("GoodsActivity", "onActivityResult:" + selectList.size());
                    break;
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_goods_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false);
        photoRecycler.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        photoRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                LocalMedia media = selectList.get(position);
                String pictureType = media.getPictureType();
                int mediaType = PictureMimeType.pictureToVideo(pictureType);
                switch (mediaType) {
                    case 1:
                        // 预览图片
                        if(!DoubleUtils.isFastDoubleClick()) {
                            Intent intent = new Intent(GoodsActivity.this,PictureExternalPreviewActivity.class);
                            intent.putExtra("previewSelectList", (Serializable)selectList);
                            intent.putExtra("position", position);
                            startActivity(intent);
                           overridePendingTransition(com.luck.picture.lib.R.anim.a5, 0);
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("商品详情");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
