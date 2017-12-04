package com.yuangee.flower.customer.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by developerLzh on 2017/10/26 0026.
 * 商铺
 */
@Entity
public class Shop {
    @Id
    public long id;

    @Property
    public String address;

    @Property
    public String simpleShopName;

    @Property
    public String shopName;

    @Property
    public Long memberId;

    @Property
    public Double money;

    @Property
    public Integer status;

    @Property
    public String idCardNo;

    @Property
    public String idImgBack;

    @Property
    public String photo;

    @Property
    public String idImgFront;

    @Property
    public String businessLicence;

    @Property
    public String scene1;

    @Property
    public String scene2;

    @Property
    public String description;

    @Property
    public String phone;

    @Property
    public String name;

    @Property
    public String bankNo;

    @Property
    public String bankName;

    @Property
    public String openAccountName;

    @Property
    public String businessLicenceNo;

    @Generated(hash = 911674282)
    public Shop(long id, String address, String simpleShopName, String shopName,
            Long memberId, Double money, Integer status, String idCardNo,
            String idImgBack, String photo, String idImgFront,
            String businessLicence, String scene1, String scene2,
            String description, String phone, String name, String bankNo,
            String bankName, String openAccountName, String businessLicenceNo) {
        this.id = id;
        this.address = address;
        this.simpleShopName = simpleShopName;
        this.shopName = shopName;
        this.memberId = memberId;
        this.money = money;
        this.status = status;
        this.idCardNo = idCardNo;
        this.idImgBack = idImgBack;
        this.photo = photo;
        this.idImgFront = idImgFront;
        this.businessLicence = businessLicence;
        this.scene1 = scene1;
        this.scene2 = scene2;
        this.description = description;
        this.phone = phone;
        this.name = name;
        this.bankNo = bankNo;
        this.bankName = bankName;
        this.openAccountName = openAccountName;
        this.businessLicenceNo = businessLicenceNo;
    }

    @Generated(hash = 633476670)
    public Shop() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSimpleShopName() {
        return this.simpleShopName;
    }

    public void setSimpleShopName(String simpleShopName) {
        this.simpleShopName = simpleShopName;
    }

    public String getShopName() {
        return this.shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Long getMemberId() {
        return this.memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Double getMoney() {
        return this.money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIdCardNo() {
        return this.idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getIdImgBack() {
        return this.idImgBack;
    }

    public void setIdImgBack(String idImgBack) {
        this.idImgBack = idImgBack;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getIdImgFront() {
        return this.idImgFront;
    }

    public void setIdImgFront(String idImgFront) {
        this.idImgFront = idImgFront;
    }

    public String getBusinessLicence() {
        return this.businessLicence;
    }

    public void setBusinessLicence(String businessLicence) {
        this.businessLicence = businessLicence;
    }

    public String getScene1() {
        return this.scene1;
    }

    public void setScene1(String scene1) {
        this.scene1 = scene1;
    }

    public String getScene2() {
        return this.scene2;
    }

    public void setScene2(String scene2) {
        this.scene2 = scene2;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankNo() {
        return this.bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getOpenAccountName() {
        return this.openAccountName;
    }

    public void setOpenAccountName(String openAccountName) {
        this.openAccountName = openAccountName;
    }

    public String getBusinessLicenceNo() {
        return this.businessLicenceNo;
    }

    public void setBusinessLicenceNo(String businessLicenceNo) {
        this.businessLicenceNo = businessLicenceNo;
    }
}
