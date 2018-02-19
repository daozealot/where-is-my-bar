package com.sample.mybar.utils;

import com.sample.mybar.api.model.BarPresentData;
import com.sample.mybar.api.model.places.Result;
import com.sample.mybar.api.model.places.ResultsWrapper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class Utils {

    public static List<BarPresentData> convertResponseToBarData(Response<ResultsWrapper<Result>> response) {
        if (response.body() == null) {
            return new ArrayList<>();
        }
        List<BarPresentData> barData = new ArrayList<>(response.body().results.size());
        for (Result result: response.body().results) {
            barData.add(new BarPresentData(result.name, result.geometry.location, result.placeId));
        }

        return barData;
    }
}
