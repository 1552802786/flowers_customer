package com.yuangee.flower.customer.network.api;

import com.google.gson.JsonElement;
import com.yuangee.flower.customer.entity.AreaResult;
import com.yuangee.flower.customer.entity.BannerBean;
import com.yuangee.flower.customer.entity.CartItem;
import com.yuangee.flower.customer.entity.Coupon;
import com.yuangee.flower.customer.entity.CustomerOrder;
import com.yuangee.flower.customer.entity.Express;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.entity.HadOpenArea;
import com.yuangee.flower.customer.entity.InformationEntity;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.entity.Message;
import com.yuangee.flower.customer.entity.ShopOrder;
import com.yuangee.flower.customer.entity.PersonResult;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.entity.Setting;
import com.yuangee.flower.customer.entity.Shop;
import com.yuangee.flower.customer.entity.ShopOrderCount;
import com.yuangee.flower.customer.entity.SystomConfig;
import com.yuangee.flower.customer.entity.UserMoneyEntity;
import com.yuangee.flower.customer.entity.UserScoreEntity;
import com.yuangee.flower.customer.entity.ZfbResult;
import com.yuangee.flower.customer.fragment.shopping.FeeCreatOrderResult;
import com.yuangee.flower.customer.result.BaseResult;
import com.yuangee.flower.customer.result.PageResult;
import com.yuangee.flower.customer.result.QueryCartResult;
import com.yuangee.flower.customer.result.SuppStatus;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by developerLzh on 2017/8/21 0021.
 */

public interface ApiService {

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @return
     */
    @GET("rest/system/sendSmsLogin")
    Observable<BaseResult<Object>> sendSmsLogin(@Query("phone") String phone);

    @FormUrlEncoded
    @POST("rest/member/login")
    Observable<BaseResult<Member>> login(@Field("phone") String phone,
                                         @Field("smsCode") String smsCode,
                                         @Field("registrationId") String registrationId);

    @FormUrlEncoded
    @POST("rest/aliLogin")
    Observable<BaseResult<Member>> alipayLogin(@Field("userId") String userId,
                                               @Field("code") String code);

    /**
     * 查询商品类型
     *
     * @return
     */
    @GET("rest/wares/findAllGenre")
    Observable<BaseResult<List<Genre>>> findAllGenre();

    /**
     * 根据商品类型id查询子类型
     *
     * @param id
     * @return
     */
    @GET("rest/wares/findGenreSub")
    Observable<BaseResult<Object>> findGenreSub(@Query("genreId") Long id);

    /**
     * 查询推荐商品
     *
     * @return
     */
    @GET("rest/wares/findAllRecommend")
    Observable<BaseResult<List<Recommend>>> findAllRecommend();

    /**
     * 根据条件分页查询商品
     *
     * @param genreName    大类型名称
     * @param genreSubName 子类型名称
     * @param params       搜索条件 （商品名称、类别名称、还有颜色）
     * @param offset       分页参数
     * @param Limit        分页参数
     * @return
     */
    @GET("rest/wares/findWares")
    Observable<BaseResult<PageResult<Goods>>> findWares(@Query("genreName") String genreName,
                                                        @Query("genreSubNames") String genreSubName,
                                                        @Query("params") String params,
                                                        @Query("offset") Long offset,
                                                        @Query("bespeak") String bespeak,
                                                        @Query("limit") Long Limit,
                                                        @Query("shopId") Long shopId);

    @GET("rest/wares/findWares")
    Observable<BaseResult<PageResult<Goods>>> searchWares(@Query("waresName") String waresName,
                                                          @Query("genreSubNames") String genreSubName,
                                                          @Query("params") String params,
                                                          @Query("offset") Long offset,
                                                          @Query("limit") Long Limit,
                                                          @Query("bespeak") String bespeak);

