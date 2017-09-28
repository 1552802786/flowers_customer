package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.util.DisplayUtil;
import com.yuangee.flower.customer.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/10.
 */
public class OrderAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Recommend> data;

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.order_adapter, null);
        OrderHolder holder = new OrderHolder(view);

        holder.layoutView = view;    //将布局view保存起来用作点击事件

        holder.relativeLayout = view.findViewById(R.id.rel_layout);

        ImageView imageView = new ImageView(context);

        int width = DisplayUtil.getScreenWidth(context) / 2 - DisplayUtil.dp2px(context, 15);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(width, width);

        holder.relativeLayout.addView(imageView, layoutParams);

        holder.imageView = imageView;

        return holder;
    }

    public void setData(List<Recommend> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    //设置数据
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        OrderHolder orderHolder = (OrderHolder) holder;
        final Recommend bean = data.get(position);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(bean.imgPath)
                .apply(options)
                .into(orderHolder.imageView);

        //给该item设置一个监听器
        if (mOnItemClickListener != null) {
            orderHolder.layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
        }
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

        ImageView imageView;
        RelativeLayout relativeLayout;
        View layoutView;

    }

}
