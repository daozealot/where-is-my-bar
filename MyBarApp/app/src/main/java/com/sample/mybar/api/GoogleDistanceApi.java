package com.sample.mybar.api;

import com.sample.mybar.api.model.distance.Row;
import com.sample.mybar.api.model.distance.RowsWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleDistanceApi {

    @GET("distancematrix/json")
    Call<RowsWrapper<Row>> getDistance(
            @Query("origins") String location,
            @Query("destinations") String placeId,
            @Query("mode") String mode,
            @Query("key") String key);

}
