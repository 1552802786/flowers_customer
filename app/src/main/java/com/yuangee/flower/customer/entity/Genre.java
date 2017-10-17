package com.yuangee.flower.customer.entity;

import java.util.List;

/**
 * Created by developerLzh on 2017/10/17 0017.
 *
 * 大种类
 */

public class Genre {
    public long id;
    public String genreName;//种类名
    public int genreSequence;//排序
    public List<GenreSub> genreSubs;
}