    /**
     * 根据用户id查询店铺信息
     *
     * @param memberId 用户id
     * @return
     */
    @GET("rest/wares/findShopByMemberId")
    Observable<BaseResult<Shop>> findShopByMemberId(@Query("memberId") Long memberId);


    /**
     * 添加商品
     *
     * @param waresImage   商品图片
     * @param genreId      所属类别id
     * @param genreName    所属类别名称
     * @param genreSubId   所属类别-子类id
     * @param genreSubName 所属类别-子类名称
     * @param name         商品名称
     * @param grade        商品等级 A-D
     * @param color        商品颜色
     * @param spec         商品规格
     * @param unit         商品单位
     * @param unitPrice    商品单价
     * @param salesVolume  可售量
     * @param shopId       供货商Id
     * @param shopName     供货商名称
     * @return
     */
    @Multipart
    @POST("rest/wares/createWares")
    Observable<BaseResult<Object>> createWares(@Part MultipartBody.Part waresImage,
                                               @Part MultipartBody.Part genreId,
                                               @Part MultipartBody.Part genreName,
                                               @Part MultipartBody.Part genreSubId,
                                               @Part MultipartBody.Part genreSubName,
                                               @Part MultipartBody.Part name,
                                               @Part MultipartBody.Part grade,
                                               @Part MultipartBody.Part color,
                                               @Part MultipartBody.Part spec,
                                               @Part MultipartBody.Part unit,
                                               @Part MultipartBody.Part unitPrice,
                                               @Part MultipartBody.Part salesVolume,
                                               @Part MultipartBody.Part shopId,
                                               @Part MultipartBody.Part shopName,
                                               @Part MultipartBody.Part auction,
                                               @Part MultipartBody.Part bespeak,
                                               @Part MultipartBody.Part bespeakNum,
                                               @Part MultipartBody.Part depict,
                                               @Part MultipartBody.Part memo,
                                               @Part MultipartBody.Part mumPrice,
                                               @Part MultipartBody.Part startDeliver);

    /**
     * 修改商品
     *
     * @param Id           商品Id
     * @param waresImage   商品图片
     * @param genreId      所属类别id
     * @param genreName    所属类别名称
     * @param genreSubId   所属类别-子类id
     * @param genreSubName 所属类别-子类名称
     * @param name         商品名称
     * @param grade        商品等级 A-D
     * @param color        商品颜色
     * @param spec         商品规格
     * @param unit         商品单位
     * @param unitPrice    商品单价
     * @param salesVolume  可售量
     * @param shopId       供货商Id
     * @param shopName     供货商名称
     * @return
     */
    @Multipart
    @POST("rest/wares/updateWares")
    Observable<BaseResult<Object>> updateWares(@Part MultipartBody.Part Id,
                                               @Part MultipartBody.Part waresImage,
                                               @Part MultipartBody.Part genreId,
                                               @Part MultipartBody.Part genreName,
                                               @Part MultipartBody.Part genreSubId,
                                               @Part MultipartBody.Part genreSubName,
                                               @Part MultipartBody.Part name,
                                               @Part MultipartBody.Part grade,
                                               @Part MultipartBody.Part color,
                                               @Part MultipartBody.Part spec,
                                               @Part MultipartBody.Part unit,
                                               @Part MultipartBody.Part unitPrice,
                                               @Part MultipartBody.Part salesVolume,
                                               @Part MultipartBody.Part shopId,
                                               @Part MultipartBody.Part shopName,
                                               @Part MultipartBody.Part auction,
                                               @Part MultipartBody.Part bespeak,
                                               @Part MultipartBody.Part bespeakNum,
                                               @Part MultipartBody.Part depict,
                                               @Part MultipartBody.Part memo,
                                               @Part MultipartBody.Part mumPrice,
                                               @Part MultipartBody.Part startDeliver);

    /**
     * 删除商品
     *
     * @param Id
     * @return
     */
    @GET("rest/wares/deleteWares")
    Observable<BaseResult<Object>> deleteWares(@Query("id") Long Id);

