package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.UserScoreEntity;
import com.yuangee.flower.customer.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Rookie on 2018/11/5 15:57
 */
public class UserScoreAdapter extends RecyclerView.Adapter<UserScoreAdapter.ShowHolder> {
    public Context mContext;
    public List<UserScoreEntity> datas=new ArrayList<>();

    public UserScoreAdapter(Context context) {
        this.mContext = context;

    }

    public void setData(List<UserScoreEntity> datas) {
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public ShowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShowHolder(LayoutInflater.from(mContext).inflate(R.layout.user_score_item,null));
    }

    @Override
    public void onBindViewHolder(ShowHolder holder, int position) {
        UserScoreEntity entity=datas.get(position);
        holder.score.setText(entity.integral);
        holder.time.setText(TimeUtil.getTime(TimeUtil.YMD_HMS,entity.created));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ShowHolder extends RecyclerView.ViewHolder {
        public TextView time,score,type1,type2;
        public ShowHolder(View itemView) {
            super(itemView);
            time=itemView.findViewById(R.id.created_time);
            score=itemView.findViewById(R.id.score);
            type1=itemView.findViewById(R.id.use_type);
            type2=itemView.findViewById(R.id.use_type1);
        }
    }
}
