package com.yuangee.flower.customer.fragment;

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

public class HomeGoodShopSelection extends StatelessSection {

    private List<Recommend> recommends;

    private OnItemClickListener mOnItemClickListener;   //声明监听器接口

    private Context mContext;

    public HomeGoodShopSelection(List<Recommend> recommends, Context mContext, OnItemClickListener mOnItemClickListener) {
        super(R.layout.home_recommed_header, R.layout.good_shop_item);
        this.recommends = recommends;
        this.mContext = mContext;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return recommends.size() > 4 ? 2 : (int) Math.ceil(recommends.size() / 2);
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
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int pos) {
        RecommendHolder orderHolder = (RecommendHolder) holder;
        final Recommend bean = recommends.get(pos);
        RequestOptions options = new RequestOptions()
                .centerCrop()
//                .transform(new GlideRoundTransform(mContext, 8,120))
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(mContext)
                .load(Config.BASE_URL + recommends.get(2 * pos).image)
                .apply(options)
                .into(orderHolder.imageView2);
        orderHolder.title2.setText(recommends.get(2 * pos).title);
        orderHolder.subtitle2.setText(recommends.get(2 * pos).depict);
        orderHolder.relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, 2 * pos);
            }
        });
        if (recommends.size() > (2 * pos + 1)) {

            Glide.with(mContext)
                    .load(Config.BASE_URL + recommends.get(2 * pos + 1).image)
                    .apply(options)
                    .into(orderHolder.imageView3);
            orderHolder.title3.setText(recommends.get(2 * pos + 1).title);
            orderHolder.subtitle3.setText(recommends.get(2 * pos + 1).depict);
            orderHolder.relativeLayout3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, 2 * pos + 1);
                }
            });

        }
    }


    class HearderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;

        public HearderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //自定义holder
    class RecommendHolder extends RecyclerView.ViewHolder {


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

        public RecommendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
