package com.appunite.example.debugutilsexample.dao;

import com.appunite.example.debugutilsexample.model.Repos;
import com.appunite.example.debugutilsexample.service.GitHubService;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Scheduler;

@Singleton
public class GitHubDao {

    @Nonnull
    private final Observable<List<Repos>> reposObservable;

    @Inject
    public GitHubDao(final GitHubService service, Scheduler uiScheduler, Scheduler networkScheduler) {

        reposObservable = service.getRepos()
                .subscribeOn(networkScheduler)
                .observeOn(uiScheduler);
    }


    @Nonnull
    public Observable<List<Repos>> getReposObservable() {
        return reposObservable;
    }

}
