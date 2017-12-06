package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.entity.GenreSub;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.entity.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/10.
 */
public class Type3Adapter extends RecyclerView.Adapter {

    private Context context;
    private List<GenreSub> data;

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

    public Type3Adapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.type_item_3, null);
        OrderHolder holder = new OrderHolder(view);

        holder.layoutView = view;    //将布局view保存起来用作点击事件

        holder.titleStr = view.findViewById(R.id.title_str);

        return holder;
    }

    public void setData(List<GenreSub> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    //设置数据
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        OrderHolder orderHolder = (OrderHolder) holder;
        final GenreSub bean = data.get(position);
        orderHolder.titleStr.setText(bean.name);

        if (!bean.clicked) {
            orderHolder.titleStr.setBackgroundResource(R.drawable.corners_bg_white);
        } else {
            orderHolder.titleStr.setBackgroundResource(R.drawable.corners_bg_orange);
        }

        //给该item设置一个监听器
        if (mOnItemClickListener != null) {
            orderHolder.layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v,position);
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

        TextView titleStr;
        View layoutView;

    }

}
