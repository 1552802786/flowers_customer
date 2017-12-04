package com.yuangee.flower.customer.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.db.DbHelper;
import com.yuangee.flower.customer.entity.Address;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.entity.PersonResult;
import com.yuangee.flower.customer.network.HaveErrSubscriberListener;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liuzihao on 2017/12/4.
 */

public class PersonUtil {
    public static void getMemberInfo(RxManager rxManager, Context context, Long memberId,
                                     final OnGetMember onGetMember) {
        Observable<PersonResult> observable = ApiManager.getInstance().api
                .findById(memberId)
                .map(new HttpResultFunc<PersonResult>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        rxManager.add(observable.subscribe(new MySubscriber<>(context, true,
                false, new HaveErrSubscriberListener<PersonResult>() {
            @Override
            public void onNext(PersonResult result) {
                //auctionTime
                DbHelper.getInstance().getAuctionTimeLongDBManager().deleteAll();
                if (null != result.auctionTime) {
                    DbHelper.getInstance().getAuctionTimeLongDBManager().insert(result.auctionTime);
                }

                //businessTime
                DbHelper.getInstance().getBusinessTimeLongDBManager().deleteAll();
                if (null != result.businessTime) {
                    DbHelper.getInstance().getBusinessTimeLongDBManager().insert(result.businessTime);
                }

                //destineTime
                DbHelper.getInstance().getDestineTimeLongDBManager().deleteAll();
                if (null != result.destineTime) {
                    DbHelper.getInstance().getDestineTimeLongDBManager().insert(result.destineTime);
                }

                //updateWares
                DbHelper.getInstance().getUpdateWaresLongDBManager().deleteAll();
                if (null != result.updateWares) {
                    DbHelper.getInstance().getUpdateWaresLongDBManager().insert(result.updateWares);
                }

                //member
                DbHelper.getInstance().getMemberLongDBManager().deleteAll();
                DbHelper.getInstance().getShopLongDBManager().deleteAll();
                DbHelper.getInstance().getExpressLongDBManager().deleteAll();
                DbHelper.getInstance().getAddressLongDBManager().deleteAll();
                DbHelper.getInstance().getCouponLongDBManager().deleteAll();
                if (null != result.member) {
                    DbHelper.getInstance().getMemberLongDBManager().insert(result.member);
                    if (null != result.member.shop) {
                        DbHelper.getInstance().getShopLongDBManager().insert(result.member.shop);
                    }
                    if (null != result.member.expressDelivery) {
                        DbHelper.getInstance().getExpressLongDBManager().insertInTx(result.member.expressDelivery);
                    }
                    if (null != result.member.memberAddressList) {
                        DbHelper.getInstance().getAddressLongDBManager().insertInTx(result.member.memberAddressList);
                    }
                    if (null != result.member.listCoupon) {
                        DbHelper.getInstance().getCouponLongDBManager().insertInTx(result.member.listCoupon);
                    }
                    if (null != onGetMember) {
                        onGetMember.onSuccess(result.member);
                    }
                } else {
                    onGetMember.onFailed();
                }
            }

            @Override
            public void onError(int code) {
                onGetMember.onFailed();
            }
        })));
    }

    public interface OnGetMember {
        void onSuccess(Member member);

        void onFailed();
    }
}
