package com.sample.mybar.api;

import com.sample.mybar.utils.ApiManager;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApiModule.class})
public interface ApiComponent {
    void inject(ApiManager apiManager);
}
