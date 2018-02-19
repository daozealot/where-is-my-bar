package com.sample.mybar.api;

import com.sample.mybar.AppModule;
import com.sample.mybar.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface ApiComponent {
    void inject(MainActivity activity);
}