    /**
     * 根据条件分页查询订单
     *
     * @param status  订单状态 0：未支付1：未发货2：未收货3：确认收货4：预约订单，已支付预约金，未完成支付 5：订单取消
     * @param bespeak 是否为预约订单
     * @param shopId  店铺id
     * @param offset
     * @param limit
     * @return
     */
    @GET("rest/order/findByParams")
    Observable<BaseResult<PageResult<ShopOrder>>> findByParams(@Query("status") Integer status,
                                                               @Query("bespeak") Boolean bespeak,
                                                               @Query("shopId") Long shopId,
                                                               @Query("offset") Long offset,
                                                               @Query("limit") Long limit);

    /**
     * 添加商品到购物车
     *
     * @param memberId 用户id
     * @param waresId  是否为预约订单
     * @param num      商品数量
     * @return
     */
    @GET("rest/order/addCartItem")
    Observable<BaseResult<CartItem>> addCartItem(@Query("memberId") Long memberId,
                                                 @Query("waresId") Long waresId,
                                                 @Query("num") Integer num,
                                                 @Query("bespeak") Boolean bespeak
    );

    /**
     * 增加购物车中已有商品数量
     *
     * @param memberId 用户id
     * @param waresId  是否为预约订单
     * @param num      商品数量
     * @return
     */
    @GET("rest/order/cartItemAdd")
    Observable<BaseResult<CartItem>> cartItemAdd(@Query("itemId") Long memberId,
                                                 @Query("cartId") Long waresId,
                                                 @Query("num") Integer num);

    /**
     * 减少购物车中已有商品数量
     */
    @GET("rest/order/cartItemSub")
    Observable<BaseResult<CartItem>> cartItemSub(@Query("itemId") Long itemId,
                                                 @Query("cartId") Long cartId,
                                                 @Query("num") Integer num);

    /**
     * 删除购物车中商品
     *
     * @param itemId 购物车中商品id
     * @param cartId 购物车id
     * @return
     */
    @GET("rest/order/deleteCartItem")
    Observable<BaseResult<Object>> deleteCartItem(@Query("itemId") Long itemId,
                                                  @Query("cartId") Long cartId);

    /**
     * 查询购物车中的所有商品
     *
     * @param memberId
     * @return
     */
    @GET("rest/order/findByMemberId")
    Observable<BaseResult<QueryCartResult>> queryCart(@Query("memberId") Long memberId);

    /**
     * 下单
     *
     * @param memberId        用户id
     * @param receiverName    收货人姓名
     * @param receiverPhone   收货人手机号
     * @param receiverAddress 收货人地址
     * @param expressId       快递id
     * @return
     */
    @FormUrlEncoded
    @POST("rest/order/confirmOrderMulti")
    Observable<BaseResult<CustomerOrder>> confirmOrderMulti(@Field("memberId") Long memberId,
                                                            @Field("receiverName") String receiverName,
                                                            @Field("receiverPhone") String receiverPhone,
                                                            @Field("receiverAddress") String receiverAddress,
                                                            @Field("expressId") Long expressId,
                                                            @Field("couponId") Long couponId,
                                                            @Field("cartItemIds") Long[] cartItemIds,
                                                            @Field("memo") String memo,
                                                            @Field("startLat") Double lat,
                                                            @Field("startLng") Double lng);

    /**
     * @param memberId
     * @param receiverName
     * @param receiverPhone
     * @param receiverAddress
     * @param expressId
     * @param bespeakDate     预约日期
     * @return
     */
    @FormUrlEncoded
    @POST("rest/order/bespeakOrderMulti")
    Observable<BaseResult<CustomerOrder>> bespeakOrderMulti(@Field("memberId") Long memberId,
                                                            @Field("receiverName") String receiverName,
                                                            @Field("receiverPhone") String receiverPhone,
                                                            @Field("receiverAddress") String receiverAddress,
                                                            @Field("expressId") Long expressId,
                                                            @Field("bespeakDate") String bespeakDate,
                                                            @Field("couponId") Long couponId,
                                                            @Field("cartItemIds") Long[] cartItemIds
    );

