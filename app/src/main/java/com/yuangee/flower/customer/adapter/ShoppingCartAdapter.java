package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.CartItem;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.util.RxManager;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/3/10.
 */
public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.GoodsHolder> {

    private Context context;
    private List<CartItem> data;

    private OnItemClickListener mOnItemClickListener;   //声明监听器接口

    private RxManager rxManager;

    public interface OnItemClickListener {
        /**
         * 点击事件的处理
         *
         * @param view     :item中设置了监听的view
         * @param position :点击了item的位置
         */
        void onItemClick(View view, int position);
    }

    /**
     * 增删后总金钱的变化
     */
    public interface OnMoneyChangedListener {
        void onMoneyChange(double money);
    }

    /**
     * 通过adapter设置监听器
     *
     * @param mOnItemClickListener 监听器的接口类型
     */
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public OnMoneyChangedListener onMoneyChangedListener;

    public void setOnMoneyChangedListener(OnMoneyChangedListener onMoneyChangedListener) {
        this.onMoneyChangedListener = onMoneyChangedListener;
    }

    public ShoppingCartAdapter(Context context, RxManager rxManager) {
        this.context = context;
        this.rxManager = rxManager;
        data = new ArrayList<>();
    }

    @Override
    public GoodsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.shopping_car_item, null);
        GoodsHolder holder = new GoodsHolder(view);

        holder.layoutView = view;    //将布局view保存起来用作点击事件

        holder.goodsImg = view.findViewById(R.id.goods_img);
        holder.goodsName = view.findViewById(R.id.goods_name);
        holder.goodsMoney = view.findViewById(R.id.goods_money);
        holder.goodsPrice = view.findViewById(R.id.goods_price);
        holder.removeFromCar = view.findViewById(R.id.delete_con);
        holder.numSub = view.findViewById(R.id.num_sub);
        holder.numAdd = view.findViewById(R.id.num_add);
        holder.goodsNum = view.findViewById(R.id.goods_num);
        holder.selectedRadio = view.findViewById(R.id.selected);

        return holder;
    }

    public void setData(List<CartItem> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    //设置数据
    @Override
    public void onBindViewHolder(final GoodsHolder holder, final int position) {

        final CartItem bean = data.get(position);
        if (null != bean.wares) {
            bean.wares.selectedNum = bean.quantity;
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.color.color_f6)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context)
                    .load(Config.BASE_URL + bean.wares.image)
                    .apply(options)
                    .into(holder.goodsImg);

            holder.goodsName.setText(bean.wares.name);
            holder.goodsPrice.setText("¥" + bean.wares.unitPrice + "/" + bean.wares.unit);
        }
        holder.goodsMoney.setText("总计：" + bean.totalPrice + "元");

        holder.goodsNum.setText(bean.quantity + "");

        holder.selectedRadio.setChecked(bean.selected);

        holder.selectedRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    bean.selected = true;
                } else {
                    bean.selected = false;
                }
            }
        });

        if (null != onMoneyChangedListener) {
            changeMoney();
        }

        holder.removeFromCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.remove(position);
                notifyDataSetChanged();
            }
        });

        if (bean.quantity >= bean.wares.salesVolume) {
            holder.numAdd.setEnabled(false);
        } else {
            holder.numAdd.setEnabled(true);
        }

        if (bean.quantity <= 1) {
            holder.numSub.setEnabled(false);
        } else {
            holder.numSub.setEnabled(true);
        }

        holder.numSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.goodsNum.setText("" + (bean.quantity - 1));
                cartItemSub(bean.id, bean.cartId, 1, position);
            }
        });

        holder.numAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.goodsNum.setText("" + (bean.quantity + 1));
                cartItemAdd(bean.id, bean.cartId, 1, position);
            }
        });

        holder.removeFromCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCartItem(bean.id, bean.cartId, position);
            }
        });

        //给该item设置一个监听器
        if (mOnItemClickListener != null) {
            holder.layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    //自定义holder
    class GoodsHolder extends RecyclerView.ViewHolder {

        GoodsHolder(View itemView) {
            super(itemView);
        }

        View layoutView;
        ImageView goodsImg;
        TextView goodsName;
        FrameLayout removeFromCar;
        TextView goodsMoney;//总计
        TextView goodsPrice;//单价
        ImageView numSub;
        ImageView numAdd;
        TextView goodsNum;
        CheckBox selectedRadio;
    }

    private void changeMoney() {
        double money = 0.0;
        for (CartItem datum : data) {
            if (datum.selected) {
                money += datum.totalPrice;
            }
        }
        if (onMoneyChangedListener != null) {
            onMoneyChangedListener.onMoneyChange(money);
        }
    }

    private void cartItemAdd(long itemId, long cartId, int num, final int position) {
        Observable<CartItem> observable = ApiManager.getInstance().api
                .cartItemAdd(itemId, cartId, num)
                .map(new HttpResultFunc<CartItem>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        rxManager.add(observable.subscribe(new MySubscriber<>(context, false, false, new HaveErrSubscriberListener<CartItem>() {
            @Override
            public void onNext(CartItem o) {
                data.set(position, o);
                notifyItemChanged(position);
                changeMoney();
            }

            @Override
            public void onError(int code) {
                notifyItemChanged(position);//如果失败了数量 就设置回去
            }
        })));
    }

    private void cartItemSub(long memberId, long waresId, int num, final int position) {
        Observable<CartItem> observable = ApiManager.getInstance().api
                .cartItemSub(memberId, waresId, num)
                .map(new HttpResultFunc<CartItem>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        rxManager.add(observable.subscribe(new MySubscriber<>(context, false, false, new HaveErrSubscriberListener<CartItem>() {
            @Override
            public void onNext(CartItem o) {
                if (o.quantity == 0) {
                    data.remove(position);
                    notifyDataSetChanged();
                } else {
                    data.set(position, o);
                    notifyItemChanged(position);
                }
                changeMoney();
            }

            @Override
            public void onError(int code) {
                notifyItemChanged(position);//如果失败了数量 就设置回去
            }
        })));
    }

    private void deleteCartItem(long itemId, long cartId, final int position) {
        Observable<Object> observable = ApiManager.getInstance().api
                .deleteCartItem(itemId, cartId)
                .map(new HttpResultFunc<>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        rxManager.add(observable.subscribe(new MySubscriber<>(context, true, true, new HaveErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                data.remove(position);
                notifyDataSetChanged();
                changeMoney();
            }

            @Override
            public void onError(int code) {

            }
        })));
    }

    public List<CartItem> getList() {
        return data;
    }

}
