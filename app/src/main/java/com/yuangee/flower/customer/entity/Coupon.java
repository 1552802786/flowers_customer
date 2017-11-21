package com.yuangee.flower.customer.entity;

/**
 * Created by liuzihao on 2017/11/21.
 */

public class Coupon {
    public Long id;

    public String encoded; //编码

    public Integer status; //使用状态 0 未使用 1 已使用 2 已失效

    public Long couponRuleId; //优惠卷规则id

    public double money; //优惠卷金额

    public double couponFullMoney; //满减金额

    public Long memberId;

    public String memberName;

    public String memberPhone;

    public String becomeDueTime; //到期时间
}
