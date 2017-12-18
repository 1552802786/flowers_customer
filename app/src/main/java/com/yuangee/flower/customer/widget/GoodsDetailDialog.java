package com.yuangee.flower.customer.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.Goods;

/**
 * Created by liuzihao on 2017/12/18.
 */

public class GoodsDetailDialog extends Dialog {
    private View view;

    public GoodsDetailDialog(@NonNull Context context) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.goods_detail_dialog, null);
    }

    protected GoodsDetailDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected GoodsDetailDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void initDialog(Context context) {

    }

    public void setGoods(Goods goods) {
        TextView genre = view.findViewById(R.id.genre_first);
        TextView subGenre = view.findViewById(R.id.genre_sub);
        TextView grade = view.findViewById(R.id.grade);
        TextView color = view.findViewById(R.id.color);
        TextView spec = view.findViewById(R.id.spec);
        TextView unit = view.findViewById(R.id.unit);
        TextView sales_value = view.findViewById(R.id.sales_value);
        TextView goods_name = view.findViewById(R.id.goods_name);
        TextView goods_price = view.findViewById(R.id.goods_price);

        ImageView img = view.findViewById(R.id.ic_goods_img);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_no_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(getContext())
                .load(Config.BASE_URL + goods.image)
                .apply(options)
                .into(img);

        genre.setText(goods.genreName);
        subGenre.setText(goods.genreSubName);
        grade.setText(goods.grade);
        color.setText(goods.color);
        spec.setText(goods.spec);
        unit.setText(goods.unit);
        sales_value.setText(String.valueOf(goods.salesVolume));
        goods_name.setText(goods.name);
        goods_price.setText(String.valueOf(goods.unitPrice));

        setContentView(view);
        show();
    }
}
