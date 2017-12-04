package com.yuangee.flower.customer.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by developerLzh on 2017/10/26 0026.
 */
@Entity
public class Member {

    @Id
    public long id;

    @Property
    public String name;

    @Property
    public String userName;

    @Property
    public String passWord;

    @Property
    public String phone;

    @Property
    public String email;

    @Property
    public String photo;

    @Property
    public boolean gender;

    @Property
    public String address;

    @Property
    public String type;

    @Property
    public boolean inBlacklist;

    @Property
    public boolean isRecycle;

    @Property
    public boolean inFirst;

    @Property
    public double balance;

//    @ToMany(referencedJoinProperty = "memberId")  考虑到只需要保存一个会员的信息，所以没用一对多关联
    @Transient
    public List<Address> memberAddressList;//客户的收货地址

    @Transient
    public List<Express> expressDelivery;//收货方式

    @Transient
    public List<Coupon> listCoupon;//优惠券列表

    @Transient
    public Shop shop;//商店

    @Transient
    public MemberToken memberToken;//token

    @Generated(hash = 388917339)
    public Member(long id, String name, String userName, String passWord,
            String phone, String email, String photo, boolean gender,
            String address, String type, boolean inBlacklist, boolean isRecycle,
            boolean inFirst, double balance) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.passWord = passWord;
        this.phone = phone;
        this.email = email;
        this.photo = photo;
        this.gender = gender;
        this.address = address;
        this.type = type;
        this.inBlacklist = inBlacklist;
        this.isRecycle = isRecycle;
        this.inFirst = inFirst;
        this.balance = balance;
    }

    @Generated(hash = 367284327)
    public Member() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return this.passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean getGender() {
        return this.gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getInBlacklist() {
        return this.inBlacklist;
    }

    public void setInBlacklist(boolean inBlacklist) {
        this.inBlacklist = inBlacklist;
    }

    public boolean getIsRecycle() {
        return this.isRecycle;
    }

    public void setIsRecycle(boolean isRecycle) {
        this.isRecycle = isRecycle;
    }

    public boolean getInFirst() {
        return this.inFirst;
    }

    public void setInFirst(boolean inFirst) {
        this.inFirst = inFirst;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

}
