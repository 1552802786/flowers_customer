package com.yuangee.flower.customer.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by liuzihao on 2017/12/4.
 * (商家修改商品时间设置 value：是true否false开启；start：开始时间；
 * end:结束时间；)
 */
@Entity()
public class UpdateWares {
    @Id(autoincrement = true)
    public long id;

    @Property
    public String start;//07:00

    @Property
    public String end;//23:59

    @Property
    public boolean value;//

    @Generated(hash = 1354181019)
    public UpdateWares(long id, String start, String end, boolean value) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.value = value;
    }

    @Generated(hash = 105855106)
    public UpdateWares() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStart() {
        return this.start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return this.end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }


}
