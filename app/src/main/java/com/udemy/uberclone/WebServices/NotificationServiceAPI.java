package com.udemy.uberclone.WebServices;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationServiceAPI {

    private static NotificationServiceAPI instance;
    private Retrofit retrofit;
    private HttpLoggingInterceptor httpLoggingInterceptor;
    private OkHttpClient.Builder httpClientBuilder;
    private static final String URL_BASE = "https://fcm.googleapis.com";

    private NotificationServiceAPI(){
        httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor);
        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public <S> S getAPI(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }

    public static NotificationServiceAPI getInstance(){
        if(instance == null)
            instance = new NotificationServiceAPI();

        return instance;
    }

}
