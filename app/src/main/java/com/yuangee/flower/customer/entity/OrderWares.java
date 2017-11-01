package com.yuangee.flower.customer.entity;

/**
 * Created by developerLzh on 2017/11/1 0001.
 */

public class OrderWares {
    public Long id;

    public String created;

    public String updated;

    public String image;//商品图片

    public Long genreId;//所属类别id

    public String genreName;//所属类别名称

    public Long genreSubId;//所属类别-子类id

    public String genreSubName;//所属类别-子类名称

    public String name;//商品名称

    public String grade;//商品等级 A-D	

    public String color;//商品颜色

    public String spec;//商品规格

    public String unit;//商品单位

    public Double unitPrice;//商品单价

    public Integer salesVolume;//可售量

    public Integer orderNum;//订购量

    public Long shopId;//供货商Id

    public String shopName;//供货商名称

    public Long version;//版本号

}
