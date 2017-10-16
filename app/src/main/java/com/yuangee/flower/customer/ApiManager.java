package com.yuangee.flower.customer;

import android.app.Application;

import com.yuangee.flower.customer.network.api.ApiService;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import it.sauronsoftware.base64.Base64;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by developerLzh on 2017/6/13 0013.
 */

public class ApiManager {

    public ApiService api;

    /**
     * 内部静态类实现单例,且在第一次使用时才加载.
     */
    private static class SingletonHolder {
        private static ApiManager INSTANCE = new ApiManager();
    }

    /**
     * 获取单例实例.
     *
     * @return Api对象
     */
    public static ApiManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 私有化构造方法,配置okhttpClient以及retrofit.
     */
    private ApiManager() {
        File cacheFile = new File(FlowerApp.getContext().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 2); //2M
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();   //拦截器用来输出请求日志方便调试
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); //日志输出等级为BODY(打印请求和返回值的头部和body信息)

        final Long passengerId = App.getPassengerId();
        final String token = App.me().getSharedPreferences().getString("token", "");
        //创建okhttp客户端
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(16000, TimeUnit.MILLISECONDS)
                .connectTimeout(16000, TimeUnit.MILLISECONDS)
                .addInterceptor(logInterceptor) //添加日志拦截器,进行输出日志
                .retryOnConnectionFailure(false)    //失败不重连
                .cache(cache)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("X-token", new String(Base64.encode((passengerId + "_" + token).getBytes())))
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();

        //创建api
        api = createApi(okHttpClient, Constant.BASE_URL, ApiService.class);
    }

    /**
     * 创建一个网络访问的api.
     *
     * @param okHttpClient 内置的okHttp客户端
     * @param hostUrl      该api的host地址
     * @param service      api的class类型
     * @param <T>          实际需要返回类型
     * @return 实际返回的api实例
     */
    private <T> T createApi(OkHttpClient okHttpClient, String hostUrl, Class<T> service) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(hostUrl)
                .build();

        return retrofit.create(service);
    }

    /**
     * 重新实例化okhttpclient
     */
    public void addHeader() {
        SingletonHolder.INSTANCE = new ApiManager();
    }

}
