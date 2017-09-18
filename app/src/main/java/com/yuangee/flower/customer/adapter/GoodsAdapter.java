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
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.fragment.shopping.ShoppingContract;
import com.yuangee.flower.customer.util.DisplayUtil;
import com.yuangee.flower.customer.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/10.
 */
public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsHolder> {

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

    public GoodsAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    @Override
    public GoodsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.goods_item, null);
        GoodsHolder holder = new GoodsHolder(view);

        holder.layoutView = view;    //将布局view保存起来用作点击事件

        holder.goodsImg = view.findViewById(R.id.goods_img);
        holder.goodsName = view.findViewById(R.id.goods_name);
        holder.goodsGrade = view.findViewById(R.id.goods_grade);
        holder.goodsColor = view.findViewById(R.id.goods_color);
        holder.goodsSpec = view.findViewById(R.id.goods_spec);
        holder.goodsLeft = view.findViewById(R.id.goods_left);
        holder.addToCar = view.findViewById(R.id.add_to_car);
        holder.goodsMoney = view.findViewById(R.id.goods_money);
        holder.numSub = view.findViewById(R.id.num_sub);
        holder.numAdd = view.findViewById(R.id.num_add);
        holder.goodsNum = view.findViewById(R.id.goods_num);

        return holder;
    }

    public void setData(List<Goods> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    //设置数据
    @Override
    public void onBindViewHolder(final GoodsHolder holder, final int position) {

        final Goods bean = data.get(position);

        Glide.with(context)
                .load(bean.imgPath)
                .centerCrop()
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.goodsImg);
        holder.goodsName.setText(bean.goodsName);
        holder.goodsGrade.setText("等级：" + bean.goodsGrade);
        holder.goodsColor.setText("颜色：" + bean.goodsColor);
        holder.goodsSpec.setText("规格：" + bean.goodsSpec + "cm");
        holder.goodsLeft.setText("可售量：" + bean.goodsLeft);

        holder.addToCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bean.isAddToCar = true;
            }
        });

        holder.numSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bean.selectedNum > 1) {
                    bean.selectedNum--;
                    if (bean.selectedNum == 1) {
                        holder.numSub.setEnabled(false);
                    } else {
                        holder.numSub.setEnabled(true);
                    }
                }
                holder.goodsNum.setText(bean.selectedNum);
            }
        });

        holder.numAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bean.selectedNum < 10) {
                    bean.selectedNum++;
                    if (bean.selectedNum == 10) {
                        holder.numAdd.setEnabled(false);
                    } else {
                        holder.numAdd.setEnabled(true);
                    }
                }
                holder.goodsNum.setText(bean.selectedNum);
            }
        });

        holder.goodsMoney.setText(bean.goodsMoney);

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

        public GoodsHolder(View itemView) {
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
    }

}
