package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.Order;
import com.yuangee.flower.customer.entity.OrderWares;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.util.DisplayUtil;
import com.yuangee.flower.customer.util.TimeUtil;
import com.yuangee.flower.customer.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/10.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private Context context;
    private List<Order> data;

    private OnItemClickListener mOnItemClickListener;   //声明监听器接口

    public interface OnItemClickListener {
        /**
         * 点击事件的处理
         *
         * @param view     :item中设置了监听的view
         * @param position :点击了item的位置
         */
        void onItemClick(View view, int position);
    }

    /**
     * 通过adapter设置监听器
     *
     * @param mOnItemClickListener 监听器的接口类型
     */
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public OrderAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    @Override
    public OrderAdapter.OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.orders_item, null);
        OrderHolder holder = new OrderHolder(view);

        holder.root = view;    //将布局view保存起来用作点击事件
        holder.img1 = view.findViewById(R.id.img_1);
        holder.img2 = view.findViewById(R.id.img_2);
        holder.img3 = view.findViewById(R.id.img_3);
        holder.img4 = view.findViewById(R.id.img_4);
        holder.img5 = view.findViewById(R.id.img_5);
        holder.leftBtn = view.findViewById(R.id.left_btn);
        holder.rightBtn = view.findViewById(R.id.right_btn);
        holder.tvGoodsKind = view.findViewById(R.id.order_goods_kind);
        holder.tvOrderMoney = view.findViewById(R.id.order_fee);
        holder.tvOrderStatus = view.findViewById(R.id.order_status);
        holder.tvOrderTime = view.findViewById(R.id.order_time);

        return holder;
    }

    public void setData(List<Order> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    //设置数据
    @Override
    public void onBindViewHolder(OrderAdapter.OrderHolder holder, final int position) {

        final Order bean = data.get(position);
        String kindStr = "共<font color='#52A436'>" + bean.orderWaresList != null ? "" + bean.orderWaresList.size() : "0" + "</font>类商品";
        holder.tvGoodsKind.setText(kindStr);
        holder.tvOrderTime.setText("下单时间：" + bean.created);
        holder.tvOrderStatus.setText(bean.getStatusStr());
        holder.tvOrderMoney.setText("¥" + bean.payable + "(不包含运费)");

        if (null != bean.orderWaresList) {
            for (int i = 0; i < bean.orderWaresList.size(); i++) {
                OrderWares wares = bean.orderWaresList.get(i);
                if (i == 0) {
                    loadImg(holder.img1, wares.image, context);
                } else if (i == 1) {
                    loadImg(holder.img2, wares.image, context);
                } else if (i == 2) {
                    loadImg(holder.img3, wares.image, context);
                } else if (i == 3) {
                    loadImg(holder.img4, wares.image, context);
                } else if (i == 4) {
                    loadImg(holder.img5, wares.image, context);
                }
            }
        }

        if (bean.status == 0) {
            holder.leftBtn.setVisibility(View.VISIBLE);
            holder.rightBtn.setVisibility(View.VISIBLE);
            holder.leftBtn.setText("去支付");
            holder.rightBtn.setText("取消订单");
            holder.leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payOrder(bean);
                }
            });
            holder.rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelOrder(bean);
                }
            });
        } else if (bean.status == 1) {
            holder.leftBtn.setVisibility(View.VISIBLE);
            holder.rightBtn.setVisibility(View.GONE);
            holder.leftBtn.setText("提醒发货");
            holder.leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showMessage(context,"提醒卖家发货成功，商品将很快送到你手中");
                }
            });
        } else if (bean.status == 2) {
            holder.leftBtn.setVisibility(View.VISIBLE);
            holder.rightBtn.setVisibility(View.GONE);
            holder.leftBtn.setText("确认收货");
            holder.leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmOrder(bean);
                }
            });
        } else if (bean.status == 3) {
            holder.leftBtn.setVisibility(View.GONE);
            holder.rightBtn.setVisibility(View.GONE);
        } else if (bean.status == 4) {
            holder.leftBtn.setVisibility(View.VISIBLE);
            holder.rightBtn.setVisibility(View.VISIBLE);
            holder.leftBtn.setText("去支付");
            holder.rightBtn.setText("取消订单");
            holder.leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payOrder(bean);
                }
            });
            holder.rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelOrder(bean);
                }
            });
        } else if (bean.status == 5) {
            holder.leftBtn.setVisibility(View.GONE);
            holder.rightBtn.setVisibility(View.GONE);
        }
        //给该item设置一个监听器
        if (mOnItemClickListener != null) {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
        }
    }

    private void payOrder(Order order){

    }

    private void cancelOrder(Order order){

    }

    private void confirmOrder(Order order){

    }

    private void loadImg(ImageView imageView, String path, Context context) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(Config.BASE_URL + path)
                .apply(options)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    //自定义holder
    class OrderHolder extends RecyclerView.ViewHolder {

        public OrderHolder(View itemView) {
            super(itemView);
        }

        View root;
        TextView tvOrderTime;
        TextView tvOrderStatus;
        TextView tvGoodsKind;
        TextView tvOrderMoney;
        Button rightBtn;
        Button leftBtn;
        ImageView img1;
        ImageView img2;
        ImageView img3;
        ImageView img4;
        ImageView img5;
    }

}
