package com.appunite.example.debugutilsexample.dagger;

import android.app.Application;
import android.content.Context;

import com.appunite.debughelper.DebugHelper;
import com.appunite.example.debugutilsexample.App;
import com.appunite.example.debugutilsexample.BuildConfig;
import com.appunite.example.debugutilsexample.dao.GitHubDao;
import com.appunite.example.debugutilsexample.service.GitHubService;
import com.appunite.rx.dagger.NetworkScheduler;
import com.appunite.rx.dagger.UiScheduler;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;


@Module
public final class AppModule {

    private static final String TAG = AppModule.class.getCanonicalName();

    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    @ForApplication
    public Context activityContext() {
        return app.getApplicationContext();
    }


    @Provides
    @Singleton
    Picasso providePicasso(@ForApplication Context context, OkHttpClient okHttpClient) {
        return new Picasso.Builder(context)
                .indicatorsEnabled(BuildConfig.DEBUG)
                .loggingEnabled(BuildConfig.DEBUG)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(DebugHelper.getResponseInterceptor())
                .build();
    }


    @Provides
    @Singleton
    GitHubService provideRestAdapterBuilder(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(GitHubService.class);
    }

    @Provides
    @Singleton
    GitHubDao provideGithubDao(GitHubService service,
                               @UiScheduler Scheduler uiScheduler,
                               @NetworkScheduler Scheduler networkScheduler) {
        return new GitHubDao(service, uiScheduler, networkScheduler);
    }

    @Provides
    @Singleton
    File provideCacheDirectory(@ForApplication Context context) {
        return context.getCacheDir();
    }

    @Provides
    @Singleton
    Locale provideLocale() {
        return Locale.getDefault();
    }


}
