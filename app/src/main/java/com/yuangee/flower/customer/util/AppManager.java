package com.yuangee.flower.customer.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.yuangee.flower.customer.App;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class AppManager {

    private static Stack<Activity> activityStack;
    private static volatile AppManager instance;

    /**
     * 构造方法.
     */
    private AppManager() {
    }

    /**
     * getAppManager.
     *
     * @return AppManager
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }

    /**
     * addActivity.
     *
     * @param activity activity
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * currentActivity.
     *
     * @return Activity
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * finishActivity.
     *
     * @param activity activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && null != activityStack) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * finishActivity.
     *
     * @param cls cls
     */
    public void finishActivity(Class<?> cls) {
        if (null != activityStack) {

            List<Activity> activities = new LinkedList<Activity>();
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    activities.add(activity);
                }
            }

            //����Activity
            for (Activity activity : activities) {
                finishActivity(activity);
            }

        }
    }

    /**
     * finishActivity.
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        if (activity != null) {
            activity.finish();
            activity = null;
        }
    }

    /**
     * removeActivity.
     *
     * @param activity activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null && null != activityStack) {
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * finishAllActivity.
     */
    public void finishAllActivity() {
        if (null != activityStack) {
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (null != activityStack.get(i)) {
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
        }
    }

    /**
     * AppExit.
     *
     * @param context context
     */
    public static void exit(Context context) {
        SharedPreferences.Editor editor = App.me().getSharedPreferences().edit();
        editor.clear();
        editor.apply();

//        Intent intent = new Intent();
//        intent.setAction(LocService.STOP_LOC);
//        intent.setPackage(context.getPackageName());
//        context.startService(intent);

        AppManager.getAppManager().finishAllActivity();
        System.exit(0);
    }

}
