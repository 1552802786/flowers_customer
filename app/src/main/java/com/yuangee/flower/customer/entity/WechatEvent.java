package com.yuangee.flower.customer.entity;

import java.io.Serializable;

/**
 * 作者：Rookie on 2018/9/20 14:01
 */
public class WechatEvent implements Serializable {
    public String openid;

    public WechatEvent() {

    }

    public WechatEvent(String openid) {
        this.openid = openid;
    }
}
