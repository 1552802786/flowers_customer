package com.yuangee.flower.customer.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by liuzihao on 2017/11/21.
 */
@Entity
public class Coupon {
    @Id
    public Long id;

    @Property
    public String encoded; //编码

    @Property
    public Integer status; //使用状态 0 未使用 1 已使用 2 已失效

    @Property
    public Long couponRuleId; //优惠卷规则id

    @Property
    public double money; //优惠卷金额

    @Property
    public double couponFullMoney; //满减金额

    @Property
    public Long memberId;

    @Property
    public String memberName;

    @Property
    public String memberPhone;

    @Property
    public String becomeDueTime; //到期时间

    @Generated(hash = 919291176)
    public Coupon(Long id, String encoded, Integer status, Long couponRuleId,
            double money, double couponFullMoney, Long memberId, String memberName,
            String memberPhone, String becomeDueTime) {
        this.id = id;
        this.encoded = encoded;
        this.status = status;
        this.couponRuleId = couponRuleId;
        this.money = money;
        this.couponFullMoney = couponFullMoney;
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberPhone = memberPhone;
        this.becomeDueTime = becomeDueTime;
    }

    @Generated(hash = 75265961)
    public Coupon() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEncoded() {
        return this.encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCouponRuleId() {
        return this.couponRuleId;
    }

    public void setCouponRuleId(Long couponRuleId) {
        this.couponRuleId = couponRuleId;
    }

    public double getMoney() {
        return this.money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getCouponFullMoney() {
        return this.couponFullMoney;
    }

    public void setCouponFullMoney(double couponFullMoney) {
        this.couponFullMoney = couponFullMoney;
    }

    public Long getMemberId() {
        return this.memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return this.memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberPhone() {
        return this.memberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }

    public String getBecomeDueTime() {
        return this.becomeDueTime;
    }

    public void setBecomeDueTime(String becomeDueTime) {
        this.becomeDueTime = becomeDueTime;
    }
}
