package com.yuangee.flower.customer.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.victor.loading.newton.NewtonCradleLoading;
import com.victor.loading.rotate.RotateLoading;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.util.StringUtils;

/**
 * 加载框
 */
public class RxProgressHUD extends Dialog {

    public RxProgressHUD(Context context) {
        super(context);
    }

    public RxProgressHUD(Context context, int theme) {
        super(context, theme);
    }


    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            findViewById(R.id.message).setVisibility(View.VISIBLE);
            TextView txt = (TextView) findViewById(R.id.message);
            txt.setText(message);
            txt.invalidate();
        }
    }

    public void dismiss() {
        RotateLoading imageView = findViewById(R.id.spinnerImageView);
        imageView.stop();
        super.dismiss();
    }

    public void show() {

        RotateLoading imageView = findViewById(R.id.spinnerImageView);
        imageView.start();

        super.show();
    }

    public static class Builder {
        private String title;
        private String message;
        private Context context;
        private OnDismissListener dismissListener;
        private boolean cancelable;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener dismissListener) {
            this.dismissListener = dismissListener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public RxProgressHUD create() {
            RxProgressHUD dialog = new RxProgressHUD(context, R.style.ProgressHUD);
            if (StringUtils.isEmpty(title)) {
                dialog.setTitle("");
            }
            dialog.setContentView(R.layout.progress_hud);
            if (message == null || message.length() == 0) {
                dialog.findViewById(R.id.message).setVisibility(View.GONE);
            } else {
                TextView txt = (TextView) dialog.findViewById(R.id.message);
                txt.setText(message);
            }
            dialog.setCancelable(cancelable);
            dialog.setOnDismissListener(dismissListener);
            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.2f;
            dialog.getWindow().setAttributes(lp);
            return dialog;
        }
    }

}
