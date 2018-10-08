package com.yuangee.flower.customer.fragment.home;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunfusheng.marqueeview.MarqueeView;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.TypeAdapter;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.widget.AutoMarqueeTextView;
import com.yuangee.flower.customer.widget.SpaceItemDecoration;
import com.yuangee.flower.customer.widget.sectioned.StatelessSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by developerLzh on 2017/10/18 0018.
 */

public class HomeBigGenreSelection extends StatelessSection {

    private Context mContext;
    private List<String> infoStr=new ArrayList<>();

    public HomeBigGenreSelection(List<String> info, Context mContext) {
        super(R.layout.type_con, R.layout.layout_home_recommend_empty);
        this.mContext = mContext;
        infoStr.add("红玫瑰今日竞拍9.9元起");
        infoStr.add("新货上架，芍药上架，市场新低");
        infoStr.add("端午节当季花卉活动");
        infoStr.add("六一儿童节，鲜花促销活动");

    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new EmptyViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new BigGenerHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        final BigGenerHolder holder1 = (BigGenerHolder) holder;
        holder1.infoTv.startWithList(infoStr);
        Collections.reverse(infoStr);
        holder1.infoTv1.startWithList(infoStr);

    }

    //自定义holder
    static class BigGenerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.buy_now)
        View buy;
        @BindView(R.id.jingpai)
        View jinpai;
        @BindView(R.id.dazong)
        View dazong;
        @BindView(R.id.yuding)
        View yuding;
        @BindView(R.id.lingjuan)
        View lingjuan;
        @BindView(R.id.tv_notice)
        MarqueeView infoTv;
        @BindView(R.id.tv_notice1)
        MarqueeView infoTv1;

        public BigGenerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
