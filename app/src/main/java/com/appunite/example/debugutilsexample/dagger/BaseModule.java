package com.appunite.example.debugutilsexample.dagger;

import com.appunite.rx.dagger.NetworkScheduler;
import com.appunite.rx.dagger.UiScheduler;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public final class BaseModule {


    @Provides
    @UiScheduler
    Scheduler provideUiScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @NetworkScheduler
    Scheduler provideNetworkScheduler() {
        return Schedulers.io();
    }
}