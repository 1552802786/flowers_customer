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
import android.widget.TextView;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.CouponAdapter;
import com.yuangee.flower.customer.entity.CouponEntity;

import java.util.List;

/**
 * 规则弹窗
 * Created by xl on 2017/11/16.
 */

public class CouponDialog extends Dialog {


    private ListView view;
    private Context mContext;
    List<CouponEntity> datas;

    public CouponDialog(Context context, int themeResId, List<CouponEntity> datas) {
        super(context, themeResId);
        this.mContext = context;
        this.datas = datas;
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
            return datas.size();
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
            Holder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_coupon_index, null);
                holder = new Holder(view);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            CouponEntity coupon = datas.get(i);
            holder.price.setText(coupon.money);
            if (coupon.hasGet) {
                holder.getBtn.setEnabled(false);
                holder.getBtn.setText("已领取");
                holder.getBtn.setBackgroundResource(R.drawable.corners_color_gray);
            } else {
                holder.getBtn.setBackgroundResource(R.drawable.corners_color_red);
                holder.getBtn.setEnabled(false);
                holder.getBtn.setText("未领取");
            }
            return view;
        }
    }

    class Holder {
        TextView price;
        TextView getBtn;
        TextView desc;
        public Holder(View view) {
            price = view.findViewById(R.id.price_coupon);
            getBtn = view.findViewById(R.id.get_now);
            desc = view.findViewById(R.id.coupon_desc);
        }
    }
}