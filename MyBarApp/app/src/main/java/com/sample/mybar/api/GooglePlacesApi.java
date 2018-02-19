package com.sample.mybar.api;

import com.sample.mybar.api.model.places.Result;
import com.sample.mybar.api.model.places.ResultsWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesApi {

    @GET("place/nearbysearch/json")
    Call<ResultsWrapper<Result>> getNearbyBars(
            @Query("location") String location,
            @Query("type") String type,
            @Query("rankby") String rankBy,
            @Query("key") String key);

}
