package com.yuangee.flower.customer.result;

import com.yuangee.flower.customer.entity.CartItem;

/**
 * Created by developerLzh on 2017/10/26 0026.
 */

public class QueryCartResult {
    public long id;
    public int total;
    public double totalPrice;
    public CartItem items;
}
