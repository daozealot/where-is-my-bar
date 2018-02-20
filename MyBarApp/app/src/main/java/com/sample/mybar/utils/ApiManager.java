package com.sample.mybar.utils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sample.mybar.BarApplication;
import com.sample.mybar.R;
import com.sample.mybar.api.GoogleDistanceApi;
import com.sample.mybar.api.GooglePlacesApi;
import com.sample.mybar.api.model.distance.Row;
import com.sample.mybar.api.model.distance.RowsWrapper;
import com.sample.mybar.api.model.places.Result;
import com.sample.mybar.api.model.places.ResultsWrapper;
import com.sample.mybar.events.BarsReceivedEvent;
import com.sample.mybar.events.DistanceReceivedEvent;
import com.sample.mybar.utils.common.BarPresentData;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiManager {

    @Inject
    Retrofit mRetrofit;

    private final Context mContext;

    public ApiManager(Context context) {
        this.mContext = context;

        // Inject dependencies
        BarApplication.get(mContext).getApiComponent().inject(this);

    }

    public void getNearbyBars(final LatLng latLng) {
        mRetrofit.create(GooglePlacesApi.class)
                .getNearbyBars(
                        latLng.latitude + "," + latLng.longitude,
                        "bar",
                        "distance",
                        mContext.getString(R.string.google_maps_key))
                .enqueue(new Callback<ResultsWrapper<Result>>() {
                    @Override
                    public void onResponse(Call<ResultsWrapper<Result>> call, Response<ResultsWrapper<Result>> response) {
                        if (response.isSuccessful()) {
                            List<BarPresentData> barData = Utils.convertResponseToBarData(response);
                            EventBus.getDefault().post(new BarsReceivedEvent(barData));
                            getBarDistances(latLng, barData);
                        } else {
                            Log.d("PlacesServiceCallback", "Code: " + response.code() + " Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultsWrapper<Result>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void getBarDistances(LatLng lastLocation, List<BarPresentData> bars) {
        for (final BarPresentData data : bars) {
            mRetrofit.create(GoogleDistanceApi.class)
                    .getDistance(lastLocation.latitude + "," + lastLocation.longitude,
                            "place_id:" + data.placeId,
                            "walking",
                            mContext.getString(R.string.google_maps_key))
                    .enqueue(new Callback<RowsWrapper<Row>>() {
                        @Override
                        public void onResponse(Call<RowsWrapper<Row>> call, Response<RowsWrapper<Row>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                data.distance = response.body().rows.get(0).elements.get(0).distance.text;
                                EventBus.getDefault().post(new DistanceReceivedEvent());
                            } else {
                                Log.d("DistanceServiceCallback", "Code: " + response.code() + " Message: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<RowsWrapper<Row>> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
        }
    }
}
