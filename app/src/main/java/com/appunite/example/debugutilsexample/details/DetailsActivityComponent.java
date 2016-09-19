package com.appunite.example.debugutilsexample.details;

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
public interface DetailsActivityComponent extends BaseActivityComponent {

    void inject(DetailsActivity activity);

}