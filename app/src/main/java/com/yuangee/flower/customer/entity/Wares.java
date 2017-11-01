package com.yuangee.flower.customer.entity;

/**
 * Created by developerLzh on 2017/11/1 0001.
 */

public class Wares {
    public Long id;

    public Long orderId; //订单id

    public Long waresId; //商品id

    public Long shopId; //供货商id

    public String waresName; //商品名称

    public Integer quantity; //数量

    public Double total;//总价

    public Wares wares; //商品对象
}
