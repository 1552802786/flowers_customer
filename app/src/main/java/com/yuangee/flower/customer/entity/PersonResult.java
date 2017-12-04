package com.yuangee.flower.customer.entity;

/**
 * Created by liuzihao on 2017/12/4.
 */

public class PersonResult {
    public long currentServerTime;//currentServerTime系统当前时间

    public UpdateWares updateWares;//(商家修改商品时间设置 value：是true否false开启；start：开始时间 end:结束时间；)

    public AuctionTime auctionTime;//(竞拍时间设置 value：是true否false开启；start：开始时间；end:结束时间；)

    public Member member;//会员信息

    public DestineTime destineTime;//(预约时间设置 value：是true否false开启；start：最小天数；startDate:最小预约时间；end:最大天数；endDate:最大预约时间)

    public BusinessTime businessTime;//(交易时间设置 value：是true否false开启；start：开始时间 end:结束时间；)
}
