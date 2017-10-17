package com.yb.btcinfo.repository.net;


import com.yb.btcinfo.repository.entity.IndexEntity;
import com.yb.btcinfo.repository.entity.PlatformEntity;
import com.yb.btcinfo.repository.entity.PlatformListEntity;
import com.yb.btcinfo.repository.entity.UserEntity;
import com.yb.ilibray.BuildConfig;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import mvp.data.net.DataResponse;
import mvp.data.net.converter.GsonConverterFactory;
import mvp.data.net.converter.Retry;
import mvp.data.net.converter.StringConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ericYang on 2017/5/18.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public interface RemoteApiService {
//    String BASEURL="http://10.0.2.2:8080";
    String BASEURL="http://47.94.92.29";
//    String BASEURL="http://www.baidu.com";

    @POST("/user/{id}")
    Observable<DataResponse<UserEntity>> getUser(@Path("id") int id);
    @GET("/user/users")
    Observable<DataResponse<List<UserEntity>>> getUsers();
    @GET("/api/News/GetIndexNews")
    @Retry(count = 3)
    Observable<DataResponse<List<IndexEntity>>> getIndexNews(@Query("n") String number);
    @GET("/api/Platform/GetSupportPlatform")
    Observable<DataResponse<List<PlatformEntity>>> getSupportPlatform();
    @GET("/api/news/GetPlatformNews")
    Observable<DataResponse<List<PlatformListEntity>>> getPlatformNews(@Query("platformid") String platformId, @Query("n")String pageSize);



    /**
     * 创建一个API服务
     */
    class Factory {
        private Factory() {  }

        public static RemoteApiService createService( ) {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.readTimeout(10, TimeUnit.SECONDS);
            builder.connectTimeout(9, TimeUnit.SECONDS);

            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(interceptor);
            }

//            builder.addInterceptor(new HeaderInterceptor());
            OkHttpClient client = builder.build();
            Retrofit retrofit =
                    new Retrofit.Builder().baseUrl(BASEURL)
                            .client(client)
                            .addConverterFactory(StringConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();
            return retrofit.create(RemoteApiService.class);
        }
    }
}
