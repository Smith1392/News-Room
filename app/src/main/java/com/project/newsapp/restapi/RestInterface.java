package com.project.newsapp.restapi;

import com.project.newsapp.model.TotalNews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestInterface {

    @GET("v2/top-headlines?country=in&category=business&apiKey=24802f736c1d41889bb99f0e5b9c8ea2")
    Call<TotalNews> getTotalNews(@Query("country") String country, @Query("apiKey") String apiKey);

    @GET("v2/top-headlines?country=in&category=business&apiKey=24802f736c1d41889bb99f0e5b9c8ea2")
    Call<TotalNews> getTotalNews(@Query("country") String country, @Query("category") String category, @Query("apiKey") String apiKey);

    @GET("v2/top-headlines?country=in&category=business&apiKey=24802f736c1d41889bb99f0e5b9c8ea2")
    Call<TotalNews> getSearchedTotalNews(@Query("q") String country, @Query("apiKey") String apiKey);
}
