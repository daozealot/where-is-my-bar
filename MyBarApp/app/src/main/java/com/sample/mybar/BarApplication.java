package com.sample.mybar;

import android.app.Application;
import android.content.Context;

import com.sample.mybar.api.ApiComponent;
import com.sample.mybar.api.ApiModule;
import com.sample.mybar.api.DaggerApiComponent;

public class BarApplication extends Application {

    private ApiComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerApiComponent.builder()
                .appModule(new AppModule(this))
                .apiModule(new ApiModule())
                .build();
    }

    // Convenience method for getting application context
    public static BarApplication get(Context context) {
        return (BarApplication) context.getApplicationContext();
    }

    public ApiComponent getAppComponent() {
        return mAppComponent;
    }
}