package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.yuangee.flower.customer.entity.Express;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/21.
 */

public class KuaidiAdapter extends RecyclerView.Adapter<KuaidiAdapter.ViewHolder> {

    private Context context;
    private List<Express> expressList;

    private Express clickedExpress;

    private List<RadioButton> buttons;

    public KuaidiAdapter(Context context) {
        this.context = context;
        buttons = new ArrayList<>();
        expressList = new ArrayList<>();
    }

    public void setExpressList(List<Express> expressList) {
        this.expressList = expressList;
        buttons.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RadioButton view = new RadioButton(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        buttons.add(holder.button);
        final Express express = expressList.get(position);
        String text = express.expressDeliveryName + "(" + express.expressDeliveryMoney + "元)";
        holder.button.setText(text);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAllButton();
                holder.button.setChecked(true);
                clickedExpress = express;
            }
        });
    }

    public Express getClicked() {
        return clickedExpress;
    }

    private void resetAllButton() {
        for (RadioButton button : buttons) {
            button.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return expressList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton button;

        public ViewHolder(RadioButton itemView) {
            super(itemView);
            button = itemView;
        }
    }
}
