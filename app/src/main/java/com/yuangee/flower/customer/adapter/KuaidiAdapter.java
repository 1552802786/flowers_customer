package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.yuangee.flower.customer.entity.Express;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/21.
 */

public class KuaidiAdapter extends RecyclerView.Adapter<KuaidiAdapter.ViewHolder>{

    private Context context;
    private List<Express> expressList;

    public KuaidiAdapter(Context context) {
        this.context = context;
        expressList = new ArrayList<>();
    }

    public void setExpressList(List<Express> expressList) {
        this.expressList = expressList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RadioButton view = new RadioButton(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(RadioButton itemView) {
            super(itemView);
        }
    }
}
