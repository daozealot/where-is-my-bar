package com.sample.mybar.location;

import com.sample.mybar.AppModule;
import com.sample.mybar.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, LocationModule.class})
public interface LocationComponent {
    void inject(MainActivity mainActivity);
}
