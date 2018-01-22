package com.yuangee.flower.customer;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.util.CrashHandler;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by hcc on 16/8/7 21:18
 * 100332338@qq.com
 * <p/>
 * 哔哩哔哩动画App
 */
public class FlowerApp extends MultiDexApplication {

    public static FlowerApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance(this);
        mInstance = this;
        App.initApp(this);
        init();


    }

    public static Context getContext() {
        return mInstance;
    }

    private void init() {
        //初始化Stetho调试工具
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        initDataBase();
        initJPush();
    }

    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    public static FlowerApp getInstance() {
        return mInstance;
    }

    private void initDataBase() {
        DbHelper.getInstance().init(getApplicationContext());
    }
}
