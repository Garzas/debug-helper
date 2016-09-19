package com.appunite.example.debugutilsexample.service;

import com.appunite.example.debugutilsexample.model.Repos;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import rx.Observable;

public interface GitHubService {

    @Headers("Accept: application/vnd.github.v3+json")
    @GET("/orgs/appunite/repos")
    Observable<List<Repos>> getRepos();

}
