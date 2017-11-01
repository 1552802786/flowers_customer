package com.yuangee.flower.customer.entity;

/**
 * Created by developerLzh on 2017/11/1 0001.
 */

public class OrderPay {

    private Long id;

    private Long orderId; //订单id

    private Double balance; //余额

    private Integer status; // 0 等待支付 1 已支付

    private Double moeny; //订单金额

    private String info; //支付信息

    private String created;

    private Integer payType; //0、预约  1、立即

    private String payMode; //支付宝 | 微信 | 余额 | 微信和余额 | 支付宝和余额

    private String  outTradeNo; //第三号支付号

    private String tradeNo; //交易流水号

}
