package com.yuangee.flower.customer.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/9/4 0004.
 */

public class BannerBean {

    @SerializedName("photo")
    public String imageUrl;

    @SerializedName("name")
    public String title;

    @SerializedName("url")
    public String linkUrl;
}
