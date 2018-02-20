package com.sample.mybar;

import android.app.Application;
import android.content.Context;

import com.sample.mybar.api.ApiComponent;
import com.sample.mybar.api.ApiModule;
import com.sample.mybar.api.DaggerApiComponent;
import com.sample.mybar.location.DaggerLocationComponent;
import com.sample.mybar.location.LocationComponent;
import com.sample.mybar.location.LocationModule;

public class BarApplication extends Application {

    private ApiComponent mApiComponent;
    private LocationComponent mLocationComponent;

    // Convenience method for getting application context
    public static BarApplication get(Context context) {
        return (BarApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApiComponent = DaggerApiComponent.builder()
                .apiModule(new ApiModule())
                .build();

        mLocationComponent = DaggerLocationComponent.builder()
                .appModule(new AppModule(this))
                .locationModule(new LocationModule())
                .build();
    }

    public ApiComponent getApiComponent() {
        return mApiComponent;
    }

    public LocationComponent getLocationComponent() {
        return mLocationComponent;
    }

}