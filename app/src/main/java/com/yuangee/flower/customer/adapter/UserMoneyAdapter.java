package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.UserMoneyEntity;
import com.yuangee.flower.customer.entity.UserScoreEntity;
import com.yuangee.flower.customer.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Rookie on 2018/11/5 15:57
 */
public class UserMoneyAdapter extends RecyclerView.Adapter<UserMoneyAdapter.ShowHolder> {
    public Context mContext;
    public List<UserMoneyEntity> datas = new ArrayList<>();

    public UserMoneyAdapter(Context context) {
        this.mContext = context;

    }

    public void setData(List<UserMoneyEntity> datas) {
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public ShowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShowHolder(LayoutInflater.from(mContext).inflate(R.layout.user_score_item, null));
    }

    @Override
    public void onBindViewHolder(ShowHolder holder, int position) {
        UserMoneyEntity entity = datas.get(position);
        holder.score.setText(String.valueOf(entity.money));
        holder.type1.setText(entity.meme);
        holder.type2.setText(entity.money > 0 ? ("+" + entity.money) : String.valueOf(entity.money));
        holder.time.setText(TimeUtil.getTime(TimeUtil.YMD_HMS, entity.payTime==null?0:entity.payTime));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ShowHolder extends RecyclerView.ViewHolder {
        public TextView time, score, type1, type2;

        public ShowHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.created_time);
            score = itemView.findViewById(R.id.score);
            type1 = itemView.findViewById(R.id.use_type);
            type2 = itemView.findViewById(R.id.use_type1);
        }
    }
}
