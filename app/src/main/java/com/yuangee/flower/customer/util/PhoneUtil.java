package com.yuangee.flower.customer.util;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
}
