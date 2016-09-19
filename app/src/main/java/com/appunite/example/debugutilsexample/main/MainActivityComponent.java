package com.appunite.example.debugutilsexample.main;

import com.appunite.example.debugutilsexample.dagger.ActivityModule;
import com.appunite.example.debugutilsexample.dagger.ActivityScope;
import com.appunite.example.debugutilsexample.dagger.AppComponent;
import com.appunite.example.debugutilsexample.dagger.BaseActivityComponent;

import dagger.Component;

@ActivityScope
@Component(
        dependencies = AppComponent.class,
        modules = ActivityModule.class
)
public interface MainActivityComponent extends BaseActivityComponent {

    void inject(MainActivity activity);

}