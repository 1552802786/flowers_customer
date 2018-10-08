package com.yuangee.flower.customer.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.CouponAdapter;
import com.yuangee.flower.customer.entity.CouponEntity;

/**
 * 规则弹窗
 * Created by xl on 2017/11/16.
 */

public class CouponDialog extends Dialog {


    private ListView view;
    private Context mContext;

    public CouponDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_dialog);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        view = findViewById(R.id.coupon_list);
        UserAdapter adapter = new UserAdapter();
        view.setAdapter(adapter);
    }

    private class UserAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CouponEntity getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_coupon_index, null);
            }
            return view;
        }
    }

    class Holder {

    }
}