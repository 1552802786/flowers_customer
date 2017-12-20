package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.OrderWare;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2017/12/4.
 */

public class OrderWareAdapter extends RecyclerView.Adapter<OrderWareAdapter.Holder> {

    private List<OrderWare> orderWares;

    private Context context;

    public OrderWareAdapter(Context context) {
        this.context = context;
        orderWares = new ArrayList<>();
    }

    public void setOrderWares(List<OrderWare> orderWares) {
        this.orderWares = orderWares;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_ware_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        OrderWare orderWare = orderWares.get(position);
        if(orderWare.wares != null){
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.color.color_f6)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context)
                    .load(Config.BASE_URL + orderWare.wares.image)
                    .apply(options)
                    .into(holder.img);
            holder.goodsName.setText(orderWare.wares.name);
            holder.goodsPrice.setText("¥" + orderWare.wares.unitPrice + "元/扎");
        }
        holder.goodsTotalFee.setText("总价：" +
                (orderWare.total == null ? 0.0 : orderWare.total.doubleValue()) + "元");
        holder.goodsNum.setText("x" + orderWare.quantity);
    }

    @Override
    public int getItemCount() {
        return orderWares.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView goodsName;
        TextView goodsPrice;
        TextView goodsTotalFee;
        TextView goodsNum;

        public Holder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.goods_img);
            goodsName = itemView.findViewById(R.id.goods_name);
            goodsPrice = itemView.findViewById(R.id.goods_price);
            goodsTotalFee = itemView.findViewById(R.id.total_fee);
            goodsNum = itemView.findViewById(R.id.goods_number);
        }
    }
}
