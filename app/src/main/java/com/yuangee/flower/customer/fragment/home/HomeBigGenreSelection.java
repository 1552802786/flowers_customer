package com.yuangee.flower.customer.fragment.home;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.adapter.TypeAdapter;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.widget.SpaceItemDecoration;
import com.yuangee.flower.customer.widget.sectioned.StatelessSection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by developerLzh on 2017/10/18 0018.
 */

public class HomeBigGenreSelection extends StatelessSection {
    private List<Genre> genreList;

    private OnItemClickListener mOnItemClickListener;   //声明监听器接口

    private Context mContext;

    public HomeBigGenreSelection(List<Genre> genreList, Context mContext, OnItemClickListener mOnItemClickListener) {
        super(R.layout.type_con, R.layout.layout_home_recommend_empty);
        this.genreList = genreList;
        this.mContext = mContext;
        this.mOnItemClickListener = mOnItemClickListener;
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
        TypeAdapter typeAdapter = new TypeAdapter(mContext);
        RecyclerView.LayoutManager horManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        holder1.recyclerView.setLayoutManager(horManager);
        holder1.recyclerView.setAdapter(typeAdapter);
        holder1.recyclerView.addItemDecoration(new SpaceItemDecoration(0, 0, 30, 30, LinearLayout.HORIZONTAL));

        holder1.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                showOrHideRow(recyclerView, holder1);
            }
        });
        typeAdapter.setData(genreList);
        typeAdapter.setOnItemClickListener(mOnItemClickListener);
    }

    //自定义holder
    static class BigGenerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.type_recycler)
        RecyclerView recyclerView;

        @BindView(R.id.left_row)
        ImageView leftRow;

        @BindView(R.id.right_row)
        ImageView rightRow;

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

    public void showOrHideRow(RecyclerView recyclerView, BigGenerHolder holder) {
        boolean isBottom = !recyclerView.canScrollHorizontally(1);//返回false不能往右滑动，即代表到最右边了
        boolean isTop = !recyclerView.canScrollHorizontally(-1);//返回false不能往左滑动，即代表到最左边了
        if (isBottom) {
            holder.rightRow.setVisibility(View.INVISIBLE);
        } else {
            holder.rightRow.setVisibility(View.VISIBLE);
        }
        if (isTop) {
            holder.leftRow.setVisibility(View.INVISIBLE);
        } else {
            holder.leftRow.setVisibility(View.VISIBLE);
        }
    }
}
