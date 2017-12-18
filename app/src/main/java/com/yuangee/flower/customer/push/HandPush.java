package com.yuangee.flower.customer.push;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.FlowerApp;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.MainActivity;
import com.yuangee.flower.customer.activity.MessageActivity;
import com.yuangee.flower.customer.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/7/13 0013.
 */

public class HandPush {
    private long[] vir = {0, 500, 100, 500, 1000, 500, 100, 500};

    public HandPush(Context context, String jsonStr) {
        JSONObject customJson;
        try {
            customJson = new JSONObject(jsonStr);
            String type = null;
            String msg = null;
            JSONObject data = null;
            Log.e("pushData", customJson.toString());
            if (!customJson.isNull("type")) {
                type = customJson.getString("type");
            }
            if (!customJson.isNull("message")) {
                msg = customJson.getString("message");
            }
            if (!customJson.isNull("data")) {
                data = customJson.getJSONObject("data");
            }

            detailPush(context, type, msg, data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void detailPush(Context context, String type, String message, JSONObject data) {

        boolean login = App.me().getSharedPreferences().getBoolean("login", false);
        if (!login) {
            return;
        }
//        if (type.equals("new_order")) {
//            //抢单
        Utils.vibrate(context, false);
        showNotify(context, "", "新通知", "您有新的通知，请前往应用查看", MessageActivity.class);
//        }
    }

    private void showNotify(Context context, String tips, String title,
                            String content, Class clz) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) FlowerApp
                .getInstance().getSystemService(ns);

        Intent notificationIntent = new Intent(context, clz);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
                FlowerApp.getInstance(), 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                FlowerApp.getInstance());
        builder.setSmallIcon(R.drawable.ic_menu_flower_white);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder.setTicker(tips);
        builder.setColor(context.getResources().getColor(R.color.colorPrimary));
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setContentIntent(contentIntent);
        builder.setVibrate(vir);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);

        Notification notification = builder.build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1, notification);
    }
}
