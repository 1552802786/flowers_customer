package com.yuangee.flower.customer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者：Rookie on 2018/11/5 15:57
 */
public class UserScoreAdapter extends RecyclerView.Adapter<UserScoreAdapter.ShowHolder> {
    @Override
    public ShowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ShowHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ShowHolder extends RecyclerView.ViewHolder {

        public ShowHolder(View itemView) {
            super(itemView);
        }
    }
}
