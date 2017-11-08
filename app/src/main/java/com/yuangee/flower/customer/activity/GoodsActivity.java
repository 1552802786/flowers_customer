package com.yuangee.flower.customer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.*;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DebugUtil;
import com.luck.picture.lib.tools.DoubleUtils;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.GridImageAdapter;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.entity.GenreSub;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;
import com.yuangee.flower.customer.widget.FullyGridLayoutManager;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
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

public class GoodsActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @OnClick(R.id.genre_first)
    void choiceFirst() {
        final RadioGroup group = new RadioGroup(this);
        if (null != MainActivity.genreList && MainActivity.genreList.size() != 0) {
            for (int i = 0; i < MainActivity.genreList.size(); i++) {
                Genre genre = MainActivity.genreList.get(i);
                RadioButton button = new RadioButton(this);
                if (goods.genreId == genre.id) {
                    button.setChecked(true);
                } else {
                    button.setChecked(false);
                }
                button.setText(genre.genreName);
                group.addView(button);
            }
            dialog = new AlertDialog.Builder(this)
                    .setTitle("选择品类")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            boolean flag = false;
                            for (int i1 = 0; i1 < MainActivity.genreList.size(); i1++) {
                                RadioButton btn = (RadioButton) group.getChildAt(i1);
                                if (btn.isChecked()) {
                                    goods.genreId = MainActivity.genreList.get(i1).id;
                                    goods.genreName = MainActivity.genreList.get(i1).genreName;
                                    flag = true;
                                    break;
                                }
                            }
                            if (!flag) {
                                ToastUtil.showMessage(GoodsActivity.this, "请选择一个类别");
                            } else {
                                genreFirst.setText(goods.genreName);
                                dialogInterface.dismiss();
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setView(group)
                    .create();
            dialog.show();
        } else {
            ToastUtil.showMessage(this, "没有可以备选的种类，请在管理系统里添加");
        }

    }

    @OnClick(R.id.genre_sub)
    void choiceSub() {
        final RadioGroup group = new RadioGroup(this);
        if (0 != goods.genreId) {
            for (int i = 0; i < MainActivity.genreList.size(); i++) {
                final Genre genre = MainActivity.genreList.get(i);
                if (genre.id == goods.genreId) {
                    for (GenreSub sub : genre.genreSubs) {
                        RadioButton button = new RadioButton(this);
                        if (goods.genreSubId == sub.id) {
                            button.setChecked(true);
                        } else {
                            button.setChecked(false);
                        }
                        button.setText(sub.name);
                        group.addView(button);
                    }
                    dialog = new AlertDialog.Builder(this)
                            .setTitle("选择类别")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    boolean flag = false;
                                    for (int i1 = 0; i1 < genre.genreSubs.size(); i1++) {
                                        RadioButton btn = (RadioButton) group.getChildAt(i1);
                                        if (btn.isChecked()) {
                                            goods.genreSubId = genre.genreSubs.get(i1).id;
                                            goods.genreSubName = genre.genreSubs.get(i1).name;
                                            flag = true;
                                            break;
                                        }
                                    }
                                    if (!flag) {
                                        ToastUtil.showMessage(GoodsActivity.this, "请选择一个类别");
                                    } else {
                                        genreSub.setText(goods.genreName);
                                        dialogInterface.dismiss();
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setView(group)
                            .create();
                    dialog.show();
                } else {
                    ToastUtil.showMessage(this, "请先选择种类");
                }
            }
        }

    }

    AlertDialog dialog;

    @OnClick(R.id.grade)
    void choiceGrade() {
        final List<String> grades = new ArrayList<>();
        grades.add("A");
        grades.add("B");
        grades.add("C");
        grades.add("D");
        final RadioGroup group = new RadioGroup(this);
        for (String s : grades) {
            RadioButton btn = new RadioButton(this);
            btn.setText(s);
            if (StringUtils.isNotBlank(goods.grade) && goods.grade.equals(s)) {
                btn.setChecked(true);
            } else {
                btn.setChecked(false);
            }
            group.addView(btn);
        }
        dialog = new AlertDialog.Builder(this)
                .setTitle("选择等级")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean flag = false;
                        for (int i1 = 0; i1 < grades.size(); i1++) {
                            RadioButton btn = (RadioButton) group.getChildAt(i1);
                            if (btn.isChecked()) {
                                goods.grade = grades.get(i1);
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            ToastUtil.showMessage(GoodsActivity.this, "请选择一个等级");
                        } else {
                            grade.setText(goods.grade);
                            dialogInterface.dismiss();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setView(group)
                .create();
        dialog.show();
    }

    @OnClick(R.id.color)
    void editColor() {
        showEditDialog(color, "输入颜色");
    }

    @OnClick(R.id.spec)
    void editSpec() {
        showEditDialog(spec, "输入规格");
    }

    @OnClick(R.id.unit)
    void editUnit() {
        showEditDialog(unit, "输入单位");
    }

    @OnClick(R.id.sales_value)
    void editSalesNum() {
        showEditDialog(salesValue, "输入可售量");
    }

    @OnClick(R.id.goods_price)
    void editPrice() {
        showEditDialog(salesValue, "输入单价");
    }

    @OnClick(R.id.goods_name)
    void editName() {
        showEditDialog(salesValue, "输入名称");
    }

    @BindView(R.id.genre_first)
    TextView genreFirst;

    @BindView(R.id.genre_sub)
    TextView genreSub;

    @BindView(R.id.grade)
    TextView grade;

    @BindView(R.id.color)
    TextView color;

    @BindView(R.id.spec)
    TextView spec;

    @BindView(R.id.unit)
    TextView unit;

    @BindView(R.id.sales_value)
    TextView salesValue;

    @BindView(R.id.goods_price)
    TextView goodsPrice;

    @BindView(R.id.goods_name)
    TextView goodsName;

    @BindView(R.id.ic_right_1)
    ImageView icRight1;
    @BindView(R.id.ic_right_2)
    ImageView icRight2;
    @BindView(R.id.ic_right_3)
    ImageView icRight3;
    @BindView(R.id.ic_right_4)
    ImageView icRight4;
    @BindView(R.id.ic_right_5)
    ImageView icRight5;
    @BindView(R.id.ic_right_6)
    ImageView icRight6;
    @BindView(R.id.ic_right_7)
    ImageView icRight7;
    @BindView(R.id.ic_right_8)
    ImageView icRight8;
    @BindView(R.id.ic_right_9)
    ImageView icRight9;

    @BindView(R.id.goods_img)
    ImageView goodsImg;

    @BindView(R.id.apply)
    Button apply;

    @OnClick(R.id.apply)
    void apply() {
        goods.grade = grade.getText().toString();
        goods.color = color.getText().toString();
        goods.spec = spec.getText().toString();
        goods.unit = unit.getText().toString();
        goods.name = goodsName.getText().toString();
        try {
            goods.salesVolume = Double.parseDouble(salesValue.getText().toString());
            goods.unitPrice = Double.parseDouble(goodsPrice.getText().toString());
        } catch (NumberFormatException e) {
            ToastUtil.showMessage(GoodsActivity.this, "请填写合法的单价或可售量");
            return;
        }
        createOrUpdate();
    }

    @OnClick(R.id.goods_img)
    void pickImg() {
        PictureSelector.create(GoodsActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .theme(R.style.picture_default_style)
                .selectionMode(PictureConfig.SINGLE)
                .previewImage(true)
                .isCamera(true)
                .enableCrop(false)
                .compress(true)
                .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
                .glideOverride(160, 160)
                .previewEggs(true)
                .hideBottomControls(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    private String imgPath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> images = PictureSelector.obtainMultipleResult(data);
                if (images != null && images.size() > 0) {
                    imgPath = images.get(0).getCutPath();
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.ic_default_photo_gray)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(GoodsActivity.this)
                            .load(imgPath)
                            .apply(options)
                            .into(goodsImg);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_goods_detail;
    }

    private boolean change = false;
    private Goods goods;

    private Long shopId;
    private String shopName;

    @Override
    public void initViews(Bundle savedInstanceState) {

        change = getIntent().getBooleanExtra("change", false);
        shopId = getIntent().getLongExtra("shopId", -1);
        shopName = getIntent().getStringExtra("shopName");
        goods = (Goods) getIntent().getSerializableExtra("goods");

        if (null != goods) {
            genreFirst.setText(goods.genreName);
            genreSub.setText(goods.genreSubName);
            grade.setText(goods.grade);
            color.setText(goods.color);
            spec.setText(goods.spec);
            unit.setText(goods.unit);
            salesValue.setText("" + goods.salesVolume);
            goodsName.setText(goods.name);
            goodsPrice.setText("" + goods.unitPrice);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_add_img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(GoodsActivity.this)
                    .load(Config.BASE_URL + goods.image)
                    .apply(options)
                    .into(goodsImg);
        }

        if (!change) {
            icRight1.setVisibility(View.GONE);
            icRight2.setVisibility(View.GONE);
            icRight3.setVisibility(View.GONE);
            icRight4.setVisibility(View.GONE);
            icRight5.setVisibility(View.GONE);
            icRight6.setVisibility(View.GONE);
            icRight7.setVisibility(View.GONE);
            icRight8.setVisibility(View.GONE);
            icRight9.setVisibility(View.GONE);

            genreFirst.setEnabled(false);
            genreSub.setEnabled(false);
            grade.setEnabled(false);
            color.setEnabled(false);
            spec.setEnabled(false);
            unit.setEnabled(false);
            salesValue.setEnabled(false);
            goodsImg.setEnabled(false);
            goodsPrice.setEnabled(false);
            goodsName.setEnabled(false);

            apply.setVisibility(View.GONE);
        } else {
            if (goods == null) {
                apply.setText("创建商品");
            } else {
                apply.setText("修改商品");
            }
        }


        if (goods == null) {
            goods = new Goods();
        }
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

    private void createOrUpdate() {
        if (StringUtils.isBlank(goods.image) && StringUtils.isBlank(imgPath)) {
            ToastUtil.showMessage(this, "请选择商品照片");
            return;
        }
        MultipartBody.Part waresImagePart = null;
        if (StringUtils.isNotBlank(imgPath)) {
            waresImagePart = MultipartBody.Part.createFormData("waresImage", "waresImage.png", RequestBody.create(MediaType.parse("image/png"), new File(imgPath)));
        }
        MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", String.valueOf(goods.id));
        MultipartBody.Part genreIdPart = MultipartBody.Part.createFormData("genreId", String.valueOf(goods.genreId));
        MultipartBody.Part genreSubIdPart = MultipartBody.Part.createFormData("genreSubId", String.valueOf(goods.genreSubId));
        MultipartBody.Part genreNamePart = MultipartBody.Part.createFormData("genreName", goods.genreName);
        MultipartBody.Part genreSubNamePart = MultipartBody.Part.createFormData("genreSubName", goods.genreSubName);
        MultipartBody.Part namePart = MultipartBody.Part.createFormData("name", goods.name);
        MultipartBody.Part gradePart = MultipartBody.Part.createFormData("grade", goods.grade);
        MultipartBody.Part colorPart = MultipartBody.Part.createFormData("color", goods.color);
        MultipartBody.Part specPart = MultipartBody.Part.createFormData("spec", goods.spec);
        MultipartBody.Part unitPart = MultipartBody.Part.createFormData("unit", goods.unit);
        MultipartBody.Part unitPricePart = MultipartBody.Part.createFormData("unitPrice", String.valueOf(goods.unitPrice));
        MultipartBody.Part salesVolumePart = MultipartBody.Part.createFormData("salesVolume", String.valueOf(goods.salesVolume));
        MultipartBody.Part shopIdPart = MultipartBody.Part.createFormData("shopId", String.valueOf(shopId));
        MultipartBody.Part shopNamePart = MultipartBody.Part.createFormData("shopName", String.valueOf(shopName));

        if(apply.getText().toString().contains("创建")){
            Observable<Object> observable = ApiManager.getInstance().api
                    .createWares(waresImagePart,genreIdPart,genreNamePart,genreSubIdPart,genreSubNamePart,namePart,gradePart,colorPart,specPart,unitPart,unitPricePart,salesVolumePart,shopIdPart,shopNamePart)
                    .map(new HttpResultFunc<>(GoodsActivity.this))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            mRxManager.add(observable.subscribe(new MySubscriber<>(GoodsActivity.this, true, false, new NoErrSubscriberListener<Object>() {
                @Override
                public void onNext(Object o) {
                    ToastUtil.showMessage(GoodsActivity.this, "商品创建成功");
                    finish();
                }
            })));
        } else {
            Observable<Object> observable = ApiManager.getInstance().api
                    .updateWares(idPart,waresImagePart,genreIdPart,genreNamePart,genreSubIdPart,genreSubNamePart,namePart,gradePart,colorPart,specPart,unitPart,unitPricePart,salesVolumePart,shopIdPart,shopNamePart)
                    .map(new HttpResultFunc<>(GoodsActivity.this))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            mRxManager.add(observable.subscribe(new MySubscriber<>(GoodsActivity.this, true, false, new NoErrSubscriberListener<Object>() {
                @Override
                public void onNext(Object o) {
                    ToastUtil.showMessage(GoodsActivity.this, "商品修改成功");
                    finish();
                }
            })));
        }
    }

    private void showEditDialog(final TextView showView, String hint) {
        final EditText editName = new EditText(this);
        String sName = showView.getText().toString();
        editName.setText(sName);
        if (hint.contains("号码") || hint.contains("电话")) {
            editName.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (hint.contains("邮箱")) {
            editName.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        } else if (hint.contains("可售量")) {
            editName.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            editName.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        if (!StringUtils.isEmpty(sName)) {
            editName.setSelection(sName.length());
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(50, 20, 50, 10);
        editName.setLayoutParams(layoutParams);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.addView(editName);
        dialog = new AlertDialog.Builder(this)
                .setTitle(hint)
                .setPositiveButton("好", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = editName.getText().toString();
                        showView.setText(s);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setView(linearLayout)
                .create();
        dialog.show();
    }
}
