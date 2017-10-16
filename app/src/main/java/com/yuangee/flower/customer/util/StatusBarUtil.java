package com.yuangee.flower.customer.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by developerLzh on 2017/4/21.
 */

public class StatusBarUtil {
    /**
     * 设置状态栏状态 该方法目标api版本LOLLIPOP（21）
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusColor(Activity context, int colorRes) {
        //sdk版本大于21，即LOLLIPOP方法时才执行该方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getResources().getColor(colorRes));
        }
    }

    /**
     * 设置透明状态栏 该方法目标api版本LOLLIPOP（21）
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setTransStatusBar(Activity context) {
        //sdk版本大于22，即LOLLIPOP方法时才执行该方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = context.getWindow();
            //设置透明状态栏,这样才能让 ContentView 向上
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            ViewGroup mContentView = (ViewGroup) context.findViewById(Window.ID_ANDROID_CONTENT);    //获取根布局
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其为系统 View 预留空间.
                ViewCompat.setFitsSystemWindows(mChildView, true);
            }
        }
    }

    /**
     * 对于一些控件会延伸到状态栏的解决
     * 此方法的父控件必须是 DrawerLayout
     * @param activity
     * @param views
     */
    public static void setStateBar(Activity activity, View... views) {
        setTransStatusBar(activity);

        //5.0版本或以上才执行
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            int height = getStatusBarHeight(activity);  //获取状态栏高度

            for (int i = 0; i < views.length; i++) {
                View v = views[i];
                DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) v.getLayoutParams();
                params.topMargin = height;  //设置topMargin
                v.setLayoutParams(params);
            }

        }
    }

    /**
     * 获得状态栏的高度
     *
     * @return :
     */
    public static int getStatusBarHeight(Activity context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
