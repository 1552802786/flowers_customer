package com.yuangee.flower.customer.entity;

import java.io.Serializable;

/**
 * Created by admin on 2018/1/22.
 */

public class SystomConfig implements Serializable{
      public UserAgreeMent agreement;

    public UserAgreeMent getAgreement() {
        return agreement;
    }

    public void setAgreement(UserAgreeMent agreement) {
        this.agreement = agreement;
    }
}
