package com.yuangee.flower.customer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.entity.MemberToken;
import com.yuangee.flower.customer.entity.Shop;
import com.yuangee.flower.customer.util.CommonUtil;

import java.io.File;
import java.util.List;


public class App {

    public static long sLastPositonUploaded;

    public static int sNextDelay = 5000;

    private static final int DEF_MAX_SPEED = 120;

    private static final String CONFIG_FILE = "config.properties";

    private static final String BASE_DATA_DIR = "";

    private static App instance;

    private boolean isDebug = true;

    private Context context;

    private SharedPreferences sharedPreferences;

    private String sp;

    private App(Context context) {

        this.sp = "consumer";
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(sp, Context.MODE_PRIVATE);
    }

    /**
     * 初始化 App
     */
    public static void initApp(Context context) {
        instance = new App(context);
    }

    /**
     * App 单例，必须在程序启动的时候进行初始化
     */
    public static App me() {
//		if(null == instance){
//			instance = new App();
//		}
        return instance;
    }

    public String getSp() {
        return sp;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public static String getDeviceName() {
        return Build.MANUFACTURER + "-" + Build.MODEL;
    }

    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getDeviceInfo() {
        StringBuilder buffer = new StringBuilder();
        String device = getDeviceName();
        buffer.append(device);
        buffer.append("(").append(getOSVersion()).append(")");
        return buffer.toString();
    }

    public String getAppVersion() {

        return CommonUtil.getAppVersion(context);
    }

    public static String getCrashFolder() {
        return BASE_DATA_DIR + File.separator + "crash";
    }

    public double getMaxSpeed() {

        return sharedPreferences.getInt("max_speed", DEF_MAX_SPEED);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static boolean isAnalysisDataEnabled() {
        return true;
    }

    public static long getPassengerId() {
        Long memberId = App.me().getSharedPreferences().getLong("id", -1);
        Log.e("memberId", "" + memberId);
        return memberId;
    }

    public Member getMemberInfo() {
        Member member = DbHelper.getInstance().getMemberLongDBManager().load(getPassengerId());
        member.listCoupon = DbHelper.getInstance().getCouponLongDBManager().loadAll();
        member.expressDelivery = DbHelper.getInstance().getExpressLongDBManager().loadAll();
        List<Shop> shops = DbHelper.getInstance().getShopLongDBManager().loadAll();
        if (null != shops && shops.size() != 0) {
            member.shop = shops.get(0);
        }
        member.memberAddressList = DbHelper.getInstance().getAddressLongDBManager().loadAll();
        return member;
    }

}
