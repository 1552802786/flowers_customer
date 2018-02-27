package com.yuangee.flower.customer.entity;

import java.io.Serializable;

/**
 * Created by developerLzh on 2017/9/18 0018.
 */

public class Goods implements Serializable {

    public long id;//商品id

    public String image;//图片地址

    public long genreId;//一级大类id

    public String genreName;//一级大类名称

    public long genreSubId;//二级小类Id

    public String genreSubName;//二级小类名称

    public String name;//商品名称

    public String grade;//商品等级

    public String color;//颜色

    public String spec;//规格

    public String unit;//单位

    public double unitPrice;//单价

    public int salesVolume;//可售量

    public int bespeakNum;//可预约数量

    public long shopId;//商铺id

    public String shopName;//商铺名

    public long selectedNum;//选中的商品数量

    public String startDeliver;//发货时间
    public boolean bespeak; //是否参与预约
    public String depict; //详细说明
    public String memo; //备注
    public double mumPrice;//竞拍价
    public boolean auction;
}
