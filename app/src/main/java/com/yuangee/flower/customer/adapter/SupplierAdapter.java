package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.GoodsActivity;
import com.yuangee.flower.customer.activity.LoginActivity;
import com.yuangee.flower.customer.entity.CartItem;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.RxManager;
import com.yuangee.flower.customer.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/3/10.
 */
public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.GoodsHolder> {

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

    private RxManager rxManager;

    private long shopId;
    private String shopName;

    public SupplierAdapter(Context context, RxManager rxManager,long shopId,String shopName) {
        this.context = context;
        this.rxManager = rxManager;
        this.shopId = shopId;
        this.shopName = shopName;
        data = new ArrayList<>();
    }

    @Override
    public GoodsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.supplier_goods_item, null);
        GoodsHolder holder = new GoodsHolder(view);

        holder.layoutView = view;    //将布局view保存起来用作点击事件

        holder.goodsImg = view.findViewById(R.id.goods_img);
        holder.goodsName = view.findViewById(R.id.goods_name);
        holder.goodsGrade = view.findViewById(R.id.goods_grade);
        holder.goodsColor = view.findViewById(R.id.goods_color);
        holder.goodsSpec = view.findViewById(R.id.goods_spec);
        holder.goodsLeft = view.findViewById(R.id.goods_left);
        holder.goodsMoney = view.findViewById(R.id.goods_money);
        holder.changeGoods = view.findViewById(R.id.change);
        holder.deleteGoodsp = view.findViewById(R.id.delete);
//        holder.goodsNum = view.findViewById(R.id.goods_num);

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

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(Config.BASE_URL + bean.image)
                .apply(options)
                .into(holder.goodsImg);

        holder.goodsName.setText(bean.name);
        holder.goodsGrade.setText("等级：" + bean.grade);
        holder.goodsColor.setText("颜色：" + bean.color);
        holder.goodsSpec.setText("规格：" + bean.spec);
        holder.goodsLeft.setText("可售量：" + bean.salesVolume);
//        holder.goodsNum.setText(bean.selectedNum + "");

        holder.goodsMoney.setText(bean.unitPrice + "/" + bean.unit);

        holder.changeGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GoodsActivity.class);
                intent.putExtra("goods", bean);
                intent.putExtra("change", true);
                intent.putExtra("shopId", shopId);
                intent.putExtra("shopName", shopName);
                context.startActivity(intent);
            }
        });

        holder.deleteGoodsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<Object> observable = ApiManager.getInstance().api
                        .deleteWares(bean.id)
                        .map(new HttpResultFunc<>(context))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

                rxManager.add(observable.subscribe(new MySubscriber<>(context, true, false, new NoErrSubscriberListener<Object>() {
                    @Override
                    public void onNext(Object o) {
                        data.remove(position);
                        notifyDataSetChanged();
                    }
                })));

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
        TextView goodsGrade;
        TextView goodsColor;
        TextView goodsSpec;
        TextView goodsLeft;
        TextView goodsMoney;
//        TextView goodsNum;

        TextView changeGoods;
        TextView deleteGoodsp;
    }

}
