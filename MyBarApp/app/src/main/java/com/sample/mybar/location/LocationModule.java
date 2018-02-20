package com.sample.mybar.location;

import android.app.Application;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationModule {

    @Provides
    @Singleton
    FusedLocationProviderClient provideFusedLocation(Application application) {
        return LocationServices.getFusedLocationProviderClient(application);
    }

}
