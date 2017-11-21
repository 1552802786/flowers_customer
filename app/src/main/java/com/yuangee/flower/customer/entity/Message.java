package com.yuangee.flower.customer.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/11/6 0006.
 */

public class Message {
    public long id;
    public String content;
    public String contentMore;

    @SerializedName("hasHead")
    public boolean isRead;
    public String created;
}
