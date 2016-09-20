package com.appunite.example.debugutilsexample.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.appunite.example.debugutilsexample.App;
import com.appunite.example.debugutilsexample.BaseActivity;
import com.appunite.example.debugutilsexample.R;
import com.appunite.example.debugutilsexample.dagger.ActivityModule;
import com.appunite.example.debugutilsexample.dagger.BaseActivityComponent;
import com.appunite.example.debugutilsexample.presenter.DetailsPresenter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.ButterKnife;

public class DetailsActivity extends BaseActivity {


    @Inject
    DetailsPresenter presenter;

    public static Intent newIntent(final Context context) {
        return new Intent(context, DetailsActivity.class);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        final DetailsActivityComponent component = (DetailsActivityComponent) getActivityComponent();
        component.inject(this);
        ButterKnife.inject(this);


    }

    @Nonnull
    @Override
    public BaseActivityComponent createActivityComponent(@Nullable Bundle savedInstanceState) {
        return DaggerDetailsActivityComponent
                .builder()
                .activityModule(new ActivityModule(this))
                .appComponent(App.getAppComponent(getApplication()))
                .build();
    }
}