    @GET("rest/shop/shopSettleInfo")
    Observable<BaseResult<ShopOrderCount>> getShopOrderCount(@Query("shopId") Long shopId,
                                                             @Query("startDate") String start_date,
                                                             @Query("endDate") String end_date);

    /**
     * 查询单个用户
     *
     * @param id 用户id
     * @return
     */
    @GET("rest/member/findById")
    Observable<BaseResult<PersonResult>> findById(@Query("id") Long id);

    /**
     * 获取系统确认字符串
     *
     * @return
     */
    @GET("rest/system/conf")
    Observable<BaseResult<SystomConfig>> systemConfig();

    /**
     * 修改用户信息
     *
     * @param id     用户id
     * @param name   用户名称
     * @param gender 性别
     * @param phone  电话
     * @param email  邮箱
     * @param photo  头像
     * @return
     */
    @Multipart
    @POST("rest/member/updateMember")
    Observable<BaseResult<Object>> updateMember(@Part MultipartBody.Part id,
                                                @Part MultipartBody.Part name,
                                                @Part MultipartBody.Part gender,
                                                @Part MultipartBody.Part phone,
                                                @Part MultipartBody.Part email,
                                                @Part MultipartBody.Part photo,
                                                @Part MultipartBody.Part deliverId
    );

    /**
     * 创建收货地址
     *
     * @param shippingId     收货人id
     * @param shippingName   收货人名称
     * @param phone          收货人电话
     * @param pro            省
     * @param city           市
     * @param area           区县
     * @param street         街道
     * @param defaultAddress 是否设为默认地址
     * @return
     */
    @FormUrlEncoded
    @POST("rest/member/createMemberAddress")
    Observable<BaseResult<Object>> createMemberAddress(@Field("shippingId") Long shippingId,
                                                       @Field("shippingName") String shippingName,
                                                       @Field("shippingPhone") String phone,
                                                       @Field("pro") String pro,
                                                       @Field("city") String city,
                                                       @Field("area") String area,
                                                       @Field("street") String street,
                                                       @Field("latitude") Double lat,
                                                       @Field("longitude") Double lng,
                                                       @Field("defaultAddress") Boolean defaultAddress
    );

    /**
     * 创建收货地址
     *
     * @param shippingId     收货人id
     * @param shippingName   收货人名称
     * @param phone          收货人电话
     * @param pro            省
     * @param city           市
     * @param area           区县
     * @param street         街道
     * @param defaultAddress 是否设为默认地址
     * @return
     */
    @FormUrlEncoded
    @POST("rest/member/updateMemberAddress")
    Observable<BaseResult<Object>> updateMemberAddress(@Field("id") Long id,
                                                       @Field("shippingId") Long shippingId,
                                                       @Field("shippingName") String shippingName,
                                                       @Field("shippingPhone") String phone,
                                                       @Field("pro") String pro,
                                                       @Field("city") String city,
                                                       @Field("area") String area,
                                                       @Field("street") String street,
                                                       @Field("latitude") Double lat,
                                                       @Field("longitude") Double lng,
                                                       @Field("defaultAddress") Boolean defaultAddress
    );

    /**
     * 获取确认订单的费用
     *
     * @param itmeIds
     * @param expressId
     * @param expressName
     * @param expressPhone
     * @param address
     * @param lat
     * @param lng
     * @return
     */
    @GET("rest/order/computeOtherPrice")
    Observable<BaseResult<FeeCreatOrderResult>> getAllfeeCreatOrder(@Query("itmeIds") Long[] itmeIds,
                                                                    @Query("expressId") Long expressId,
                                                                    @Query("expressName") String expressName,
                                                                    @Query("expressPhone") String expressPhone,
                                                                    @Query("address") String address,
                                                                    @Query("lat") Double lat,
                                                                    @Query("lng") Double lng);

