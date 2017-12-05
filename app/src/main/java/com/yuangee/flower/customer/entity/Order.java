package com.yuangee.flower.customer.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/1 0001.
 */

public class Order implements Serializable{

    public static final int ORDER_STATUS_NOTPAY = 0;//未支付
    public static final int ORDER_STATUS_WAIT = 1;//已支付,未发货
    public static final int ORDER_STATUS_CONSIGN = 2;//已发货,未收货
    public static final int ORDER_STATUS_FINISH = 3;//确认收货,完成
    public static final int ORDER_STATUS_BE_BACK=4;//预约订单 已支付预约金,未完成支付
    public static final int ORDER_STATUS_CANCEL=5;//订单取消

    public Long id;

    public String orderNo;//订单号

    public Integer quantity;//数量

    public String created;//创建时间

    public Double payable; //应付

    public String memo;//b备注

    public Integer status;//状态

    public Boolean bespeak;//是否预约

    public String bespeakDate;//预约时间

    public Double bespeakMoney;//预约金额

    public String receiverName;//收货人名字

    public String receiverPhone;//收货人电话

    public String receiverAddress;//收货人地址

    public String courierNumber;//订单号

    public Long expressId;//收货地址信息id

    public List<OrderWare> orderWaresList;//商品明细

    public static final long serialVersionUID = 1L;

    public Double expressDeliveryMoney;//快递费

    public Double couponMoney;//快递费

    public Double customerBrokerage;//收取客户手续费


    public Double peihuoFee;//收取客户手续费
    public Double baozhuangFee;//收取客户手续费

    public Double shopIncome;//供货商收益,没用字段

    public Double platformRevenue;//平台收益,没用字段

    public  Double realPay;//实付

    public Long memberId;//客户id

    public String memberName;//客户名称

    public String memberPhone;//客户手机号

    public Long shopId;//商户id

    public String shopName;//商户名称

    public Long orderRecordId;

    public String getStatusStr(){
        if(status == 0){
            return "未支付";
        } else if(status == 1){
            return "未发货";
        } else if(status == 2){
            return "未收货";
        } else if(status == 3){
            return "已完成";
        } else if(status == 4){
            return "已支付预约金";
        } else if(status == 5){
            return "已取消";
        }
        return "";
    }
}
