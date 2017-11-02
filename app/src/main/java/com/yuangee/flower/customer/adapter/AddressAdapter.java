package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.EditAddressActivity;
import com.yuangee.flower.customer.entity.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/10/27 0027.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressHolder> {

    private List<Address> addressList;

    private Context context;

    private boolean isManage = false;

    public AddressAdapter(Context context) {
        this.context = context;
        addressList = new ArrayList<>();
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
        notifyDataSetChanged();
    }

    public void setManage(boolean manage) {
        isManage = manage;
        notifyDataSetChanged();
    }

    public boolean getIsManage(){
        return isManage;
    }

    public List<Address> getAddressList(){
        return addressList;
    }

    @Override
    public AddressHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.address_item, null);
        AddressHolder holder = new AddressHolder(view);

        holder.receiverName = view.findViewById(R.id.receiver_name);
        holder.receiverPhone = view.findViewById(R.id.receiver_phone);
        holder.detailPlace = view.findViewById(R.id.detail_place);
        holder.icDelete = view.findViewById(R.id.ic_delete);
        holder.icEdit = view.findViewById(R.id.ic_edit);
        holder.icCheckBox = view.findViewById(R.id.selected);

        return holder;
    }

    @Override
    public void onBindViewHolder(AddressHolder holder, final int position) {
        final Address address = addressList.get(position);
        holder.detailPlace.setText(address.getStreet());
        holder.receiverName.setText(address.getShippingName());
        holder.receiverPhone.setText(address.getShippingPhone());

        if (address.isSelected) {
            holder.icCheckBox.setImageResource(R.drawable.ic_check_box_black_24dp);
        } else {
            holder.icCheckBox.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        }

        if (isManage) {
            holder.icCheckBox.setVisibility(View.GONE);
            holder.icDelete.setVisibility(View.VISIBLE);
            holder.icEdit.setVisibility(View.VISIBLE);
        } else {
            holder.icCheckBox.setVisibility(View.VISIBLE);
            holder.icDelete.setVisibility(View.GONE);
            holder.icEdit.setVisibility(View.GONE);
        }

        holder.icDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO delete place
            }
        });

        holder.icEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditAddressActivity.class);
                intent.putExtra("address", address);
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isManage) {
                    for (int i = 0; i < addressList.size(); i++) {
                        if (i == position) {
                            addressList.get(i).isSelected = true;
                        } else {
                            addressList.get(i).isSelected = false;
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    //自定义holder
    class AddressHolder extends RecyclerView.ViewHolder {

        public AddressHolder(View itemView) {
            super(itemView);
        }

        TextView detailPlace;
        TextView receiverName;
        TextView receiverPhone;
        ImageView icDelete;
        ImageView icEdit;
        ImageView icCheckBox;
    }
}
