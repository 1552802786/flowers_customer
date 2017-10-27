package com.yuangee.flower.customer.activity;

import android.os.Bundle;
import android.view.View;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.base.RxBaseActivity;
import com.yuangee.flower.customer.picker.AddressInitTask;
import com.yuangee.flower.customer.util.ToastUtil;

import java.util.ArrayList;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;

/**
 * Created by Administrator on 2017/10/27.
 */

public class EditAddressActivity extends RxBaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_add_address;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    public void showPicker(View view) {
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
                        ToastUtil.showMessage(EditAddressActivity.this, provinceName + " " + cityName + " " + countyName);
                    }
                });
                picker.show();
            }
        }).execute();
    }
}
