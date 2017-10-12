package com.yuangee.flower.customer.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.yuangee.flower.customer.R;

/**
 * Created by developerLzh on 2017/10/11 0011.
 */

public class PhoneUtil {
    public static void hideKeyboard(Activity paramActivity) {

        InputMethodManager localInputMethodManager = (InputMethodManager) paramActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View localView = paramActivity.getCurrentFocus();
        if (localView != null) {
            IBinder localIBinder = localView.getWindowToken();
            if (localIBinder != null)
                localInputMethodManager
                        .hideSoftInputFromWindow(localIBinder, 0);
        }
    }

    /**
     * 拨打电话
     *
     * @param phoneNum 电话号码
     */
    public static void call(Context context, String phoneNum) {

        try {
            Intent phoneIntent = new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:" + phoneNum));
            context.startActivity(phoneIntent);
        } catch (SecurityException e) {
            e.printStackTrace();
            ToastUtil.showMessage(context, "拨打电话失败");
        }

    }
}
