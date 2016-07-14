package com.popcorp.parser.skidkaonline.net;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class APIFactory {

    private static API api;
    private static Scheduler scheduler;

    public static API getAPI(){
        if (api == null) {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            okHttpClientBuilder.connectTimeout(60, TimeUnit.SECONDS);
            okHttpClientBuilder.readTimeout(60, TimeUnit.SECONDS);

            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClientBuilder.build())
                    .baseUrl("https://skidkaonline.ru")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

            api = retrofit.create(API.class);
        }
        return api;
    }

    public static Scheduler getScheduler(){
        if (scheduler == null){
            scheduler = Schedulers.from(Executors.newFixedThreadPool(10));
        }
        return scheduler;
    }
}
