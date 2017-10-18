package com.yuangee.flower.customer.fragment.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.widget.sectioned.StatelessSection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by developerLzh on 2017/10/18 0018.
 */

public class HomeRecommedSelection extends StatelessSection {

    private List<Recommend> recommends;

    private OnItemClickListener mOnItemClickListener;   //声明监听器接口

    private Context mContext;

    public HomeRecommedSelection(List<Recommend> recommends, Context mContext, OnItemClickListener mOnItemClickListener) {
        super(R.layout.home_recommed_header, R.layout.order_adapter);
        this.recommends = recommends;
        this.mContext = mContext;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return recommends.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new RecommendHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HearderHolder headerViewHolder = (HearderHolder) holder;
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HearderHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        RecommendHolder orderHolder = (RecommendHolder) holder;
        final Recommend bean = recommends.get(position);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(mContext)
//                .load(Config.BASE_URL + bean.image)
                .load("http://ww4.sinaimg.cn/large/006uZZy8jw1faic259ohaj30ci08c74r.jpg")
                .apply(options)
                .into(orderHolder.imageView);

        //给该item设置一个监听器
        if (mOnItemClickListener != null) {
            orderHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
        }
    }

    static class HearderHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.title)
        TextView title;

        public HearderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    //自定义holder
    static class RecommendHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.image_view)
        ImageView imageView;

        @BindView(R.id.rel_layout)
        RelativeLayout relativeLayout;

        public RecommendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
