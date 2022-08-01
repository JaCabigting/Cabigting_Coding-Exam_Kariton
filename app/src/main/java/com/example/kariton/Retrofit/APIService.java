package com.example.kariton.Retrofit;

import com.example.kariton.Models.Shop;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface APIService {

  // GET Route to retrieve the list of products to be shown in the app
  @GET("api/v1/products")
  Call<Shop> getProducts(@Header("authorization") String header); // Uses the Authorization header as a parameter for this function

}
