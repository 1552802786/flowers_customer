package com.yuangee.flower.customer.entity;

import java.util.List;

/**
 * Created by developerLzh on 2017/10/26 0026.
 */

public class Member {

    public long id;

    public String name;

    public String userName;

    public String passWord;

    public String phone;

    public String email;

    public String photo;

    public boolean gender;

    public String address;

    public String type;

    public boolean inBlacklist;

    public boolean isRecycle;

    public boolean inFirst;

    public double balance;

    public List<Address> memberAddressList;

    public Shop shop;

    public MemberToken memberToken;

}
