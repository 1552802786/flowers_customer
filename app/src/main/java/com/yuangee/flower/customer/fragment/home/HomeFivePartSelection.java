package com.yuangee.flower.customer.fragment.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
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

public class HomeFivePartSelection extends StatelessSection {

    private List<Recommend> recommends;

    private OnItemClickListener mOnItemClickListener;   //声明监听器接口

    private Context mContext;

    public HomeFivePartSelection(List<Recommend> recommends, Context mContext, OnItemClickListener mOnItemClickListener) {
        super(R.layout.home_recommed_header, R.layout.five_recommend_item);
        this.recommends = recommends;
        this.mContext = mContext;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return 2;
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
        if (position==0){
            orderHolder.item1.setVisibility(View.GONE);
            orderHolder.relativeLayout.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(Config.BASE_URL + bean.image)
                    .apply(options)
                    .into(orderHolder.imageView);
            orderHolder.title.setText(bean.title);
            orderHolder.subtitle.setText(bean.depict);
        }else {
            orderHolder.item1.setVisibility(View.VISIBLE);
            orderHolder.relativeLayout.setVisibility(View.GONE);
            if (recommends.size() > 1) {
                Glide.with(mContext)
                        .load(Config.BASE_URL + recommends.get(1).image)
                        .apply(options)
                        .into(orderHolder.imageView2);
                orderHolder.title2.setText(recommends.get(1).title);
                orderHolder.subtitle2.setText(recommends.get(1).depict);
                orderHolder.relativeLayout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v, 1);
                    }
                });
            }
            if (recommends.size() > 2) {

                Glide.with(mContext)
                        .load(Config.BASE_URL + recommends.get(2).image)
                        .apply(options)
                        .into(orderHolder.imageView3);
                orderHolder.title3.setText(recommends.get(2).title);
                orderHolder.subtitle3.setText(recommends.get(2).depict);
                orderHolder.relativeLayout3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v, 2);
                    }
                });

            }
            if (recommends.size() > 3) {

                Glide.with(mContext)
                        .load(Config.BASE_URL + recommends.get(3).image)
                        .apply(options)
                        .into(orderHolder.imageView4);
                orderHolder.title4.setText(recommends.get(3).title);
                orderHolder.subtitle4.setText(recommends.get(3).depict);
                orderHolder.relativeLayout4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v, 3);
                    }
                });

            }
            if (recommends.size() > 4) {

                Glide.with(mContext)
                        .load(Config.BASE_URL + recommends.get(4).image)
                        .apply(options)
                        .into(orderHolder.imageView5);
                orderHolder.title5.setText(recommends.get(4).title);
                orderHolder.subtitle5.setText(recommends.get(4).depict);
                orderHolder.relativeLayout5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v, 4);
                    }
                });
            }
        }



        //给该item设置一个监听器
        if (mOnItemClickListener != null) {
            orderHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, 0);
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
        TextView subtitle;
        @BindView(R.id.rel_layout1)
        LinearLayout relativeLayout;

        @BindView(R.id.image_view2)
        RoundedImageView imageView2;
        @BindView(R.id.title2)
        TextView title2;
        @BindView(R.id.title_second2)
        TextView subtitle2;
        @BindView(R.id.rel_layout2)
        LinearLayout relativeLayout2;

        @BindView(R.id.image_view3)
        RoundedImageView imageView3;
        @BindView(R.id.title3)
        TextView title3;
        @BindView(R.id.title_second3)
        TextView subtitle3;
        @BindView(R.id.rel_layout3)
        LinearLayout relativeLayout3;

        @BindView(R.id.image_view4)
        RoundedImageView imageView4;
        @BindView(R.id.title4)
        TextView title4;
        @BindView(R.id.title_second4)
        TextView subtitle4;
        @BindView(R.id.rel_layout4)
        LinearLayout relativeLayout4;

        @BindView(R.id.image_view5)
        RoundedImageView imageView5;
        @BindView(R.id.title5)
        TextView title5;
        @BindView(R.id.title_second5)
        TextView subtitle5;
        @BindView(R.id.rel_layout5)
        LinearLayout relativeLayout5;
        @BindView(R.id.item1)
        LinearLayout item1;
        public RecommendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
