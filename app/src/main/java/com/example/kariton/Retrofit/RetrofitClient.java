package com.example.kariton.Retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static Retrofit instance;

    // Getter for Retrofit instance
    public static Retrofit getInstance() {

        if(instance == null)
            instance = new Retrofit.Builder()
                    .baseUrl("https://sw-coding-challenge.herokuapp.com/") // Url where the request for the list of products will be sent
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        return instance;
    }
}
