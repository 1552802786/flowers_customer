package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.LoginActivity;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.fragment.shopping.ShoppingContract;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.result.AddCartResult;
import com.yuangee.flower.customer.util.DisplayUtil;
import com.yuangee.flower.customer.util.RxManager;
import com.yuangee.flower.customer.util.TimeUtil;
import com.yuangee.flower.customer.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/3/10.
 */
public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsHolder> {

    private Context context;
    private List<Goods> data;

    private OnItemClickListener mOnItemClickListener;   //声明监听器接口

    private int flag = 0;//0代表右侧是加入购物车  1代表右侧是预约

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
     * 通过adapter设置监听器
     *
     * @param mOnItemClickListener 监听器的接口类型
     */
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnAddClickListener {
        void onAddClick(ImageView view, int selectedNum);
    }

    private OnAddClickListener mOnAddClickListener;

    public void setmOnAddClickListener(OnAddClickListener listener) {
        this.mOnAddClickListener = listener;
    }

    private RxManager rxManager;

    public GoodsAdapter(Context context, int flag,RxManager rxManager) {
        this.flag = flag;
        this.context = context;
        this.rxManager = rxManager;
        data = new ArrayList<>();
    }

    @Override
    public GoodsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.goods_item, null);
        GoodsHolder holder = new GoodsHolder(view);

        holder.layoutView = view;    //将布局view保存起来用作点击事件

        holder.goodsImg = view.findViewById(R.id.goods_img);
        holder.goodsName = view.findViewById(R.id.goods_name);
        holder.goodsGrade = view.findViewById(R.id.goods_grade);
        holder.goodsColor = view.findViewById(R.id.goods_color);
        holder.goodsSpec = view.findViewById(R.id.goods_spec);
        holder.goodsLeft = view.findViewById(R.id.goods_left);
        holder.addToCar = view.findViewById(R.id.add_to_car);
        holder.goodsMoney = view.findViewById(R.id.goods_money);
        holder.numSub = view.findViewById(R.id.num_sub);
        holder.numAdd = view.findViewById(R.id.num_add);
        holder.goodsNum = view.findViewById(R.id.goods_num);
        holder.yuYue = view.findViewById(R.id.yu_yue);

        return holder;
    }

    public void setData(List<Goods> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    private void setBtnEnable(GoodsHolder holder, Goods bean) {
        if (bean.selectedNum == 1) {
            holder.numSub.setEnabled(false);
            holder.numAdd.setEnabled(true);
        } else if (bean.selectedNum == bean.salesVolume) {//商品可售的最大值
            holder.numSub.setEnabled(true);
            holder.numAdd.setEnabled(false);
        } else {
            holder.numSub.setEnabled(true);
            holder.numAdd.setEnabled(true);
        }
    }

    //设置数据
    @Override
    public void onBindViewHolder(final GoodsHolder holder, final int position) {

        final Goods bean = data.get(position);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(Config.BASE_URL + bean.image)
                .apply(options)
                .into(holder.goodsImg);

        holder.goodsName.setText(bean.name);
        holder.goodsGrade.setText("等级：" + bean.grade);
        holder.goodsColor.setText("颜色：" + bean.color);
        holder.goodsSpec.setText("规格：" + bean.spec);
        holder.goodsLeft.setText("可售量：" + bean.salesVolume);
        holder.goodsNum.setText(bean.selectedNum + "");

        if (flag == 0) {
            holder.addToCar.setVisibility(View.VISIBLE);
            holder.yuYue.setVisibility(View.GONE);
        } else {
            holder.addToCar.setVisibility(View.GONE);
            holder.yuYue.setVisibility(View.VISIBLE);
        }

        setBtnEnable(holder, bean);

        holder.addToCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long memberId = App.getPassengerId();
                addToCar(memberId, bean.id, (int) bean.selectedNum, bean, holder,position);
            }
        });
        holder.yuYue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                bean.isAddToCar = true;
            }
        });

        holder.numSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bean.selectedNum--;
                setBtnEnable(holder, bean);
                holder.goodsNum.setText("" + bean.selectedNum);
            }
        });

        holder.numAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bean.selectedNum++;
                setBtnEnable(holder, bean);
                holder.goodsNum.setText("" + bean.selectedNum);
            }
        });

        holder.goodsMoney.setText(bean.unitPrice + "/" + bean.unit);

        if(bean.selectedNum != 0){ //如果已经加入到购物车 将不能再进行添加到购物车
            holder.addToCar.setEnabled(false);
        } else {
            holder.addToCar.setEnabled(true);
        }

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
        TextView goodsGrade;
        TextView goodsColor;
        TextView goodsSpec;
        TextView goodsLeft;
        ImageView addToCar;
        TextView goodsMoney;
        ImageView numSub;
        ImageView numAdd;
        TextView goodsNum;
        TextView yuYue;
    }

    private void addToCar(long memberId, long waresId, final int num, final Goods goods, final GoodsHolder holder, final int position) {
        Observable<AddCartResult> observable = ApiManager.getInstance().api
                .addCartItem(memberId, waresId, num)
                .map(new HttpResultFunc<AddCartResult>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        rxManager.add(observable.subscribe(new MySubscriber<>(context, true, true, new HaveErrSubscriberListener<AddCartResult>() {
            @Override
            public void onNext(AddCartResult o) {
                //TODO 添加到购物车成功
                goods.selectedNum = num;
                ToastUtil.showMessage(context, "添加到购物车成功");
                if (null != mOnAddClickListener) {
                    mOnAddClickListener.onAddClick(holder.addToCar, (int) goods.selectedNum);
                }
                notifyItemChanged(position);
            }

            @Override
            public void onError(int code) {
                //TODO 添加到购物车失败
            }
        })));
    }

}
