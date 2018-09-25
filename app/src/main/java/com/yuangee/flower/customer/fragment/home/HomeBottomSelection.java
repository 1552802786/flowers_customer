package com.yuangee.flower.customer.fragment.home;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.GoodsAdapter;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.widget.SyLinearLayoutManager;
import com.yuangee.flower.customer.widget.sectioned.StatelessSection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by developerLzh on 2017/10/18 0018.
 */

public class HomeBottomSelection extends StatelessSection {

    private List<Recommend> recommends;

    private GoodsAdapter adapter;   //声明监听器接口

    private Context mContext;

    public HomeBottomSelection( Context mContext, GoodsAdapter adapter) {
        super(R.layout.bottom_section_layout, R.layout.layout_home_recommend_empty);
        this.mContext = mContext;
        this.adapter = adapter;
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new RecommendHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HearderHolder holder1 = (HearderHolder) holder;
        holder1.title.setText("更多好货");
        LinearLayoutManager manager = new SyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        holder1.list.setLayoutManager(manager);
        holder1.list.setAdapter(adapter);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HearderHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        RecommendHolder orderHolder = (RecommendHolder) holder;


    }

    static class HearderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.bottom_list)
        RecyclerView list;

        public HearderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //自定义holder
    static class RecommendHolder extends RecyclerView.ViewHolder {




        public RecommendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
