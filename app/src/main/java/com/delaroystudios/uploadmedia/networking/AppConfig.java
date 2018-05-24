package com.delaroystudios.uploadmedia.networking;

/**
 * Created by delaroystudios on 10/5/2016.
 */
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AppConfig {

    public static String BASE_URL = "http://unitypuzzlegame.com/";

    public static Retrofit getRetrofit() {

        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
