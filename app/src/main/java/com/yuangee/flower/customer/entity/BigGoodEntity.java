package com.yuangee.flower.customer.entity;

import java.io.Serializable;

/**
 * 参与大宗使用  记录区间对应的价格
 */
public class BigGoodEntity implements Serializable {
    public String mini;
    public String max;
    public String price;

    public BigGoodEntity() {

    }

    public BigGoodEntity(String mini, String max, String price) {
        this.mini = mini;
        this.max = max;
        this.price = price;
    }
}
