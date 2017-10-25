package com.yuangee.flower.customer.network.api;

import com.yuangee.flower.customer.entity.BannerBean;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.entity.Goods;
import com.yuangee.flower.customer.entity.Recommend;
import com.yuangee.flower.customer.entity.Type;
import com.yuangee.flower.customer.result.BaseResult;
import com.yuangee.flower.customer.result.PageResult;

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
    Observable<BaseResult<Object>> login(@Field("phone") String phone,
                                         @Field("smsCode") String smsCode);

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
                                                        @Query("genreSubName") String genreSubName,
                                                        @Query("params") String params,
                                                        @Query("offset") Long offset,
                                                        @Query("limit") Long Limit);

    /**
     * 根据用户id查询店铺信息
     *
     * @param memberId 用户id
     * @return
     */
    @GET("rest/wares/findShopByMemberId")
    Observable<BaseResult<Object>> findShopByMemberId(@Query("memberId") Long memberId);

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
                                               @Part MultipartBody.Part shopName);

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
    @POST("rest/wares/findShopByMemberId")
    Observable<BaseResult<Object>> findShopByMemberId(@Part MultipartBody.Part Id,
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
                                                      @Part MultipartBody.Part shopName);

    /**
     * 删除商品
     *
     * @param Id
     * @return
     */
    @GET("rest/wares/deleteWares")
    Observable<BaseResult<Object>> deleteWares(@Query("deleteWares") Long Id);

    /**
     * 根据条件分页查询订单
     *
     * @param status   订单状态 0：未支付1：未发货2：未收货3：确认收货4：预约订单，已支付预约金，未完成支付 5：订单取消
     * @param bespeak  是否为预约订单
     * @param memberId 客户id
     * @param shopId   店铺id
     * @param offset
     * @param Limit
     * @return
     */
    @GET("rest/order/findByParams")
    Observable<BaseResult<Object>> findByParams(@Query("status") Integer status,
                                                @Query("bespeak") Boolean bespeak,
                                                @Query("memberId") Long memberId,
                                                @Query("shopId") Long shopId,
                                                @Query("offset") Long offset,
                                                @Query("Limit") Long Limit);

    /**
     * 添加商品到购物车
     *
     * @param memberId 用户id
     * @param waresId  是否为预约订单
     * @param num      商品数量
     * @return
     */
    @GET("rest/order/addCartItem")
    Observable<BaseResult<Object>> addCartItem(@Query("memberId") Long memberId,
                                               @Query("waresId") Long waresId,
                                               @Query("num") Integer num);

    /**
     * 增加购物车中已有商品数量
     *
     * @param memberId 用户id
     * @param waresId  是否为预约订单
     * @param num      商品数量
     * @return
     */
    @GET("rest/order/cartItemAdd")
    Observable<BaseResult<Object>> cartItemAdd(@Query("memberId") Long memberId,
                                               @Query("waresId") Long waresId,
                                               @Query("num") Integer num);

    /**
     * 减少购物车中已有商品数量
     *
     * @param memberId 用户id
     * @param waresId  是否为预约订单
     * @param num      商品数量
     * @return
     */
    @GET("rest/order/cartItemSub")
    Observable<BaseResult<Object>> cartItemSub(@Query("memberId") Long memberId,
                                               @Query("waresId") Long waresId,
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
    Observable<BaseResult<Object>> queryCart(@Query("memberId") Long memberId);

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
    @GET("rest/order/confirmOrderMulti")
    Observable<BaseResult<Object>> confirmOrderMulti(@Query("memberId") Long memberId,
                                                     @Query("receiverName") String receiverName,
                                                     @Query("receiverPhone") String receiverPhone,
                                                     @Query("receiverAddress") String receiverAddress,
                                                     @Query("expressId") Long expressId);

    /**
     * 查询单个用户
     *
     * @param id 用户id
     * @return
     */
    @GET("rest/member/findById")
    Observable<BaseResult<Object>> findById(@Query("id") Long id);

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
                                                @Part MultipartBody.Part photo
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
                                                       @Field("phone") String phone,
                                                       @Field("pro") String pro,
                                                       @Field("city") String city,
                                                       @Field("area") String area,
                                                       @Field("street") String street,
                                                       @Field("defaultAddress") Boolean defaultAddress
    );

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
    Observable<BaseResult<Object>> findByExpressDeliveryAll();

    /**
     * 查询用户所有通知消息
     *
     * @param memberId 用户id
     * @return
     */
    @GET("rest/notice/findByMemberId")
    Observable<BaseResult<Object>> findByExpressDeliveryAll(@Query("memberId") Long memberId);

    /**
     * 读取通知消息
     *
     * @param memberId 用户id
     * @param noticeId 通知id
     * @return
     */
    @GET("rest/notice/readMemberNotice")
    Observable<BaseResult<Object>> readMemberNotice(@Query("memberId") Long memberId,
                                                    @Query("noticeId") Long noticeId);


    /**
     * 获取banner
     */
    @GET("driver/api/rest/v4/acceptTask")
    Observable<BaseResult<List<BannerBean>>> getBannerData();

    /**
     * 获取订单信息
     */
    @GET("driver/api/rest/v4/acceptTask")
    Observable<BaseResult<List<Recommend>>> getOrderData(@Query("customerId") long customerId);

    /**
     * 获取订单信息
     */
    @GET("driver/api/rest/v4/acceptTask")
    Observable<BaseResult<List<Type>>> getTypeData();

    /**
     * 获取商品信息接口
     */
    @GET("driver/api/rest/v4/acceptTask")
    Observable<BaseResult<List<Goods>>> getGoodsData(@Query("page") int page, @Query("Limit") int Limit);


}
