package com.yuangee.flower.customer.fragment.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.widget.GlideRoundTransform;
import com.yuangee.flower.customer.widget.sectioned.StatelessSection;

import java.security.MessageDigest;
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
        super(R.layout.home_recommed_header, R.layout.recommend_item);
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
        HearderHolder holder1 = (HearderHolder) holder;
        holder1.title.setText(recommends.get(0).module);
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
//                .transform(new GlideRoundTransform(mContext, 8,120))
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(mContext)
                .load(Config.BASE_URL + bean.image)
                .apply(options)
                .into(orderHolder.imageView);
        orderHolder.title.setText(bean.title);
        orderHolder.title2.setText(bean.depict);
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

    static class HearderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;

        public HearderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //自定义holder
    static class RecommendHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.image_view)
        RoundedImageView imageView;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.title_second)
        TextView title2;
        @BindView(R.id.rel_layout)
        LinearLayout relativeLayout;

        public RecommendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
