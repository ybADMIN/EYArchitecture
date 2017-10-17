package com.yb.btcinfo.repository.net;

import com.yb.btcinfo.repository.entity.UserEntity;
import com.yb.ilibray.BuildConfig;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import mvp.data.net.DataResponse;
import mvp.data.net.converter.GsonConverterFactory;
import mvp.data.net.converter.StringConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ericYang on 2017/10/12.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public interface UserApiService {
    String BASEURL="http://47.94.92.29";

    @POST("/user/{id}")
    Observable<DataResponse<UserEntity>> getUser(@Path("id") int id);
    @GET("/user/users")
    Observable<DataResponse<List<UserEntity>>> getUsers();


    /**
     * 创建一个API服务
     */
    class Factory {
        private Factory() {  }

        public static UserApiService createService( ) {
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
            return retrofit.create(UserApiService.class);
        }
    }
}
