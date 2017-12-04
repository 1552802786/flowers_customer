package com.yuangee.flower.customer.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by developerLzh on 2017/10/30 0030.
 */
@Entity
public class Express {

    @Id
    public long id;

    @Property
    public String expressDeliveryName;//快递名称

    @Property
    public double expressDeliveryMoney;//快递金额

    @Generated(hash = 838322869)
    public Express(long id, String expressDeliveryName,
            double expressDeliveryMoney) {
        this.id = id;
        this.expressDeliveryName = expressDeliveryName;
        this.expressDeliveryMoney = expressDeliveryMoney;
    }

    @Generated(hash = 760607181)
    public Express() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExpressDeliveryName() {
        return this.expressDeliveryName;
    }

    public void setExpressDeliveryName(String expressDeliveryName) {
        this.expressDeliveryName = expressDeliveryName;
    }

    public double getExpressDeliveryMoney() {
        return this.expressDeliveryMoney;
    }

    public void setExpressDeliveryMoney(double expressDeliveryMoney) {
        this.expressDeliveryMoney = expressDeliveryMoney;
    }
}
