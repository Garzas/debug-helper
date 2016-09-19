package com.appunite.example.debugutilsexample.dagger;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;

import com.appunite.example.debugutilsexample.service.GitHubService;
import com.appunite.rx.dagger.NetworkScheduler;
import com.appunite.rx.dagger.UiScheduler;

import dagger.Component;
import rx.Scheduler;

@ActivityScope
@Component(
        dependencies = AppComponent.class,
        modules = {
                ActivityModule.class,
        }
)
public interface BaseActivityComponent {

    @ForActivity
    Resources getResources();

    @ForActivity
    Context getActivityContext();

    LayoutInflater getLayoutInflater();

    GitHubService getGithubService();

    @NetworkScheduler
    Scheduler getNetworkScheduler();

    @UiScheduler
    Scheduler getUiScheduler();

}