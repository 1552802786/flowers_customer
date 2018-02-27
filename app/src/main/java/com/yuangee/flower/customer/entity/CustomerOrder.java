package com.yuangee.flower.customer.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by liuzihao on 2018/1/14.
 */

public class CustomerOrder implements Serializable{
    public static final int ORDER_STATUS_NOTPAY = 0;//未支付
    public static final int ORDER_STATUS_WAIT = 1;//已支付,未发货
    public static final int ORDER_STATUS_CONSIGN = 2;//已发货
    public static final int ORDER_STATUS_BE_BACK=3;//预约订单,已支付预约金,未完成支付,待支付尾款
    public static final int ORDER_STATUS_CANCEL=4;//订单取消
    public static final int ORDER_STATUS_JP_NOT_FINISHPAY=5; //竞拍订单支付金额不够订单金额 还差点
    public static final int ORDER_STATUS_FINISH=6;//质检后完成

    public Long id;

    public String orderNo;//订单号

    public Integer quantity;//数量

    public String created;//创建时间

    public double payable; //应付

    public String memo;//b备注

    public Integer status;//状态

    public Boolean bespeak;//是否预约

    public Long bespeakDate;//预约时间

    public String bespeakDateStr;

    public double bespeakMoney;//预约金额

    public String receiverName;//收货人名字

    public String receiverPhone;//收货人电话

    public String receiverAddress;//收货人地址

    public String courierNumber;//订单号

    public Long expressId;//收货地址信息id

    public List<ShopOrder> orderList;//商品明细

    public static final long serialVersionUID = 1L;

    public BigDecimal expressDeliveryMoney;//快递费

    public BigDecimal couponMoney;

    public BigDecimal customerBrokerage;//收取客户手续费


    @SerializedName("preparePrice")
    public BigDecimal peihuoFee;//配货费

    @SerializedName("packPrice")
    public BigDecimal baozhuangFee;//包装费

    public double shopIncome;//供货商收益,没用字段

    public double platformRevenue;//平台收益,没用字段

    public  double realPay;//实付

    public Long memberId;//客户id

    public String memberName;//客户名称

    public String memberPhone;//客户手机号

    public Long shopId;//商户id

    public String shopName;//商户名称

    public Long orderRecordId;

    public String getStatusStr(){
        if(status == ORDER_STATUS_NOTPAY){
            return "未支付";
        } else if(status == ORDER_STATUS_WAIT){
            return "未发货";
        } else if(status == ORDER_STATUS_CONSIGN){
            return "未收货";
        } else if(status == ORDER_STATUS_FINISH){
            return "已完成";
        } else if(status == ORDER_STATUS_BE_BACK){
            return "预约中";
        } else if(status == ORDER_STATUS_CANCEL){
            return "已取消";
        }
        return "";
    }
}
