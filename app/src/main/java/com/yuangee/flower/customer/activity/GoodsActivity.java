package com.yuangee.flower.customer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.entity.GenreSub;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.DisplayUtil;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ch.ielse.view.SwitchView;
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
        ScrollView scrollView = new ScrollView(GoodsActivity.this);
        final RadioGroup group = new RadioGroup(this);
        scrollView.addView(group);
        group.setPadding(DisplayUtil.dp2px(GoodsActivity.this, 20), DisplayUtil.dp2px(GoodsActivity.this, 10), 0, 0);
        if (null != MainActivity.getGenre() && MainActivity.getGenre().size() != 0) {
            for (int i = 0; i < MainActivity.getGenre().size(); i++) {
                Genre genre = MainActivity.getGenre().get(i);
                RadioButton button = new RadioButton(this);
                button.setText(genre.genreName);
                group.addView(button);

                if (goods.genreId == genre.id) {
                    button.setChecked(true);
                } else {
                    button.setChecked(false);
                }
            }
            dialog = new AlertDialog.Builder(this)
                    .setTitle("选择品类")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            boolean flag = false;
                            for (int i1 = 0; i1 < MainActivity.getGenre().size(); i1++) {
                                RadioButton btn = (RadioButton) group.getChildAt(i1);
                                if (btn.isChecked()) {
                                    goods.genreId = MainActivity.getGenre().get(i1).id;
                                    goods.genreName = MainActivity.getGenre().get(i1).genreName;
                                    goods.genreSubId = 0;
                                    goods.genreSubName = "";
                                    genreSub.setText("");
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
                    .setView(scrollView)
                    .create();
            dialog.show();
        } else {
            ToastUtil.showMessage(this, "没有可以备选的种类，请在管理系统里添加");
        }

    }

    @OnClick(R.id.genre_sub)
    void choiceSub() {
        ScrollView scrollView = new ScrollView(GoodsActivity.this);
        final RadioGroup group = new RadioGroup(this);
        scrollView.addView(group);
        group.setPadding(DisplayUtil.dp2px(GoodsActivity.this, 20), DisplayUtil.dp2px(GoodsActivity.this, 10), 0, 0);
        if (0 != goods.genreId) {
            for (int i = 0; i < MainActivity.getGenre().size(); i++) {
                final Genre genre = MainActivity.getGenre().get(i);
                if (genre.id == goods.genreId) {
                    if (genre.genreSubs == null) {
                        return;
                    }
                    for (GenreSub sub : genre.genreSubs) {
                        RadioButton button = new RadioButton(this);
                        button.setText(sub.name);
                        group.addView(button);
                        if (goods.genreSubId == sub.id) {
                            button.setChecked(true);
                        } else {
                            button.setChecked(false);
                        }
                    }
                    dialog = new AlertDialog.Builder(this)
                            .setTitle("选择类别")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    boolean flag = false;
                                    for (int b = 0; b < not_nead_layout.getChildCount(); b++) {
                                        not_nead_layout.getChildAt(b).setVisibility(View.GONE);
                                    }
                                    for (int i1 = 0; i1 < genre.genreSubs.size(); i1++) {
                                        RadioButton btn = (RadioButton) group.getChildAt(i1);
                                        if (btn.isChecked()) {
                                            goods.genreSubId = genre.genreSubs.get(i1).id;
                                            goods.genreSubName = genre.genreSubs.get(i1).name;
                                            String[] depictStr = genre.genreSubs.get(i1).depictName.split(",");
                                            for (int a = 0; a < depictStr.length; a++) {
                                                not_nead_layout.setVisibility(View.VISIBLE);
                                                not_nead_layout.getChildAt(a * 2).setVisibility(View.VISIBLE);
                                                not_nead_layout.getChildAt(a * 2 + 1).setVisibility(View.VISIBLE);
                                                LinearLayout linearLayout = (LinearLayout) not_nead_layout.getChildAt(a * 2);
                                                ((TextView) linearLayout.getChildAt(1)).setText(depictStr[a]);
                                            }
                                            flag = true;
                                            break;
                                        }
                                    }
                                    if (!flag) {
                                        ToastUtil.showMessage(GoodsActivity.this, "请选择一个类别");
                                    } else {
                                        genreSub.setText(goods.genreSubName);
                                        dialogInterface.dismiss();
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setView(scrollView)
                            .create();
                    dialog.show();
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
        group.setPadding(DisplayUtil.dp2px(GoodsActivity.this, 20), DisplayUtil.dp2px(GoodsActivity.this, 10), 0, 0);
        for (String s : grades) {
            RadioButton btn = new RadioButton(this);
            btn.setText(s);
            group.addView(btn);
            if (StringUtils.isNotBlank(goods.grade) && goods.grade.equals(s)) {
                btn.setChecked(true);
            } else {
                btn.setChecked(false);
            }
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

    @OnClick(R.id.unit)
    void editUnit() {
        ScrollView scrollView = new ScrollView(GoodsActivity.this);
        final List<String> units = new ArrayList<>();
        units.add("束");
        units.add("扎");
        units.add("支");
        units.add("卷");
        units.add("本");
        units.add("箱");
        units.add("个");
        units.add("套");
        units.add("件");
        units.add("包");
        units.add("盒");
        units.add("袋");
        units.add("朵");
        units.add("瓶");
        final RadioGroup group = new RadioGroup(this);
        scrollView.addView(group);
        group.setPadding(DisplayUtil.dp2px(GoodsActivity.this, 20), DisplayUtil.dp2px(GoodsActivity.this, 10), 0, 0);
        for (String s : units) {
            RadioButton btn = new RadioButton(this);
            btn.setText(s);
            group.addView(btn);
            if (StringUtils.isNotBlank(goods.unit) && goods.unit.equals(s)) {
                btn.setChecked(true);
            } else {
                btn.setChecked(false);
            }
        }
        dialog = new AlertDialog.Builder(this)
                .setTitle("选择单位")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean flag = false;
                        for (int i1 = 0; i1 < units.size(); i1++) {
                            RadioButton btn = (RadioButton) group.getChildAt(i1);
                            if (btn.isChecked()) {
                                goods.unit = units.get(i1);
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            ToastUtil.showMessage(GoodsActivity.this, "请选择一个单位");
                        } else {
                            unit.setText(goods.unit);
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
                .setView(scrollView)
                .create();
        dialog.show();
    }

    @OnClick(R.id.goods_apply_date)
    void chooseDate() {
        final List<String> date = new ArrayList<>();
        date.add("全天");
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                date.add("0" + i + "点");
            } else {
                date.add(i + "点");
            }
        }
        ScrollView scrollView = new ScrollView(GoodsActivity.this);
        final RadioGroup group = new RadioGroup(this);
        scrollView.addView(group);
        group.setPadding(DisplayUtil.dp2px(GoodsActivity.this, 20), DisplayUtil.dp2px(GoodsActivity.this, 10), 0, 0);
        for (String s : date) {
            RadioButton btn = new RadioButton(this);
            btn.setText(s);
            group.addView(btn);
            if (StringUtils.isNotBlank(goods.startDeliver) && goods.startDeliver.equals(s)) {
                btn.setChecked(true);
            } else {
                btn.setChecked(false);
            }
        }
        dialog = new AlertDialog.Builder(this)
                .setTitle("选择发货时间")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean flag = false;
                        for (int i1 = 0; i1 < date.size(); i1++) {
                            RadioButton btn = (RadioButton) group.getChildAt(i1);
                            if (btn.isChecked()) {
                                goods.startDeliver = date.get(i1);
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            ToastUtil.showMessage(GoodsActivity.this, "请选择时间");
                        } else {
                            receiveDate.setText(goods.startDeliver);
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
                .setView(scrollView)
                .create();
        dialog.show();
    }

    @OnClick(R.id.is_reserve_switch)
    void isReserveSwitch() {
        if (!isReserve.isOpened()) {
            reserveNeedIocn.setVisibility(View.INVISIBLE);
            reserveInputLayout.setVisibility(View.INVISIBLE);
        } else {
            reserveNeedIocn.setVisibility(View.VISIBLE);
            reserveInputLayout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.is_jingpai_switch)
    void isJingpaiSwitch() {
        if (!isJinpai.isOpened()) {
            jingpaiNeedIocn.setVisibility(View.INVISIBLE);
            jingpaiInputLayout.setVisibility(View.INVISIBLE);
        } else {
            jingpaiNeedIocn.setVisibility(View.VISIBLE);
            jingpaiInputLayout.setVisibility(View.VISIBLE);
        }
    }

    //参与预约
    @BindView(R.id.is_reserve_switch)
    SwitchView isReserve;
    @BindView(R.id.reserve_need_icon)
    ImageView reserveNeedIocn;
    @BindView(R.id.reserve_input_layout)
    View reserveInputLayout;
    @BindView(R.id.goods_reserve_count)
    EditText reserveCount;

    //参与竞拍
    @BindView(R.id.is_jingpai_switch)
    SwitchView isJinpai;
    @BindView(R.id.jingpai_need_icon)
    ImageView jingpaiNeedIocn;
    @BindView(R.id.jingpai_input_layout)
    View jingpaiInputLayout;
    @BindView(R.id.goods_jingpai_count)
    EditText jingpaiPrice;


    @BindView(R.id.goods_apply_date)
    TextView receiveDate;

    @BindView(R.id.genre_first)
    TextView genreFirst;

    @BindView(R.id.genre_sub)
    TextView genreSub;

    @BindView(R.id.grade)
    TextView grade;

    @BindView(R.id.color)
    EditText color;

    @BindView(R.id.spec)
    EditText specLength;

    @BindView(R.id.unit)
    TextView unit;

    @BindView(R.id.sales_value)
    EditText salesValue;

    @BindView(R.id.goods_price)
    EditText goodsPrice;

    @BindView(R.id.goods_name)
    EditText goodsName;

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

    @BindView(R.id.change_or_add_hint)
    TextView changeOrAdd;

    @BindView(R.id.goods_first_chooseAble_label)
    TextView first_chooseAble_label;
    @BindView(R.id.goods_first_chooseAble_text)
    EditText first_chooseAble_Text;

    @BindView(R.id.goods_second_chooseAble_label)
    TextView second_chooseAble_label;
    @BindView(R.id.goods_second_chooseAble_text)
    EditText second_chooseAble_Text;

    @BindView(R.id.goods_third_chooseAble_label)
    TextView third_chooseAble_label;
    @BindView(R.id.goods_third_chooseAble_text)
    EditText third_chooseAble_Text;

    @BindView(R.id.goods_fourth_chooseAble_label)
    TextView fourth_chooseAble_label;
    @BindView(R.id.goods_fourth_chooseAble_text)
    EditText fourth_chooseAble_Text;

    @BindView(R.id.not_need_option)
    LinearLayout not_nead_layout;
    @BindView(R.id.user_message_text)
    EditText userMessage;

    @OnClick(R.id.apply)
    void apply() {

        subCheck();

    }

    @OnClick(R.id.goods_img)
    void pickImg() {
        PictureSelector.create(GoodsActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .theme(R.style.picture_default_style)
                .selectionMode(PictureConfig.SINGLE)
                .previewImage(true)
                .isCamera(true)
                .enableCrop(true)
                .withAspectRatio(4, 3)
                .compress(true)
                .showCropFrame(true)
                .showCropGrid(true)
                .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
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
                            .placeholder(R.drawable.ic_add_img)
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
            not_nead_layout.setVisibility(View.VISIBLE);
            genreFirst.setText(goods.genreName);
            genreSub.setText(goods.genreSubName);
            grade.setText(goods.grade);
            color.setText(goods.color);
            specLength.setText(goods.spec);
            unit.setText(goods.unit);
            salesValue.setText("" + goods.salesVolume);
            goodsName.setText(goods.name);
            goodsPrice.setText("" + goods.unitPrice);
            isReserve.setOpened(goods.bespeak);
            isReserveSwitch();
            isJinpai.setOpened(goods.auction);
            isJingpaiSwitch();
            receiveDate.setText(goods.startDeliver);
            userMessage.setText(goods.memo);
            reserveCount.setText("" + goods.bespeakNum);
            jingpaiPrice.setText("" + goods.mumPrice);
            setDetail();
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(change ? R.drawable.ic_add_img : R.color.color_f6)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(GoodsActivity.this)
                    .load(Config.BASE_URL + goods.image)
                    .apply(options)
                    .into(goodsImg);
            if (StringUtils.isNotBlank(goods.image)) {
                changeOrAdd.setText("修改商品图片");
            } else {
                changeOrAdd.setText("添加商品图片");
            }
        } else {
            changeOrAdd.setText("修改商品图片");
        }

        if (!change) {
            icRight1.setVisibility(View.INVISIBLE);
            icRight2.setVisibility(View.INVISIBLE);
            icRight3.setVisibility(View.INVISIBLE);
            icRight4.setVisibility(View.INVISIBLE);
            icRight5.setVisibility(View.INVISIBLE);
            icRight6.setVisibility(View.INVISIBLE);
            icRight7.setVisibility(View.INVISIBLE);
            icRight8.setVisibility(View.INVISIBLE);
            icRight9.setVisibility(View.INVISIBLE);

            changeOrAdd.setVisibility(View.GONE);

            genreFirst.setClickable(false);
            genreSub.setClickable(false);
            grade.setClickable(false);
            color.setEnabled(false);
            specLength.setEnabled(false);
            unit.setClickable(false);
            salesValue.setEnabled(false);
            goodsImg.setClickable(false);
            goodsPrice.setEnabled(false);
            goodsName.setEnabled(false);

            apply.setVisibility(View.GONE);
        } else {
            changeOrAdd.setVisibility(View.VISIBLE);
            if (goods == null) {
                apply.setText("发布");
            } else {
                apply.setText("修改");
            }
        }


        if (goods == null) {
            goods = new Goods();
        }
    }


    //设置包装说明
    private void setDetail() {
        for (int i = 0; i < DbHelper.getInstance().getGenreList().size(); i++) {
            final Genre genre = DbHelper.getInstance().getGenreList().get(i);
            if (genre.id == goods.genreId) {
                for (GenreSub sub : genre.genreSubs) {
                    if (goods.genreSubId == sub.id) {
                        String[] depictStr = sub.depictName.split(",");
                        for (int a = 0; a < depictStr.length; a++) {
                            not_nead_layout.setVisibility(View.VISIBLE);
                            not_nead_layout.getChildAt(a * 2).setVisibility(View.VISIBLE);
                            not_nead_layout.getChildAt(a * 2 + 1).setVisibility(View.VISIBLE);
                            LinearLayout linearLayout = (LinearLayout) not_nead_layout.getChildAt(a * 2);
                            ((TextView) linearLayout.getChildAt(1)).setText(depictStr[a]);
                        }
                    }
                }
            }
        }
        try {
            JSONObject obj = new JSONObject(goods.depict);
            String value = obj.getString(first_chooseAble_label.getText().toString().trim());
            if (!TextUtils.isEmpty(value)) {
                first_chooseAble_Text.setText(value);
            }
            value = obj.getString(second_chooseAble_label.getText().toString().trim());
            if (!TextUtils.isEmpty(value)) {
                second_chooseAble_Text.setText(value);
            }
            value = obj.getString(third_chooseAble_label.getText().toString().trim());
            if (!TextUtils.isEmpty(value)) {
                third_chooseAble_Text.setText(value);
            }
            value = obj.getString(fourth_chooseAble_label.getText().toString().trim());
            if (!TextUtils.isEmpty(value)) {
                fourth_chooseAble_Text.setText(value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        MultipartBody.Part actionPart;
        MultipartBody.Part bespeakPart;
        MultipartBody.Part bespeakNumPart = MultipartBody.Part.createFormData("bespeakNum", reserveCount.getText().toString());
        if (isReserve.isOpened()) {
            bespeakPart = MultipartBody.Part.createFormData("bespeak", String.valueOf(1));
        } else {
            bespeakPart = MultipartBody.Part.createFormData("bespeak", String.valueOf(0));
        }
        if (isJinpai.isOpened()) {
            actionPart = MultipartBody.Part.createFormData("auction", String.valueOf(1));
        } else {
            actionPart = MultipartBody.Part.createFormData("auction", String.valueOf(0));
        }
        JSONObject object = new JSONObject();
        try {
            object.put(first_chooseAble_label.getText().toString().trim(), first_chooseAble_Text.getText().toString().trim());
            object.put(second_chooseAble_label.getText().toString().trim(), second_chooseAble_Text.getText().toString().trim());
            object.put(third_chooseAble_label.getText().toString().trim(), third_chooseAble_Text.getText().toString().trim());
            object.put(fourth_chooseAble_label.getText().toString().trim(), fourth_chooseAble_Text.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MultipartBody.Part depictPart = MultipartBody.Part.createFormData("depict", object.toString());
        MultipartBody.Part memoPart = MultipartBody.Part.createFormData("memo", userMessage.getText().toString().trim());
        MultipartBody.Part mumPricePart = MultipartBody.Part.createFormData("mumPrice", jingpaiPrice.getText().toString().trim());
        MultipartBody.Part deliverPart = MultipartBody.Part.createFormData("startDeliver", receiveDate.getText().toString().trim());
        if (apply.getText().toString().contains("发布")) {
            Observable<Object> observable = ApiManager.getInstance().api
                    .createWares(waresImagePart, genreIdPart, genreNamePart, genreSubIdPart, genreSubNamePart, namePart, gradePart, colorPart, specPart, unitPart, unitPricePart, salesVolumePart, shopIdPart, shopNamePart,
                            actionPart, bespeakPart, bespeakNumPart, depictPart, memoPart, mumPricePart, deliverPart)
                    .map(new HttpResultFunc<>(GoodsActivity.this))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            mRxManager.add(observable.subscribe(new MySubscriber<>(GoodsActivity.this, true, false, new NoErrSubscriberListener<Object>() {
                @Override
                public void onNext(Object o) {
                    ToastUtil.showMessage(GoodsActivity.this, "商品发布成功");
                    finish();
                }
            })));
        } else {
            Observable<Object> observable = ApiManager.getInstance().api
                    .updateWares(idPart, waresImagePart, genreIdPart, genreNamePart, genreSubIdPart, genreSubNamePart, namePart, gradePart, colorPart, specPart, unitPart, unitPricePart, salesVolumePart, shopIdPart, shopNamePart,
                            actionPart, bespeakPart, bespeakNumPart, depictPart, memoPart, mumPricePart, deliverPart)
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

    private void subCheck() {
        boolean a1 = isReserve.isOpened() && TextUtils.isEmpty(reserveCount.getText().toString());
        boolean a2 = isJinpai.isOpened() && TextUtils.isEmpty(jingpaiPrice.getText().toString());
        if (a1) {
            ToastUtil.showMessage(this, "请填写预约数");
            return;
        } else if (a2) {
            ToastUtil.showMessage(this, "请填写竞拍价");
            return;
        } else if (StringUtils.isBlank(genreFirst.getText().toString().trim())) {
            ToastUtil.showMessage(this, "请选择类别");
            return;
        } else if (StringUtils.isBlank(genreSub.getText().toString().trim())) {
            ToastUtil.showMessage(this, "请选择子类");
            return;
        } else if (StringUtils.isBlank(goodsName.getText().toString().trim())) {
            ToastUtil.showMessage(this, "请填写名字");
            return;
        } else if (TextUtils.isEmpty(grade.getText().toString().trim())) {
            ToastUtil.showMessage(this, "请选择等级");
            return;
        } else if (TextUtils.isEmpty(color.getText().toString().trim())) {
            ToastUtil.showMessage(this, "请填写颜色");
            return;
        } else if (TextUtils.isEmpty(specLength.getText().toString().trim())) {
            ToastUtil.showMessage(this, "请填写规格");
            return;
        } else if (TextUtils.isEmpty(unit.getText().toString().trim())) {
            ToastUtil.showMessage(this, "请选择单位");
            return;
        }
        if (TextUtils.isEmpty(goodsPrice.getText().toString().trim())) {
            ToastUtil.showMessage(this, "请填写价格");
            return;
        } else if (TextUtils.isEmpty(salesValue.getText().toString().trim())) {
            ToastUtil.showMessage(this, "请填写数量");
            return;
        } else if (TextUtils.isEmpty(receiveDate.getText().toString().trim())) {
            ToastUtil.showMessage(this, "请填写数量");
            return;
        }
        goods.grade = grade.getText().toString();
        goods.color = color.getText().toString();
        goods.spec = specLength.getText().toString();
        goods.unit = unit.getText().toString();
        goods.name = goodsName.getText().toString();
        goods.unitPrice = Double.valueOf(goodsPrice.getText().toString());
        goods.salesVolume = Integer.valueOf(salesValue.getText().toString());
        createOrUpdate();
    }
}
