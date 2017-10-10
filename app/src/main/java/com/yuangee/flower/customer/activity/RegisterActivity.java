package com.yuangee.flower.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DebugUtil;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by developerLzh on 2017/9/28 0028.
 */

public class RegisterActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @OnClick(R.id.card_frame)
    void pickCard() {
        currentImg = idCard;
        selectPic(4, 3);
    }

    @BindView(R.id.card_hint)
    LinearLayout cardHint;

    @BindView(R.id.id_card)
    ImageView idCard;

    @OnClick(R.id.card_sec_frame)
    void pickCardSec() {
        currentImg = idCardSec;
        selectPic(4, 3);
    }

    @BindView(R.id.card_sec_hint)
    LinearLayout cardSecHint;

    @BindView(R.id.id_card_sec)
    ImageView idCardSec;

    @OnClick(R.id.quanshen_frame)
    void pickQuanshen() {
        currentImg = quanshen;
        selectPic(3, 4);
    }

    @BindView(R.id.quanshen_hint)
    LinearLayout quanshenHint;

    @BindView(R.id.quanshen)
    ImageView quanshen;

    @OnClick(R.id.zhizhao_frame)
    void pickZhizhao() {
        currentImg = zhizhao;
        selectPic(1, 1);
    }

    @BindView(R.id.zhizhao_hint)
    LinearLayout zhizhaoHint;

    @BindView(R.id.zhizhao)
    ImageView zhizhao;

    @OnClick(R.id.company_frame)
    void pickCompany() {
        currentImg = company;
        selectPic(1, 1);
    }

    @BindView(R.id.company_hint)
    LinearLayout companyHint;

    @BindView(R.id.company)
    ImageView company;

    @BindView(R.id.card_no_hint)
    TextView cardNoHint;

    @BindView(R.id.id_card_tv)
    TextView idCardTv;

    @BindView(R.id.id_card_sec_tv)
    TextView idCardSecTv;

    @BindView(R.id.quanshen_tv)
    TextView quanshenTv;

    @BindView(R.id.company_img_con)
    LinearLayout companyImgCon;

    @BindView(R.id.person_check)
    TextView personCheck;

    @BindView(R.id.company_check)
    TextView companyCheck;

    private ImageView currentImg;

    @OnClick(R.id.person_check)
    void personCheck() {
        personCheck.setTextColor(getResources().getColor(R.color.colorAccent));
        companyCheck.setTextColor(getResources().getColor(R.color.txt_black));

        companyImgCon.setVisibility(View.GONE);
        cardNoHint.setText("身份证号码");
        idCardTv.setText("点击上传身份证正面");
        idCardSecTv.setText("点击上传身份证反面");
        quanshenTv.setText("   点击上传全身照    ");
    }

    @OnClick(R.id.company_check)
    void companyCheck() {
        personCheck.setTextColor(getResources().getColor(R.color.txt_black));
        companyCheck.setTextColor(getResources().getColor(R.color.colorAccent));

        companyImgCon.setVisibility(View.VISIBLE);
        cardNoHint.setText("法人身份证号码");
        idCardTv.setText("点击上传法人身份证正面");
        idCardSecTv.setText("点击上传法人身份证反面");
        quanshenTv.setText("   点击上传法人全身照    ");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_apply_supplier;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

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

    private void selectPic(int x, int y) {
        PictureSelector.create(RegisterActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .theme(R.style.picture_default_style)
                .maxSelectNum(1)
                .minSelectNum(1)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .isCamera(true)
                .enableCrop(true)
                .compress(true)
                .showCropGrid(true)
                .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
                .withAspectRatio(x,y)
                .previewEggs(true)
                .hideBottomControls(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    private String[] imgPaths = new String[5];//0:id_card 1:id_card_sec 2:quanshen 3:zhizhao 4:company

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList != null && selectList.size() != 0) {
                    LocalMedia media = selectList.get(0);
                    RequestOptions options = new RequestOptions()
                            .fitCenter()
                            .placeholder(R.color.color_f6)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);

                    Glide.with(RegisterActivity.this)
                            .load(media.getCutPath())
                            .apply(options)
                            .into(currentImg);

                    switch (currentImg.getId()) {
                        case R.id.id_card:
                            imgPaths[0] = media.getCutPath();
                            idCard.setVisibility(View.VISIBLE);
                            cardHint.setVisibility(View.GONE);
                            break;
                        case R.id.id_card_sec:
                            imgPaths[1] = media.getCutPath();
                            idCardSec.setVisibility(View.VISIBLE);
                            cardSecHint.setVisibility(View.GONE);
                            break;
                        case R.id.quanshen:
                            imgPaths[2] = media.getCutPath();
                            quanshen.setVisibility(View.VISIBLE);
                            quanshenHint.setVisibility(View.GONE);
                            break;
                        case R.id.zhizhao:
                            imgPaths[3] = media.getCutPath();
                            zhizhao.setVisibility(View.VISIBLE);
                            zhizhaoHint.setVisibility(View.GONE);
                            break;
                        case R.id.company:
                            imgPaths[4] = media.getCutPath();
                            company.setVisibility(View.VISIBLE);
                            companyHint.setVisibility(View.GONE);
                            break;
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
