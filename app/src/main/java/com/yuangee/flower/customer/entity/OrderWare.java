package com.yuangee.flower.customer.entity;

import java.io.Serializable;

/**
 * Created by developerLzh on 2017/11/1 0001.
 */

public class OrderWare implements Serializable {
    public Long id;

    public long orderId;

    public long waresId;

    public long shopId;

    public String waresName;

    public int quantity;

    public double total;

    public Goods wares;

}
