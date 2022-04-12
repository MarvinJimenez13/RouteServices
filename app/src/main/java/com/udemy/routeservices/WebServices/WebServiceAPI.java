package com.udemy.routeservices.WebServices;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WebServiceAPI {

    private static WebServiceAPI instance;
    private Retrofit retrofit;
    private HttpLoggingInterceptor httpLoggingInterceptor;
    private OkHttpClient.Builder httpClientBuilder;
    private static final String URL_BASE = "https://maps.googleapis.com";

    private WebServiceAPI(){
        httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor);
        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .client(httpClientBuilder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    public <S> S getAPI(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }

    public static WebServiceAPI getInstance(){
        if(instance == null)
            instance = new WebServiceAPI();

        return instance;
    }

}
