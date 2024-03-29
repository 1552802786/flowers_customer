package com.yuangee.flower.customer.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by developerLzh on 2017/10/26 0026.
 */

@Entity
public class Address implements Serializable{

    @Id
    public long id;

    @Transient
    public static final long serialVersionUID = 1L;

    @Property
    public long shippingId;//收货人id

    @Property
    public String shippingName;//收货人名称

    @Property
    public String shippingPhone;//收货人电话

    @Property
    public String pro;//省

    @Property
    public String city;//市

    @Property
    public String area;

    @Property
    public String street;

    @Property
    public int type;
    @Property
    public double longitude;
    @Property
    public double latitude;
    @Property
    public boolean defaultAddress;//是否是默认地址

    @Transient
    public boolean isSelected;

    @Generated(hash = 1767467914)
    public Address(long id, long shippingId, String shippingName,
            String shippingPhone, String pro, String city, String area,
            String street, int type, double longitude, double latitude,
            boolean defaultAddress) {
        this.id = id;
        this.shippingId = shippingId;
        this.shippingName = shippingName;
        this.shippingPhone = shippingPhone;
        this.pro = pro;
        this.city = city;
        this.area = area;
        this.street = street;
        this.type = type;
        this.longitude = longitude;
        this.latitude = latitude;
        this.defaultAddress = defaultAddress;
    }

    @Generated(hash = 388317431)
    public Address() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getShippingId() {
        return this.shippingId;
    }

    public void setShippingId(long shippingId) {
        this.shippingId = shippingId;
    }

    public String getShippingName() {
        return this.shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public String getShippingPhone() {
        return this.shippingPhone;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public String getPro() {
        return this.pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return this.area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public boolean getDefaultAddress() {
        return this.defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}