    /**
     * 申请成为供货商
     *
     * @param shopName          店铺名称
     * @param simpleShopName    店铺简称
     * @param name              法人名称
     * @param phone             电话
     * @param address           地址
     * @param description       供应品种
     * @param bankNo            银行卡号
     * @param bankName          开户行
     * @param openAccountName   开户名称
     * @param idCardNo          身份证号码
     * @param idImgFront        身份证正面
     * @param idImgBack         身份证反面
     * @param photo             全身照
     * @param businessLicence   营业执照
     * @param scene1            公司照片
     * @param businessLicenceNo 营业执照号
     * @param memberId          用户id 类型 0 个人 1 企业
     * @param type
     * @return
     */
    @Multipart
    @POST("rest/shop/shopApply")
    Observable<BaseResult<Object>> shopApply(@Part MultipartBody.Part shopName,
                                             @Part MultipartBody.Part simpleShopName,
                                             @Part MultipartBody.Part name,
                                             @Part MultipartBody.Part phone,
                                             @Part MultipartBody.Part address,
                                             @Part MultipartBody.Part description,
                                             @Part MultipartBody.Part bankNo,
                                             @Part MultipartBody.Part bankName,
                                             @Part MultipartBody.Part openAccountName,
                                             @Part MultipartBody.Part idCardNo,
                                             @Part MultipartBody.Part idImgFront,
                                             @Part MultipartBody.Part idImgBack,
                                             @Part MultipartBody.Part photo,
                                             @Part MultipartBody.Part businessLicence,
                                             @Part MultipartBody.Part scene1,
                                             @Part MultipartBody.Part businessLicenceNo,
                                             @Part MultipartBody.Part memberId,
                                             @Part MultipartBody.Part type
    );

    /**
     * 查询所有快递
     *
     * @return
     */
    @GET("rest/system/findByExpressDeliveryAll")
    Observable<BaseResult<List<Express>>> findByExpressDeliveryAll();

    /**
     * 查询用户所有通知消息
     *
     * @param memberId 用户id
     * @return
     */
    @GET("rest/notice/findByMemberId")
    Observable<BaseResult<List<Message>>> findAllMessage(@Query("memberId") Long memberId);

    /**
     * 读取通知消息
     *
     * @param memberId 用户id
     * @param noticeId 通知id
     * @return
     */
    @FormUrlEncoded
    @POST("rest/notice/readMemberNotice")
    Observable<BaseResult<Object>> readMemberNotice(@Field("memberId") Long memberId,
                                                    @Field("noticeId") Long noticeId);


    /**
     * 根据商铺id查询商品
     */
    @GET("rest/wares/findWares")
    Observable<BaseResult<PageResult<Goods>>> findWares(@Query("shopId") Long shopId,
                                                        @Query("offset") Integer offset,
                                                        @Query("waresName") String waresName,
                                                        @Query("limit") Integer limit);

    /**
     * 获取banner
     */
    @GET("rest/activity/findActivityShowAll")
    Observable<BaseResult<List<BannerBean>>> getBannerData();

    /**
     * 删除用户地址
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("rest/customer/deleteMemberAddress")
    Observable<BaseResult<Object>> deleteMemberAddress(@Field("id") Long id);

    /**
     * @param orderId
     * @param status  5取消订单  2发货
     * @return
     */
    @GET("rest/order/updateOrder")
    Observable<BaseResult<Object>> updateOrder(@Query("orderId") Long orderId,
                                               @Query("status") Integer status);


