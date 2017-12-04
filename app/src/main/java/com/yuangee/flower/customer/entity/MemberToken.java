package com.yuangee.flower.customer.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by developerLzh on 2017/10/26 0026.
 */
@Entity
public class MemberToken {

    @Id
    public long id;

    @Property
    public long memberId;

    @Property
    public long deathDate;

    @Property
    public String token;

    @Generated(hash = 616744864)
    public MemberToken(long id, long memberId, long deathDate, String token) {
        this.id = id;
        this.memberId = memberId;
        this.deathDate = deathDate;
        this.token = token;
    }

    @Generated(hash = 955579968)
    public MemberToken() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMemberId() {
        return this.memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public long getDeathDate() {
        return this.deathDate;
    }

    public void setDeathDate(long deathDate) {
        this.deathDate = deathDate;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
