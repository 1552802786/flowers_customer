package com.yuangee.flower.customer.entity;

import java.io.Serializable;

/**
 * Created by admin on 2018/3/21.
 */

public class ShopOrderCount implements Serializable{
    public String shouldSettleMoney;//待结算金额
    public String orderCount;//订单数
    public String orderMoney;//订单金额
    public String shopId;//店铺id
}
