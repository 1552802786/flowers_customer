package com.yuangee.flower.customer.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.yuangee.flower.customer.activity.SplashActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 捕获全局异常捕获并输出成文件
 * <p>Created by timor on 2014/11/27.</p>
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static CrashHandler crashHandler;
    private UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    private Map<String, String> info = new HashMap<String, String>();

    private CrashHandler(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashHandler getInstance(Context context) {
        if (crashHandler == null) {
            crashHandler = new CrashHandler(context);
        }
        return crashHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            Intent intent = new Intent(mContext,SplashActivity.class);
            @SuppressLint("WrongConstant") PendingIntent restartIntent = PendingIntent.getActivity(
                    mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
            // 退出程序
            AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,restartIntent); // 1秒钟后重启应用

        }

    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        new Thread() {
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉，程序出现异常，即将退出。", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        collectDeviceInfo(mContext);// 采集设备信息
        saveCrashInfo(ex);// 保存异常
        return true;
    }

    /**
     * 采集设备信息
     *
     * @param context
     */
    private void collectDeviceInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);

            if (pi != null) {
                info.put("versionName", pi.versionName == null ? "null" : pi.versionName);
                info.put("versionCode", pi.versionCode + "");
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                info.put(field.getName(), field.get(null).toString() + "\n");
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存异常信息
     *
     * @param ex
     */
    private void saveCrashInfo(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key);
            sb.append(":");
            sb.append(value);
            sb.append("\r\n\r\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        sb.append("**********************exception info********************************\r\n");
        String[] result = writer.toString().split("	");
        for (String string : result) {
            sb.append(string);
            sb.append("\r\n");
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String time = sdf.format(new Date());
            String fileName = "crash_" + time + ".log";

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                File dir = new File(path + "/Flower");
                if (!dir.exists()) dir.mkdirs();

                File file = new File(dir, fileName);
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            // sendCrashReportsToServer(mContext);//发送异常
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
    }
}
