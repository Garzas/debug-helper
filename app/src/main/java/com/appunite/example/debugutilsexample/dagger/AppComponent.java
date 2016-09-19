package com.appunite.example.debugutilsexample.dagger;

import android.content.Context;


import com.appunite.example.debugutilsexample.App;
import com.appunite.example.debugutilsexample.dao.GitHubDao;
import com.appunite.example.debugutilsexample.service.GitHubService;
import com.appunite.rx.dagger.NetworkScheduler;
import com.appunite.rx.dagger.UiScheduler;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;
import rx.Scheduler;

@Singleton
@Component(
        modules = {
                AppModule.class,
                BaseModule.class,
        }
)
public interface AppComponent {

    void inject(App app);

    @ForApplication
    Context getContext();

    Picasso getPicasso();

    GitHubService getGitHubService();

    GitHubDao getGitHubDao();

    @UiScheduler
    Scheduler getUiScheduler();

    @NetworkScheduler
    Scheduler getNetworkScheduler();

}