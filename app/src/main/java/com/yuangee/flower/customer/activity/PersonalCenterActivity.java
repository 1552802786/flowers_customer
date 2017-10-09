package com.yuangee.flower.customer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.util.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by developerLzh on 2017/8/22 0022.
 */

public class PersonalCenterActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @OnClick(R.id.pick_photo)
    void pickPhoto() {
        choosePic(1, 1);
    }

    @BindView(R.id.ic_photo)
    ImageView icPhoto;

    @BindView(R.id.person_name)
    TextView personName;

    @BindView(R.id.person_gender)
    TextView personGender;

    @BindView(R.id.person_phone)
    TextView personPhone;

    @BindView(R.id.person_email)
    TextView personEmail;

    @BindView(R.id.person_consignee)
    TextView personConsignee;

    @BindView(R.id.person_consignee_phone)
    TextView personConsigneePhone;

    @BindView(R.id.person_consignee_place)
    TextView personConsigneePlace;

    @BindView(R.id.person_consignee_method)
    TextView personConsigneeMethod;

    private AlertDialog dialog;

    @OnClick(R.id.pick_name)
    void pickName() {
        showEditDialog(personName,"姓名");
    }

    @OnClick(R.id.pick_gender)
    void pickGender() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.gender_select_layout, null);
        RadioButton maleRadio = view.findViewById(R.id.male_radio);
        RadioButton femaleRadio = view.findViewById(R.id.female_radio);
        if (personGender.getText().toString().equals("男")) {
            maleRadio.setChecked(true);
        } else {
            femaleRadio.setChecked(true);
        }
        maleRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    personGender.setText("男");
                    dialog.dismiss();
//                    updateMemberInfo(null, null, "1");
                }
            }
        });
        femaleRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    personGender.setText("女");
                    dialog.dismiss();
//                    updateMemberInfo(null, null, "0");
                }
            }
        });
        dialog = new AlertDialog.Builder(this)
                .setTitle("性别")
                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @OnClick(R.id.pick_phone)
    void pickPhone() {
        showEditDialog(personPhone,"手机号码");
    }

    @OnClick(R.id.pick_email)
    void pickEmail(){
        showEditDialog(personEmail,"邮箱");
    }

    @OnClick(R.id.pick_consignee)
    void pickConsignee(){
        showEditDialog(personConsignee,"收货人姓名");
    }

    @OnClick(R.id.pick_consignee_phone)
    void pickConsigneePhone(){
        showEditDialog(personConsigneePhone,"收货人电话");
    }

    @OnClick(R.id.person_consignee_method)
    void pickConsigneeMethod(){
        showEditDialog(personConsigneeMethod,"收货方式");
    }
    @OnClick(R.id.person_consignee_place)
    void pickConsigneePlace(){
        showEditDialog(personConsigneePlace,"收货地址");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_personal_center;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        toolbar.setTitle("个人中心");
        setSupportActionBar(toolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> images = PictureSelector.obtainMultipleResult(data);
                if (images != null && images.size() > 0) {
                    String path = images.get(0).getCutPath();
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.ic_default_photo_gray)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(PersonalCenterActivity.this)
                            .load(path)
                            .apply(options)
                            .into(icPhoto);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showEditDialog(final TextView showView, String hint){
        final EditText editName = new EditText(this);
        String sName = showView.getText().toString();
        editName.setText(sName);
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
