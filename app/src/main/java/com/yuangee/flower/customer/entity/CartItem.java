package com.yuangee.flower.customer.entity;

/**
 * Created by developerLzh on 2017/10/26 0026.
 */

public class CartItem {
    public long id;
    public long cartId;
    public long waresId;
    public int quantity;
    public double totalPrice;
    public Goods wares;
    public boolean bespeak;
    public boolean selected;//是否选中
}
