package com.yuangee.flower.customer.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.entity.Address;
import com.yuangee.flower.customer.entity.Express;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.permission.RxPermissions;
import com.yuangee.flower.customer.picker.AddressInitTask;
import com.yuangee.flower.customer.util.StringUtils;
import com.yuangee.flower.customer.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/10/27.
 */

public class EditAddressActivity extends RxBaseActivity {

    @OnClick(R.id.pick_consignee)
    void pickConsignee() {
        flag = 0;
        showPhoneImportDialog(personConsignee, "收货人姓名");
    }

    @OnClick(R.id.pick_consignee_phone)
    void pickConsigneePhone() {
        flag = 1;
        showPhoneImportDialog(personConsigneePhone, "收货人电话");
    }

    @OnClick(R.id.pick_consignee_method)
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

    @OnClick(R.id.confirm)
    void apply() {
        if (confirm.getText().toString().equals("更新收货地址")) {
            updateMemberAddress();
        } else {
            createMemberAddress();
        }
    }

    @BindView(R.id.person_consignee_phone)
    TextView personConsigneePhone;

    @BindView(R.id.person_consignee_place_1)
    TextView personConsigneePlace1;

    @BindView(R.id.person_consignee_place_2)
    TextView personConsigneePlace2;

    @BindView(R.id.person_consignee_method)
    TextView personConsigneeMethod;

    @BindView(R.id.person_consignee)
    TextView personConsignee;

    @BindView(R.id.confirm)
    Button confirm;

    private Address address;

    @Override
    public void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("收货信息");

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
    public int getLayoutId() {
        return R.layout.activity_add_address;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        address = (Address) getIntent().getSerializableExtra("address");
        if (address == null) {
            address = new Address();
            confirm.setText("创建收货地址");
        } else {
            personConsignee.setText(address.shippingName);
            personConsigneePhone.setText(address.shippingPhone);
            personConsigneeMethod.setText(address.defaultAddress ? "是" : "否");
            personConsigneePlace1.setText(address.pro + address.city + address.city);
            personConsigneePlace2.setText(address.street);
            confirm.setText("更新收货地址");
        }
    }

    public void showPickerPlace(final TextView tv) {
        new AddressInitTask(this, new AddressInitTask.InitCallback() {
            @Override
            public void onDataInitFailure() {
                ToastUtil.showMessage(EditAddressActivity.this, "数据初始化失败");
            }

            @Override
            public void onDataInitSuccess(ArrayList<Province> provinces) {
                AddressPicker picker = new AddressPicker(EditAddressActivity.this, provinces);
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

    private AlertDialog dialog;

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

    RxPermissions rxPermissions;

    private int flag = 0;

    EditText importEdit;

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
                rxPermissions = new RxPermissions(EditAddressActivity.this);
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
                                        ToastUtil.showMessage(EditAddressActivity.this, "获取联系人失败");
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

    private String pickedPhone;
    private String pickedName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    }

    private void createMemberAddress() {
        if (StringUtils.isBlank(address.shippingName)
                || StringUtils.isBlank(address.shippingPhone) || StringUtils.isBlank(address.street)
                || StringUtils.isBlank(address.pro)) {
            ToastUtil.showMessage(EditAddressActivity.this, "请将信息填写完整");
            return;
        }
        Observable<Object> observable = ApiManager.getInstance().api
                .createMemberAddress(App.getPassengerId(), address.shippingName, address.shippingPhone,
                        address.pro, address.city, address.area, address.street, false)
                .map(new HttpResultFunc<Object>(EditAddressActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(EditAddressActivity.this, true, true, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object expresses) {
                ToastUtil.showMessage(EditAddressActivity.this, "信息更新成功");
                Intent intent = new Intent();
                intent.putExtra("address", address);
                setResult(RESULT_OK, intent);
                finish();
            }
        })));
    }

    private void updateMemberAddress() {
        if (StringUtils.isBlank(address.shippingName)
                || StringUtils.isBlank(address.shippingPhone) || StringUtils.isBlank(address.street)
                || StringUtils.isBlank(address.pro)) {
            ToastUtil.showMessage(EditAddressActivity.this, "请将信息填写完整");
            return;
        }
        Observable<Object> observable = ApiManager.getInstance().api
                .updateMemberAddress(App.getPassengerId(), address.shippingName, address.shippingPhone,
                        address.pro, address.city, address.area, address.street, false)
                .map(new HttpResultFunc<Object>(EditAddressActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(EditAddressActivity.this, true, true, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object expresses) {
                ToastUtil.showMessage(EditAddressActivity.this, "信息更新成功");
                Intent intent = new Intent();
                intent.putExtra("address", address);
                setResult(RESULT_OK, intent);
                finish();
            }
        })));
    }

    private void showBooleanDialog() {
        RadioGroup radioGroup = new RadioGroup(this);
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
