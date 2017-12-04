package com.yuangee.flower.customer.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by liuzihao on 2017/12/4.
 */
@Entity
public class DestineTime {
    @Id(autoincrement = true)
    public long id;

    @Property
    public long endDate;

    @Property
    public int start;

    @Property
    public int end;

    @Property
    public boolean value;

    @Property
    public long startDate;

    @Generated(hash = 241497469)
    public DestineTime(long id, long endDate, int start, int end, boolean value,
            long startDate) {
        this.id = id;
        this.endDate = endDate;
        this.start = start;
        this.end = end;
        this.value = value;
        this.startDate = startDate;
    }

    @Generated(hash = 485721874)
    public DestineTime() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEndDate() {
        return this.endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return this.end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public long getStartDate() {
        return this.startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

}