    /**
     * 更新大订单信息
     *
     * @param orderId
     * @param status
     * @return
     */
    @GET("rest/order/updateStatusRecord")
    Observable<BaseResult<Object>> updateBigOrder(@Query("id") Long orderId,
                                                  @Query("status") Integer status);

    @GET("rest/activity/findMemberIdByCoupon")
    Observable<BaseResult<List<Coupon>>> findCoupon(@Query("memberId") Long memberId);

    @FormUrlEncoded
    @POST("rest/pay/immediate/alipay")
    Observable<BaseResult<ZfbResult>> payJishiSingleZfb(@Field("orderRecordId") Long orderId,
                                                        @Field("type") Integer type);

    @FormUrlEncoded
    @POST("rest/pay/immediate/wxpay")
    Observable<BaseResult<JsonElement>> payJishiSingleWx(@Field("orderRecordId") Long orderId,
                                                         @Field("type") Integer type);

    @FormUrlEncoded
    @POST("rest/pay/reservation/alipay")
    Observable<BaseResult<ZfbResult>> payYuyueSingleZfb(@Field("orderRecordId") Long orderId);

    @FormUrlEncoded
    @POST("rest/pay/reservation/wxpay")
    Observable<BaseResult<JsonElement>> payYuyueSingleWx(@Field("orderRecordId") Long orderId);


    /**
     * 系统配置
     *
     * @return
     */
    @GET("rest/rest/system/conf")
    Observable<BaseResult<Setting>> getSetting();

    @GET("rest/shop/findMemberIdByShopApply")
    Observable<BaseResult<SuppStatus>> getSuppStatus(@Query("memberId") Long memberId);

    @FormUrlEncoded
    @POST("rest/system/createFeedback")
    Observable<BaseResult<Object>> feedback(@Field("memberName") String memberName,
                                            @Field("memberPhone") String memberPhone,
                                            @Field("content") String content
    );

    @FormUrlEncoded
    @POST("rest/order/customOrder")
    Observable<BaseResult<Object>> cusOrder(@Field("name") String name,
                                            @Field("phone") String phone,
                                            @Field("memberId") Long memberId,
                                            @Field("waresJson") String waresJson,
                                            @Field("shopId") Long shopIp
    );

    /**
     * 查询大订单
     *
     * @param status
     * @param bespeak
     * @param memberId
     * @param offset
     * @param limit
     * @return
     */
    @GET("rest/order/findOrderRecordByParam")
    Observable<BaseResult<PageResult<CustomerOrder>>> findMemberOrder(@Query("status") Integer status,
                                                                      @Query("bespeak") Boolean bespeak,
                                                                      @Query("memberId") Long memberId,
                                                                      @Query("offset") Long offset,
                                                                      @Query("limit") Long limit);

    @FormUrlEncoded
    @POST("rest/order/comfrimBespokePrice")
    Observable<BaseResult<CustomerOrder>> confirmMoney(@Field("orderRecordId") Long orderId);

    @GET("rest/openArea/listAll")
    Observable<BaseResult<PageResult<HadOpenArea>>> searchOpenArea();

    /**
     * 根据用户id查询店铺信息
     *
     * @param city_name 城市名
     * @return
     */
    @GET("rest/openArea/isCityOpen")
    Observable<BaseResult<AreaResult>> queryOpenCity(@Query("city") String city_name);

    /**
     * 查询用户积分明细
     */
    @FormUrlEncoded
    @POST("rest/member/listMemberIntegral")
    Observable<BaseResult<PageResult<UserScoreEntity>>> queryMyScore(@Query("memberId")Long id);
    /**
     * 查询用户余额明细
     */
    @FormUrlEncoded
    @POST("rest/member/listMemberRecharge")
    Observable<BaseResult<PageResult<UserMoneyEntity>>> queryMyMoney(@Query("memberId")Long id);
    /**
     * 查询仔细
     */
    @GET("rest/article/list")
    Observable<BaseResult<PageResult<InformationEntity>>> queryMessageInfo();

}
