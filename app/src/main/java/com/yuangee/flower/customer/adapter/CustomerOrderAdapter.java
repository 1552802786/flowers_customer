package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.yuangee.flower.customer.activity.CusOrderDetailActivity;
import com.yuangee.flower.customer.activity.CustomerOrderPayActivity;
import com.yuangee.flower.customer.entity.CustomerOrder;
import com.yuangee.flower.customer.entity.OrderWare;
import com.yuangee.flower.customer.entity.ShopOrder;
import com.yuangee.flower.customer.entity.ZfbResult;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.RxManager;
import com.yuangee.flower.customer.util.TimeUtil;
import com.yuangee.flower.customer.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/3/10.
 */
public class CustomerOrderAdapter extends RecyclerView.Adapter<CustomerOrderAdapter.OrderHolder> {

    private Context context;
    public List<CustomerOrder> data;

    private OnRefresh onRefresh;   //声明监听器接口

    private RxManager rxManager;

    private boolean isShop = false;

    public CustomerOrderAdapter() {

    }

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

    public CustomerOrderAdapter(Context context, RxManager rxManager, boolean isShop) {
        this.context = context;
        this.rxManager = rxManager;
        this.isShop = isShop;
        data = new ArrayList<>();
    }

    @Override
    public CustomerOrderAdapter.OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.orders_item, null);
        OrderHolder holder = new OrderHolder(view);
        view.setTag(holder);
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
        holder.clock_icon = view.findViewById(R.id.clock_icon);

        return holder;
    }

    public void setData(List<CustomerOrder> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    //设置数据
    @Override
    public void onBindViewHolder(CustomerOrderAdapter.OrderHolder holder, final int position) {

        final CustomerOrder bean = data.get(position);

        holder.tvOrderTime.setText("订单编号：" + bean.orderNo);
        holder.tvOrderStatus.setText(bean.getStatusStr());
        if (bean.status == CustomerOrder.ORDER_STATUS_BE_BACK) {
            holder.tvOrderMoney.setTextColor(Color.RED);
            holder.tvOrderMoney.setText(getPayLeftTime(bean.bespeakDate));
            holder.leftBtn.setBackgroundResource(R.drawable.corners_color_gray);
            holder.clock_icon.setVisibility(View.VISIBLE);
        } else {
            holder.leftBtn.setBackgroundResource(R.drawable.corners_color_green);
            holder.tvOrderMoney.setTextColor(Color.GRAY);
            holder.tvOrderMoney.setText("¥" + bean.realPay);
            holder.clock_icon.setVisibility(View.GONE);
        }

        int totalWares = 0;
        if (null != bean.orderList) {
            List<OrderWare> waresList = new ArrayList<>();
            for (ShopOrder shopOrder : bean.orderList) {
                for (int i = 0; i < shopOrder.orderWaresList.size(); i++) {
                    totalWares++;
                    OrderWare wares = shopOrder.orderWaresList.get(i);
                    waresList.add(wares);
                }
            }

            for (int i = 0; i < waresList.size(); i++) {
                OrderWare wares = waresList.get(i);
                if (wares.wares != null) {
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

        }

        String kindStr = "共<font color='#f74f50'>" + (totalWares != 0 ? "" + totalWares : "0") + "</font>类商品";
        holder.tvGoodsKind.setText(Html.fromHtml(kindStr));

        if (!isShop) {

            if (bean.status == CustomerOrder.ORDER_STATUS_NOTPAY) {
                holder.leftBtn.setVisibility(View.VISIBLE);
                holder.rightBtn.setVisibility(View.VISIBLE);
                if (!bean.bespeak) {
                    holder.leftBtn.setText("去支付");
                } else {
                    holder.leftBtn.setText("支付预约金");
                }
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
            } else if (bean.status == CustomerOrder.ORDER_STATUS_WAIT) {
                holder.leftBtn.setVisibility(View.VISIBLE);
                holder.rightBtn.setVisibility(View.GONE);
                holder.leftBtn.setText("提醒发货");
                holder.leftBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.showMessage(context, "提醒卖家发货成功，商品将很快送到你手中");
                    }
                });
            } else if (bean.status == CustomerOrder.ORDER_STATUS_CONSIGN) {
                holder.leftBtn.setVisibility(View.GONE);
                holder.rightBtn.setVisibility(View.GONE);
//                holder.leftBtn.setText("确认收货");
//                holder.leftBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        confirmOrder(bean);
//                    }
//                });
            } else if (bean.status == CustomerOrder.ORDER_STATUS_FINISH) {
                holder.leftBtn.setVisibility(View.GONE);
                holder.rightBtn.setVisibility(View.GONE);
            } else if (bean.status == CustomerOrder.ORDER_STATUS_BE_BACK) {
                holder.leftBtn.setVisibility(View.VISIBLE);
                holder.leftBtn.setEnabled(false);
                holder.rightBtn.setVisibility(View.VISIBLE);
                holder.leftBtn.setText("支付尾款");
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
            } else if (bean.status == CustomerOrder.ORDER_STATUS_CANCEL) {
                holder.leftBtn.setVisibility(View.GONE);
                holder.rightBtn.setVisibility(View.GONE);
            }
        }
        //给该item设置一个监听器
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CusOrderDetailActivity.class);
                intent.putExtra("cusOrder", bean);
                intent.putExtra("isShop", isShop);
                context.startActivity(intent);
            }
        });
    }

    private void fahuo(final CustomerOrder bean) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage("您确定要确认发货吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateOrderStatus(bean.id, CustomerOrder.ORDER_STATUS_CONSIGN);
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

    private void payOrder(final CustomerOrder shopOrder) {
        Intent it = new Intent(context, CustomerOrderPayActivity.class);
        it.putExtra("order", shopOrder);
        context.startActivity(it);
    }

    private void cancelOrder(final CustomerOrder shopOrder) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage("您确定要取消订单吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateOrderStatus(shopOrder.id, CustomerOrder.ORDER_STATUS_CANCEL);
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

//    private void confirmOrder(CustomerOrder shopOrder) {
//        Observable<CustomerOrder> observable = ApiManager.getInstance().api
//                .confirmMoney(shopOrder.id)
//                .map(new HttpResultFunc<CustomerOrder>(context))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//        rxManager.add(observable.subscribe(new MySubscriber<CustomerOrder>(context, true,
//                true, new NoErrSubscriberListener<CustomerOrder>() {
//            @Override
//            public void onNext(CustomerOrder o) {
//                payJishiZfb(o.id, 1);//预约单支付尾款
//            }
//        })));
//    }
//
//    /**
//     * @param orderId
//     * @param type    0 即时单支付 1预约单支付尾款
//     */
//    private void payJishiWx(Long orderId, Integer type) {
//        Observable<JsonElement> observable = ApiManager.getInstance().api
//                .payJishiSingleWx(orderId, type)
//                .map(new HttpResultFunc<JsonElement>(context))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//        rxManager.add(observable.subscribe(new MySubscriber<JsonElement>(context, true,
//                false, new NoErrSubscriberListener<JsonElement>() {
//            @Override
//            public void onNext(JsonElement jsonElement) {
//                detailWxPay(jsonElement);
//            }
//        })));
//    }
//
//    private void payYuyueWx(Long orderId) {
//        Observable<JsonElement> observable = ApiManager.getInstance().api
//                .payYuyueSingleWx(orderId)
//                .map(new HttpResultFunc<JsonElement>(context))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//        rxManager.add(observable.subscribe(new MySubscriber<JsonElement>(context, true,
//                false, new NoErrSubscriberListener<JsonElement>() {
//            @Override
//            public void onNext(JsonElement jsonElement) {
//                detailWxPay(jsonElement);
//            }
//        })));
//    }
//
//    private void detailWxPay(JsonElement jsonElement) {
//        try {
//            JSONObject json = new JSONObject(jsonElement.toString());
//            if (null != json && !json.has("retcode")) {
//                PayReq req = new PayReq();
//                req.appId = json.getString("appid");
//                req.partnerId = json.getString("partnerid");
//                req.prepayId = json.getString("prepayid");
//                req.nonceStr = json.getString("noncestr");
//                req.timeStamp = json.getString("timestamp");
//                req.packageValue = json.getString("package");
//                req.sign = json.getString("sign");
//                req.extData = "app data"; // optional
//                Log.e("wxPay", "正常调起支付");
//
//                IWXAPI api = WXAPIFactory.createWXAPI(context, req.appId);
//
//                api.sendReq(req);
//            } else {
//                Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
//                Toast.makeText(context, "返回错误：" + json.getString("retmsg"), Toast.LENGTH_SHORT).show();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * @param orderId
//     * @param type    0 即时单支付 1预约单支付尾款
//     */
//    private void payJishiZfb(Long orderId, Integer type) {
//        Observable<ZfbResult> observable = ApiManager.getInstance().api
//                .payJishiSingleZfb(orderId, type)
//                .map(new HttpResultFunc<ZfbResult>(context))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//        rxManager.add(observable.subscribe(new MySubscriber<ZfbResult>(context, true,
//                false, new NoErrSubscriberListener<ZfbResult>() {
//            @Override
//            public void onNext(ZfbResult s) {
//                detailZfb(s.orderInfo);
//            }
//        })));
//    }
//
//    private void payYuyueZfb(Long orderId) {
//        Observable<ZfbResult> observable = ApiManager.getInstance().api
//                .payYuyueSingleZfb(orderId)
//                .map(new HttpResultFunc<ZfbResult>(context))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//        rxManager.add(observable.subscribe(new MySubscriber<ZfbResult>(context, true,
//                false, new NoErrSubscriberListener<ZfbResult>() {
//            @Override
//            public void onNext(ZfbResult s) {
//                detailZfb(s.orderInfo);
//            }
//        })));
//    }
//
//    private void detailZfb(final String s) {
//        if (null != zfbPay) {
//            zfbPay.pay(s);
//        }
//    }

    private void updateOrderStatus(long orderId, int status) {
        Observable<Object> observable = ApiManager.getInstance().api
                .updateBigOrder(orderId, status)
                .map(new HttpResultFunc<>(context))
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

    /**
     * 支付剩余时间计算
     */
    private String getPayLeftTime(long deadLine) {
        long leftMillis = deadLine - System.currentTimeMillis();

        if (leftMillis < 0) {
            return "支付已超时";
        } else {
            long day = leftMillis / (1000 * 60 * 60 * 24);
            long h = leftMillis / (1000 * 60 * 60) % 24;
            long m = leftMillis / (1000 * 60) % 60;
            long s = leftMillis / 1000 % 60;
            StringBuilder sb = new StringBuilder();
            sb.append("距离预约时间: ");
            if (day > 0) {
                sb.append(day).append("天 ");
            }
            if (h > 0) {
                sb.append(h).append(":");
            }
            if (m > 0) {
                sb.append(m).append(":");
            }
            if (s > 0) {
                sb.append(s);
            }
            return sb.toString();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //自定义holder
    public class OrderHolder extends RecyclerView.ViewHolder {

        public OrderHolder(View itemView) {
            super(itemView);
        }

        View root;
        TextView tvOrderTime;
        TextView tvOrderStatus;
        TextView tvGoodsKind;
        public TextView tvOrderMoney;
        Button rightBtn;
        Button leftBtn;
        ImageView img1;
        ImageView img2;
        ImageView img3;
        ImageView img4;
        ImageView img5;
        ImageView clock_icon;
    }

}
