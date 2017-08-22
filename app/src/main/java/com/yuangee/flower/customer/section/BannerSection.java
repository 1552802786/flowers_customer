package com.yuangee.flower.customer.section;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.widget.sectioned.StatelessSection;

import java.util.List;

/**
 * Created by developerLzh on 2017/8/22 0022.
 */

public class BannerSection extends StatelessSection {
    public BannerSection(int itemResourceId) {
        super(itemResourceId);
    }

//    private List<BannerEntity> banners;
//    public BannerSection(List<BannerEntity> banners) {
//        super(R.layout.layout_banner, R.layout.layout_home_recommend_empty);
//        this.banners = banners;
//    }

    @Override
    public int getContentItemsTotal() {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return null;
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

    }
}
