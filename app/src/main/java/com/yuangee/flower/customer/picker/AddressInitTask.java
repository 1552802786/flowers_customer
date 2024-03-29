package com.yuangee.flower.customer.picker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.util.ConvertUtils;

/**
 * 获取地址数据
 *
 * @author 李玉江[QQ:1032694760]
 * @since 2017/10/13
 */
public class AddressInitTask extends AsyncTask<Void, Void, ArrayList<Province>> {
    private Activity activity;
    private ProgressDialog dialog;
    private InitCallback callback;
    private ArrayList<Province> provinces;

    public AddressInitTask(Activity activity, InitCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(activity, null, "正在初始化数据...", true, true);
    }

    @Override
    protected ArrayList<Province> doInBackground(Void... params) {
        try {
            String data = ConvertUtils.toString(activity.getAssets().open("city.txt"));
            return parseData(data);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Province> result) {
        dialog.dismiss();
        if (result == null || result.size() == 0) {
            callback.onDataInitFailure();
        } else {
            callback.onDataInitSuccess(result);
        }
    }

    @WorkerThread
    private ArrayList<Province> parseData(String data) {
        if (provinces != null && provinces.size() > 0) {
            return provinces;
        }
        provinces = new ArrayList<>();
        String[] fullCodeAndNames = data.split(";");
        for (String fullCodeAndName : fullCodeAndNames) {
            String[] codeAndName = fullCodeAndName.split(",");
            if (codeAndName.length != 2) {
                continue;
            }
            String code = codeAndName[0];
            String name = codeAndName[1];
            if (code.substring(2, 6).equals("0000")) {
                //省份
                Province province = new Province();
                province.setAreaId(code);
                province.setAreaName(name);
                province.setCities(new ArrayList<City>());
                provinces.add(province);
            } else if (code.substring(4, 6).equals("00")) {
                //地市
                Province province = findProvinceByCode(code.substring(0, 2));
                if (province != null) {
                    City city = new City();
                    city.setAreaId(code);
                    city.setAreaName(name);
                    city.setCounties(new ArrayList<County>());
                    province.getCities().add(city);
                }
            } else {
                //区县
                City city = findCityByCode(code.substring(0, 2), code.substring(2, 4));
                if (city != null) {
                    County county = new County();
                    county.setAreaId(code);
                    county.setAreaName(name);
                    city.getCounties().add(county);
                }
            }
        }
        return provinces;
    }

    private Province findProvinceByCode(String provinceCode) {
        for (Province province : provinces) {
            if (province.getAreaId().substring(0, 2).equals(provinceCode)) {
                return province;
            }
        }
        return null;
    }

    private City findCityByCode(String provinceCode, String cityCode) {
        for (Province province : provinces) {
            List<City> cities = province.getCities();
            for (City city : cities) {
                if (province.getAreaId().substring(0, 2).equals(provinceCode) &&
                        city.getAreaId().substring(2, 4).equals(cityCode)) {
                    return city;
                }
            }
        }
        return null;
    }

    public interface InitCallback {

        void onDataInitFailure();

        void onDataInitSuccess(ArrayList<Province> provinces);

    }

}
