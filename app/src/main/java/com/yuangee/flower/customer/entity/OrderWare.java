package com.yuangee.flower.customer.entity;

import java.io.Serializable;
import java.math.BigDecimal;

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

    public BigDecimal total;

    public Goods wares;
    public String waresUnitPrice;

}
