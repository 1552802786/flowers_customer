package com.yuangee.flower.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    @BindView(R.id.shop_name)
    EditText shopNameEdit;

    @BindView(R.id.simple_name)
    EditText simpleNameEdit;

    @BindView(R.id.fuzeren)
    EditText fuzerenEdit;

    @BindView(R.id.phone)
    EditText phoneEdit;

    @BindView(R.id.detail_address)
    EditText detailAddressEdit;

    @BindView(R.id.card_no)
    EditText cardNoEdit;

    @BindView(R.id.card_address)
    EditText cardAddressEdit;

    @BindView(R.id.card_zhu_name)
    EditText cardZhuNameEdit;

    @BindView(R.id.id_card_no)
    EditText idCardNoEdit;

    @BindView(R.id.jian_jie)
    EditText jianJieEdit;

    @BindView(R.id.zhizhao_no)
    EditText zhizhaoNoEdit;

    @OnClick(R.id.apply_now)
    void apply() {
        applyMsg();
    }

    private ImageView currentImg;

    private boolean isPersonal = true;

    @OnClick(R.id.person_check)
    void personCheck() {
        personCheck.setTextColor(getResources().getColor(R.color.colorAccent));
        companyCheck.setTextColor(getResources().getColor(R.color.txt_black));

        isPersonal = true;

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

        isPersonal = false;

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
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(RegisterActivity.this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/emdriver")// 自定义拍照保存路径,可不填
                .enableCrop(true)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                .withAspectRatio(x, y)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                .previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
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

    private void applyMsg() {

        String shopName = shopNameEdit.getText().toString();
        String simpleName = simpleNameEdit.getText().toString();
        String jianjie = jianJieEdit.getText().toString();
        String fuzeren = fuzerenEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        String address = detailAddressEdit.getText().toString();
        String cardNo = cardNoEdit.getText().toString();
        String cardAddress = cardAddressEdit.getText().toString();
        String cardZhuName = cardZhuNameEdit.getText().toString();
        String idCardNo = idCardNoEdit.getText().toString();
        String zhizhaoNo = zhizhaoNoEdit.getText().toString();
        if (StringUtils.isBlank(shopName) ||
                StringUtils.isBlank(simpleName) ||
                StringUtils.isBlank(fuzeren) ||
                StringUtils.isBlank(phone) ||
                StringUtils.isBlank(address) ||
                StringUtils.isBlank(cardNo) ||
                StringUtils.isBlank(cardAddress) ||
                StringUtils.isBlank(cardZhuName) ||
                StringUtils.isBlank(jianjie) ||
                StringUtils.isBlank(idCardNo) ||
                ((StringUtils.isBlank(zhizhaoNo) && !isPersonal))
                ) {
            ToastUtil.showMessage(RegisterActivity.this, "请将信息填写完整");
            return;
        }
        //0:id_card 1:id_card_sec 2:quanshen 3:zhizhao 4:company
        if (StringUtils.isBlank(imgPaths[0]) ||
                StringUtils.isBlank(imgPaths[1]) ||
                StringUtils.isBlank(imgPaths[2]) ||
                (!isPersonal && StringUtils.isBlank(imgPaths[3])) ||
                (!isPersonal && StringUtils.isBlank(imgPaths[4]))) {
            ToastUtil.showMessage(RegisterActivity.this, "请将照片选择完整");
            return;
        }

        MultipartBody.Part shopNamePart = MultipartBody.Part.createFormData("shopName", String.valueOf(shopName));
        MultipartBody.Part simpleNamePart = MultipartBody.Part.createFormData("simpleShopName", String.valueOf(simpleName));
        MultipartBody.Part fuzerenPart = MultipartBody.Part.createFormData("name", String.valueOf(fuzeren));
        MultipartBody.Part phonePart = MultipartBody.Part.createFormData("phone", String.valueOf(phone));

        MultipartBody.Part addressPart = MultipartBody.Part.createFormData("address", String.valueOf(address));
        MultipartBody.Part jianjiePart = MultipartBody.Part.createFormData("description", String.valueOf(jianjie));
        MultipartBody.Part bankNoPart = MultipartBody.Part.createFormData("bankNo", String.valueOf(cardNo));
        MultipartBody.Part bankNamePart = MultipartBody.Part.createFormData("bankName", String.valueOf(cardAddress));
        MultipartBody.Part cardZhuNamePart = MultipartBody.Part.createFormData("openAccountName", String.valueOf(cardZhuName));

        MultipartBody.Part idCardNoPart = MultipartBody.Part.createFormData("idCardNo", String.valueOf(idCardNo));

        MultipartBody.Part idCardPart = MultipartBody.Part.createFormData("idImgFront", "idImgFront.png", RequestBody.create(MediaType.parse("image/png"), new File(imgPaths[0])));
        MultipartBody.Part idCard2Part = MultipartBody.Part.createFormData("idImgBack", "idImgBack.png", RequestBody.create(MediaType.parse("image/png"), new File(imgPaths[1])));
        MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", "photo.png", RequestBody.create(MediaType.parse("image/png"), new File(imgPaths[2])));
        MultipartBody.Part businessLicencePart = null;
        if (StringUtils.isNotBlank(imgPaths[3])) {
            businessLicencePart = MultipartBody.Part.createFormData("businessLicence", "businessLicence.png", RequestBody.create(MediaType.parse("image/png"), new File(imgPaths[3])));
        }
        MultipartBody.Part scene1Part = null;
        if (StringUtils.isNotBlank(imgPaths[4])) {
            scene1Part = MultipartBody.Part.createFormData("scene1", "scene1.png", RequestBody.create(MediaType.parse("image/png"), new File(imgPaths[4])));
        }

        MultipartBody.Part zhizhaoNoPart = MultipartBody.Part.createFormData("businessLicenceNo", String.valueOf(zhizhaoNo));

        MultipartBody.Part memberIdPart = MultipartBody.Part.createFormData("memberId", String.valueOf(App.getPassengerId()));
        MultipartBody.Part typePart = MultipartBody.Part.createFormData("type", String.valueOf(isPersonal ? 0 : 1));

        Observable<Object> observable = ApiManager.getInstance().api
                .shopApply(shopNamePart, simpleNamePart, fuzerenPart, phonePart, addressPart, jianjiePart, bankNoPart, bankNamePart, cardZhuNamePart, idCardNoPart, idCardPart, idCard2Part, photoPart, businessLicencePart, scene1Part
                        , zhizhaoNoPart, memberIdPart, typePart)
                .map(new HttpResultFunc<>(RegisterActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(RegisterActivity.this, true, false, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                ToastUtil.showMessage(RegisterActivity.this, "申请提交成功");
                finish();
            }
        })));
    }
}
