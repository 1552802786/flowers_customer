package com.yuangee.flower.customer.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.yuangee.flower.customer.entity.Address;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.permission.RxPermissions;
import com.yuangee.flower.customer.picker.AddressInitTask;
import com.yuangee.flower.customer.util.AppManager;
import com.yuangee.flower.customer.util.DisplayUtil;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/8/22 0022.
 */

public class PersonalCenterActivity extends RxBaseActivity {

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


    @BindView(R.id.person_consignee_place)
    TextView personConsigneePlace;

    @BindView(R.id.login_out_btn)
    TextView loginOut;
    @OnClick(R.id.login_out_btn)
    void loginOut(){
        exit();
    }
    @OnClick(R.id.back_btn)
    void onBackBtn() {
        finish();
    }
    @OnClick(R.id.save_info_btn)
    void saveInfo(){
        updateMemberInfo();
        createMemberAddress();
    }
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_personal_center;
    }
    private Context mContext;
    @Override
    public void initViews(Bundle savedInstanceState) {
        //loginOut.setVisibility(View.VISIBLE);
        showMember();
        address = new Address();
        mContext=this;
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
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                Uri uri = data.getData();
                if (uri != null) {
                    ContentResolver cr = getContentResolver();
                    Cursor cursor = cr.query(uri, null, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                                null,
                                null);

                        if (phone != null) {
                            //查询该联系人的所有号码
                            while (phone.moveToNext()) {
                                String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                Log.e("TAG", usernumber + " (" + username + ")");
                                if (flag == 0) {
                                    importEdit.setText(username);
                                } else {
                                    importEdit.setText(usernumber);
                                }
                                pickedPhone = usernumber;
                                pickedName = username;
                            }
                            phone.close();
                        } else {
                            ToastUtil.showMessage(this, "未能成功获取联系人");
                        }
                        cursor.close();
                    } else {
                        ToastUtil.showMessage(this, "未能成功获取联系人");
                    }
                } else {
                    ToastUtil.showMessage(this, "未能成功获取联系人");
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
                        address.street = s;
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

    public void exit() {
        dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定退出吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        AppManager.getAppManager().finishAllActivity();
                        App.me().getSharedPreferences().edit().clear().apply();
                        startActivity(new Intent(PersonalCenterActivity.this, LoginActivity.class));
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();

    }

    @BindView(R.id.consignee_phone)
    TextView personConsigneePhone;

    @BindView(R.id.person_consignee_place_1)
    TextView personConsigneePlace1;

    @BindView(R.id.person_consignee_place_2)
    TextView personConsigneePlace2;

    @BindView(R.id.consignee_method)
    TextView personConsigneeMethod;

    @BindView(R.id.person_consignee_name)
    TextView personConsignee;
    @OnClick(R.id.person_consignee_name)
    void pickConsignee() {
        flag = 0;
        showPhoneImportDialog(personConsignee, "收货人姓名");
    }
    private int flag = 0;
    @OnClick(R.id.consignee_phone)
    void pickConsigneePhone() {
        flag = 1;
        showPhoneImportDialog(personConsigneePhone, "收货人电话");
    }

    @OnClick(R.id.consignee_method)
    void pickConsigneeMethod() {
        showBooleanDialog();
    }

    @OnClick(R.id.pick_consignee_place_1)
    void pickConsigneePlace() {
        showPickerPlace(personConsigneePlace1);
    }

    @OnClick(R.id.pick_consignee_place_2)
    void editConsigneePlace() {
        showEditDialog(personConsigneePlace2, "收货详细地址");
    }
    private Address address;
    private void createMemberAddress() {
        if (StringUtils.isBlank(address.shippingName)
                || StringUtils.isBlank(address.shippingPhone) || StringUtils.isBlank(address.street)
                || StringUtils.isBlank(address.pro)) {
            ToastUtil.showMessage(mContext, "请将信息填写完整");
            return;
        }
        Observable<Object> observable = ApiManager.getInstance().api
                .createMemberAddress(App.getPassengerId(), address.shippingName, address.shippingPhone,
                        address.pro, address.city, address.area, address.street, false)
                .map(new HttpResultFunc<Object>(mContext))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(mContext, true, true, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object expresses) {
                ToastUtil.showMessage(mContext, "添加地址成功");
                finish();
            }
        })));
    }

    public void showPickerPlace(final TextView tv) {
        new AddressInitTask(this, new AddressInitTask.InitCallback() {
            @Override
            public void onDataInitFailure() {
                ToastUtil.showMessage(mContext, "数据初始化失败");
            }

            @Override
            public void onDataInitSuccess(ArrayList<Province> provinces) {
                AddressPicker picker = new AddressPicker(PersonalCenterActivity.this, provinces);
                picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                    @Override
                    public void onAddressPicked(Province province, City city, County county) {
                        String provinceName = province.getName();
                        String cityName = "";
                        if (city != null) {
                            cityName = city.getName();
                            //忽略直辖市的二级名称
                            if (cityName.equals("市辖区") || cityName.equals("市") || cityName.equals("县")) {
                                cityName = "";
                            }
                        }
                        String countyName = "";
                        if (county != null) {
                            countyName = county.getName();
                        }
                        address.pro = provinceName;
                        address.city = cityName;
                        address.area = countyName;
                        tv.setText(provinceName + cityName + countyName);
                    }
                });
                picker.show();
            }
        }).execute();
    }

    RxPermissions rxPermissions;
    EditText importEdit;
    private String pickedPhone;
    private String pickedName;
    private void showPhoneImportDialog(final TextView showView, String hint) {
        View view = getLayoutInflater().inflate(R.layout.import_dialog, null);
        importEdit = view.findViewById(R.id.edit_text);
        importEdit.setText(showView.getText());
        importEdit.setHint(hint);
        if (flag == 0) {
            importEdit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        } else {
            importEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        }
        RelativeLayout rl = view.findViewById(R.id.import_book);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxPermissions = new RxPermissions(PersonalCenterActivity.this);
                if (!rxPermissions.isGranted(Manifest.permission.READ_CONTACTS)) {
                    rxPermissions.request(Manifest.permission.READ_CONTACTS)
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean granted) {
                                    if (granted) {
                                        Intent intent = new Intent(Intent.ACTION_PICK,
                                                ContactsContract.Contacts.CONTENT_URI);
                                        startActivityForResult(intent, 0);
                                    } else {
                                        ToastUtil.showMessage(PersonalCenterActivity.this, "获取联系人失败");
                                    }
                                }
                            });
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 0);
                }
            }
        });
        dialog = new AlertDialog.Builder(this)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = importEdit.getText().toString();
                        showView.setText(input);
                        if (flag == 0) {
                            address.shippingName = input;
                        } else {
                            address.shippingPhone = input;
                        }
                        if (StringUtils.isNotBlank(pickedName) && StringUtils.isNotBlank(pickedPhone)) {
                            if (input.equals(pickedName) || input.equals(pickedPhone)) {
                                if (showView.getId() == R.id.person_consignee) {
                                    personConsigneePhone.setText(pickedPhone);
                                    address.shippingPhone = pickedPhone;
                                } else if (showView.getId() == R.id.person_consignee_phone) {
                                    personConsignee.setText(pickedName);
                                    address.shippingName = pickedName;
                                }
                                pickedName = null;
                                pickedPhone = null;
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setView(view)
                .create();
        dialog.show();
    }
    private void showBooleanDialog() {
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setPadding(DisplayUtil.dp2px(this, 20), DisplayUtil.dp2px(this, 10), 0, 0);
        final RadioButton trueBtn = new RadioButton(this);
        trueBtn.setText("是");
        RadioButton falseBtn = new RadioButton(this);
        falseBtn.setText("否");
        radioGroup.addView(trueBtn);
        radioGroup.addView(falseBtn);
        if (address.defaultAddress) {
            trueBtn.setChecked(true);
        } else {
            falseBtn.setChecked(true);
        }
        dialog = new AlertDialog.Builder(this)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(trueBtn.isChecked()){
                            address.defaultAddress = true;
                        } else {
                            address.defaultAddress = false;
                        }
                        personConsigneeMethod.setText(address.defaultAddress ? "是" : "否");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setTitle("是否默认")
                .setView(radioGroup)
                .create();
        dialog.show();
    }

}
