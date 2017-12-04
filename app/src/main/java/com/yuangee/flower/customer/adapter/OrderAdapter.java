package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonElement;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.Config;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.activity.OrderDetailActivity;
import com.yuangee.flower.customer.entity.Order;
import com.yuangee.flower.customer.entity.OrderWare;
import com.yuangee.flower.customer.entity.ZfbResult;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.RxManager;
import com.yuangee.flower.customer.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/3/10.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private Context context;
    private List<Order> data;

    private OnRefresh onRefresh;   //声明监听器接口

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

    public interface OnRefresh {
        void onRefresh();
    }

    public interface OnStartZfbPay {
        void pay(String s);
    }

    private OnStartZfbPay zfbPay;

    public void setZfbPay(OnStartZfbPay zfbPay) {
        this.zfbPay = zfbPay;
    }

    public void setOnRefresh(OnRefresh onRefresh) {
        this.onRefresh = onRefresh;
    }

    public OrderAdapter(Context context, RxManager rxManager) {
        this.context = context;
        this.rxManager = rxManager;
        data = new ArrayList<>();
    }

    @Override
    public OrderAdapter.OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.orders_item, null);
        OrderHolder holder = new OrderHolder(view);

        holder.root = view;    //将布局view保存起来用作点击事件
        holder.img1 = view.findViewById(R.id.img_1);
        holder.img2 = view.findViewById(R.id.img_2);
        holder.img3 = view.findViewById(R.id.img_3);
        holder.img4 = view.findViewById(R.id.img_4);
        holder.img5 = view.findViewById(R.id.img_5);
        holder.leftBtn = view.findViewById(R.id.left_btn);
        holder.rightBtn = view.findViewById(R.id.right_btn);
        holder.tvGoodsKind = view.findViewById(R.id.order_goods_kind);
        holder.tvOrderMoney = view.findViewById(R.id.order_fee);
        holder.tvOrderStatus = view.findViewById(R.id.order_status);
        holder.tvOrderTime = view.findViewById(R.id.order_time);

        return holder;
    }

    public void setData(List<Order> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    //设置数据
    @Override
    public void onBindViewHolder(OrderAdapter.OrderHolder holder, final int position) {

        final Order bean = data.get(position);
        String kindStr = "共<font color='#52A436'>" + bean.orderWaresList != null ? "" + bean.orderWaresList.size() : "0" + "</font>类商品";
        holder.tvGoodsKind.setText(kindStr);
        holder.tvOrderTime.setText("下单时间：" + bean.created);
        holder.tvOrderStatus.setText(bean.getStatusStr());
        holder.tvOrderMoney.setText("¥" + bean.payable + "(不包含运费)");

        if (null != bean.orderWaresList) {
            for (int i = 0; i < bean.orderWaresList.size(); i++) {
                OrderWare wares = bean.orderWaresList.get(i);
                if (i == 0) {
                    loadImg(holder.img1, wares.wares.image, context);
                } else if (i == 1) {
                    loadImg(holder.img2, wares.wares.image, context);
                } else if (i == 2) {
                    loadImg(holder.img3, wares.wares.image, context);
                } else if (i == 3) {
                    loadImg(holder.img4, wares.wares.image, context);
                } else if (i == 4) {
                    loadImg(holder.img5, wares.wares.image, context);
                }
            }
        }

        if (bean.status == 0) {
            holder.leftBtn.setVisibility(View.VISIBLE);
            holder.rightBtn.setVisibility(View.VISIBLE);
            holder.leftBtn.setText("去支付");
            holder.rightBtn.setText("取消订单");
            holder.leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payOrder(bean);
                }
            });
            holder.rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelOrder(bean);
                }
            });
        } else if (bean.status == 1) {
            holder.leftBtn.setVisibility(View.VISIBLE);
            holder.rightBtn.setVisibility(View.GONE);
            holder.leftBtn.setText("提醒发货");
            holder.leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showMessage(context, "提醒卖家发货成功，商品将很快送到你手中");
                }
            });
        } else if (bean.status == 2) {
            holder.leftBtn.setVisibility(View.VISIBLE);
            holder.rightBtn.setVisibility(View.GONE);
            holder.leftBtn.setText("确认收货");
            holder.leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmOrder(bean);
                }
            });
        } else if (bean.status == 3) {
            holder.leftBtn.setVisibility(View.GONE);
            holder.rightBtn.setVisibility(View.GONE);
        } else if (bean.status == 4) {
            holder.leftBtn.setVisibility(View.VISIBLE);
            holder.rightBtn.setVisibility(View.VISIBLE);
            holder.leftBtn.setText("去支付");
            holder.rightBtn.setText("取消订单");
            holder.leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payOrder(bean);
                }
            });
            holder.rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelOrder(bean);
                }
            });
        } else if (bean.status == 5) {
            holder.leftBtn.setVisibility(View.GONE);
            holder.rightBtn.setVisibility(View.GONE);
        }
        //给该item设置一个监听器
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OrderDetailActivity.class);
                    intent.putExtra("order", bean);
                    context.startActivity(intent);
                }
            });
    }

    private void payOrder(final Order order) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage("请选择支付方式")
                .setPositiveButton("支付宝支付", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (order.bespeak) {
                            payYuyueZfb(order.id);
                        } else {
                            payJishiZfb(order.id);
                        }
                    }
                })
                .setNegativeButton("微信支付", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (order.bespeak) {
                            payYuyueWx(order.id);
                        } else {
                            payJishiWx(order.id);
                        }
                    }
                }).create();
        alertDialog.show();
    }

    private void cancelOrder(final Order order) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage("您确定要取消订单吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateOrderStatus(order.id, 5);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void confirmOrder(Order order) {

    }

    private void payJishiWx(Long orderId) {
        Observable<JsonElement> observable = ApiManager.getInstance().api
                .payJishiSingleWx(orderId)
                .map(new HttpResultFunc<JsonElement>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        rxManager.add(observable.subscribe(new MySubscriber<JsonElement>(context, true,
                false, new NoErrSubscriberListener<JsonElement>() {
            @Override
            public void onNext(JsonElement jsonElement) {
                detailWxPay(jsonElement);
            }
        })));
    }

    private void payYuyueWx(Long orderId) {
        Observable<JsonElement> observable = ApiManager.getInstance().api
                .payYuyueSingleWx(orderId)
                .map(new HttpResultFunc<JsonElement>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        rxManager.add(observable.subscribe(new MySubscriber<JsonElement>(context, true,
                false, new NoErrSubscriberListener<JsonElement>() {
            @Override
            public void onNext(JsonElement jsonElement) {
                detailWxPay(jsonElement);
            }
        })));
    }

    private void detailWxPay(JsonElement jsonElement) {
        try {
            JSONObject json = new JSONObject(jsonElement.toString());
            if (null != json && !json.has("retcode")) {
                PayReq req = new PayReq();
                req.appId = json.getString("appid");
                req.partnerId = json.getString("partnerid");
                req.prepayId = json.getString("prepayid");
                req.nonceStr = json.getString("noncestr");
                req.timeStamp = json.getString("timestamp");
                req.packageValue = json.getString("package");
                req.sign = json.getString("sign");
                req.extData = "app data"; // optional
                Log.e("wxPay", "正常调起支付");

                IWXAPI api = WXAPIFactory.createWXAPI(context, req.appId);

                api.sendReq(req);
            } else {
                Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
                Toast.makeText(context, "返回错误：" + json.getString("retmsg"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void payJishiZfb(Long orderId) {
        Observable<ZfbResult> observable = ApiManager.getInstance().api
                .payJishiSingleZfb(orderId)
                .map(new HttpResultFunc<ZfbResult>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        rxManager.add(observable.subscribe(new MySubscriber<ZfbResult>(context, true,
                false, new NoErrSubscriberListener<ZfbResult>() {
            @Override
            public void onNext(ZfbResult s) {
                detailZfb(s.orderInfo);
            }
        })));
    }

    private void payYuyueZfb(Long orderId) {
        Observable<ZfbResult> observable = ApiManager.getInstance().api
                .payYuyueSingleZfb(orderId)
                .map(new HttpResultFunc<ZfbResult>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        rxManager.add(observable.subscribe(new MySubscriber<ZfbResult>(context, true,
                false, new NoErrSubscriberListener<ZfbResult>() {
            @Override
            public void onNext(ZfbResult s) {
                detailZfb(s.orderInfo);
            }
        })));
    }

    private void detailZfb(final String s) {
        if (null != zfbPay) {
            zfbPay.pay(s);
        }
    }

    private void updateOrderStatus(long orderId, int status) {
        Observable<Object> observable = ApiManager.getInstance().api
                .updateOrder(orderId, status)
                .map(new HttpResultFunc<Object>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        rxManager.add(observable.subscribe(new MySubscriber<Object>(context, true,
                true, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                if (onRefresh != null) {
                    onRefresh.onRefresh();
                }
            }
        })));


    }

    private void loadImg(ImageView imageView, String path, Context context) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(Config.BASE_URL + path)
                .apply(options)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    //自定义holder
    class OrderHolder extends RecyclerView.ViewHolder {

        public OrderHolder(View itemView) {
            super(itemView);
        }

        View root;
        TextView tvOrderTime;
        TextView tvOrderStatus;
        TextView tvGoodsKind;
        TextView tvOrderMoney;
        Button rightBtn;
        Button leftBtn;
        ImageView img1;
        ImageView img2;
        ImageView img3;
        ImageView img4;
        ImageView img5;
    }

}
