package com.yuangee.flower.customer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.AppManager;
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

    @OnClick(R.id.address_manage)
    void addressManage() {
        startActivity(new Intent(PersonalCenterActivity.this, SecAddressActivity.class));
    }

    private AlertDialog dialog;

    @OnClick(R.id.pick_name)
    void pickName() {
        showEditDialog(personName, "姓名");
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
        showEditDialog(personPhone, "手机号码");
    }

    @OnClick(R.id.pick_email)
    void pickEmail() {
        showEditDialog(personEmail, "邮箱");
    }

    @OnClick(R.id.pick_consignee)
    void pickConsignee() {
        showEditDialog(personConsignee, "收货人姓名");
    }

    @OnClick(R.id.pick_consignee_phone)
    void pickConsigneePhone() {
        showEditDialog(personConsigneePhone, "收货人电话");
    }

    @OnClick(R.id.person_consignee_method)
    void pickConsigneeMethod() {
        showEditDialog(personConsigneeMethod, "收货方式");
    }

    @OnClick(R.id.person_consignee_place)
    void pickConsigneePlace() {
        showEditDialog(personConsigneePlace, "收货地址");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_personal_center;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        showMember();
    }

    private void showMember() {
        Member member = App.me().getMemberInfo();
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_default_photo_gray)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(this)
                .load(Config.BASE_URL + member.photo)
                .apply(options)
                .into(icPhoto);
        personName.setText(member.name);
        personGender.setText(!member.gender ? "男" : "女");
        personPhone.setText(member.phone);
        personEmail.setText(member.email);
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle("个人中心");
        setSupportActionBar(toolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        updateMemberInfo();
    }

    private void updateMemberInfo() {
        long id = App.getPassengerId();
        String name = personName.getText().toString();
        boolean gender = personGender.getText().toString().equals("女");
        String phone = personPhone.getText().toString();
        String email = personEmail.getText().toString();

        MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", String.valueOf(id));
        MultipartBody.Part namePart = MultipartBody.Part.createFormData("name", String.valueOf(name));
        MultipartBody.Part genderPart = MultipartBody.Part.createFormData("gender", String.valueOf(gender));
        MultipartBody.Part phonePart = MultipartBody.Part.createFormData("phone", String.valueOf(phone));
        MultipartBody.Part emailPart = MultipartBody.Part.createFormData("email", String.valueOf(email));

        MultipartBody.Part photoPart = null;
        if (StringUtils.isNotBlank(secPhoto)) {
            photoPart = MultipartBody.Part.createFormData("photo", "photo.png", RequestBody.create(MediaType.parse("image/png"), new File(secPhoto)));
        }

        Observable<Object> observable = ApiManager.getInstance().api
                .updateMember(idPart, namePart, genderPart, phonePart, emailPart, photoPart)
                .map(new HttpResultFunc<>(PersonalCenterActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(PersonalCenterActivity.this, true, false, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                ToastUtil.showMessage(PersonalCenterActivity.this, "个人信息更新成功");
                finish();
            }
        })));

    }

    private String secPhoto = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> images = PictureSelector.obtainMultipleResult(data);
                if (images != null && images.size() > 0) {
                    secPhoto = images.get(0).getCutPath();
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.ic_default_photo_gray)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(PersonalCenterActivity.this)
                            .load(secPhoto)
                            .apply(options)
                            .into(icPhoto);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showEditDialog(final TextView showView, String hint) {
        final EditText editName = new EditText(this);
        String sName = showView.getText().toString();
        editName.setText(sName);
        if (hint.contains("号码") || hint.contains("电话")) {
            editName.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (hint.contains("邮箱")) {
            editName.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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

    public void exit(View view) {
        AppManager.getAppManager().finishAllActivity();
        App.me().getSharedPreferences().edit().clear().apply();
        startActivity(new Intent(PersonalCenterActivity.this, LoginActivity.class));
    }
}
