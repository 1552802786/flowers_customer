package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.Coupon;
import com.yuangee.flower.customer.entity.Express;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/21.
 */

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {

    private Context context;
    private List<Coupon> coupons;

    private Coupon clickedCoupon;

    private List<RadioButton> buttons;

    public CouponAdapter(Context context) {
        this.context = context;
        buttons = new ArrayList<>();
        coupons = new ArrayList<>();
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
        notifyDataSetChanged();
    }

    private double orderMoney;

    public void setOrderMoney(double orderMoney) {
        this.orderMoney = orderMoney;
        buttons.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Coupon coupon = coupons.get(position);

        String moneyText = coupon.money + "元优惠券";
        if (coupon.couponFullMoney > 0) {
            holder.couponFullMoney.setVisibility(View.VISIBLE);
            holder.couponFullMoney.setText("(满" + coupon.couponFullMoney + "元可使用)");
        } else {
            holder.couponFullMoney.setVisibility(View.GONE);
        }
        String timeText = "截止日期：" + coupon.becomeDueTime;

        holder.couponMoney.setText(moneyText);
        holder.couponTime.setText(timeText);

        if (orderMoney > coupon.couponFullMoney) {
            buttons.add(holder.button);
            holder.root.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.root.setEnabled(true);
            holder.button.setEnabled(true);
        } else {
            holder.root.setBackgroundColor(context.getResources().getColor(R.color.gray_light));
            holder.root.setEnabled(false);
            holder.button.setEnabled(false);
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAllButton();
                holder.button.setChecked(true);
                clickedCoupon = coupon;
            }
        });
    }

    public Coupon getClicked() {
        return clickedCoupon;
    }

    private void resetAllButton() {
        for (RadioButton button : buttons) {
            button.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton button;
        TextView couponMoney;
        TextView couponTime;
        TextView couponFullMoney;
        View root;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            button = itemView.findViewById(R.id.coupon_button);
            couponMoney = itemView.findViewById(R.id.coupon_money);
            couponTime = itemView.findViewById(R.id.coupon_time);
            couponFullMoney = itemView.findViewById(R.id.coupon_full_money);
        }
    }
}
