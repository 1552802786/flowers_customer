package com.yuangee.flower.customer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by developerLzh on 2017/10/27 0027.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressHolder> {

    @Override
    public AddressHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(AddressHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    //自定义holder
    class AddressHolder extends RecyclerView.ViewHolder {

       public AddressHolder(View itemView) {
            super(itemView);
        }

        View layoutView;
        ImageView goodsImg;
        TextView goodsName;
        TextView goodsGrade;
        TextView goodsColor;
        TextView goodsSpec;
        TextView goodsLeft;
        ImageView addToCar;
        TextView goodsMoney;
        ImageView numSub;
        ImageView numAdd;
        TextView goodsNum;
        TextView yuYue;
    }
}
