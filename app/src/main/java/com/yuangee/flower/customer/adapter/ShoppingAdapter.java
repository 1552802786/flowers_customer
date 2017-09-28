package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.Goods;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/10.
 */
public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.GoodsHolder> {

    private Context context;
    private List<Goods> data;

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

    public ShoppingAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    @Override
    public GoodsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.shopping_car_item, null);
        GoodsHolder holder = new GoodsHolder(view);

        holder.layoutView = view;    //将布局view保存起来用作点击事件

        holder.goodsImg = view.findViewById(R.id.goods_img);
        holder.goodsName = view.findViewById(R.id.goods_name);
        holder.goodsMoney = view.findViewById(R.id.goods_money);
        holder.goodsPrice = view.findViewById(R.id.goods_price);
        holder.removeFromCar = view.findViewById(R.id.delete_con);
        holder.numSub = view.findViewById(R.id.num_sub);
        holder.numAdd = view.findViewById(R.id.num_add);
        holder.goodsNum = view.findViewById(R.id.goods_num);

        return holder;
    }

    public void setData(List<Goods> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    private void setBtnEnable(GoodsHolder holder, Goods bean) {
        if (bean.selectedNum == 0) {
            holder.numSub.setEnabled(false);
            holder.numAdd.setEnabled(true);
        } else if (bean.selectedNum == 10) {
            holder.numSub.setEnabled(true);
            holder.numAdd.setEnabled(false);
        } else {
            holder.numSub.setEnabled(true);
            holder.numAdd.setEnabled(true);
        }
        holder.goodsMoney.setText("总计：" + bean.goodsPrice * bean.selectedNum + "元");
    }

    //设置数据
    @Override
    public void onBindViewHolder(final GoodsHolder holder, final int position) {

        final Goods bean = data.get(position);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(bean.imgPath)
                .apply(options)
                .into(holder.goodsImg);

        holder.goodsName.setText(bean.goodsName);
        holder.goodsPrice.setText(bean.goodsMoney);
        holder.goodsMoney.setText("总计：" + bean.goodsPrice * bean.selectedNum + "元");

        holder.goodsNum.setText(bean.selectedNum + "");

        setBtnEnable(holder, bean);

        holder.removeFromCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.numSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bean.selectedNum--;
                setBtnEnable(holder, bean);
                holder.goodsNum.setText("" + bean.selectedNum);
            }
        });

        holder.numAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bean.selectedNum++;
                setBtnEnable(holder, bean);
                holder.goodsNum.setText("" + bean.selectedNum);
            }
        });

        //给该item设置一个监听器
        if (mOnItemClickListener != null) {
            holder.layoutView.setOnClickListener(new View.OnClickListener() {
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
    class GoodsHolder extends RecyclerView.ViewHolder {

        GoodsHolder(View itemView) {
            super(itemView);
        }

        View layoutView;
        ImageView goodsImg;
        TextView goodsName;
        FrameLayout removeFromCar;
        TextView goodsMoney;//总计
        TextView goodsPrice;//单价
        ImageView numSub;
        ImageView numAdd;
        TextView goodsNum;
    }

}
