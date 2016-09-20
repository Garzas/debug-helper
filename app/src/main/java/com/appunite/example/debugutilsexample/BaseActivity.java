package com.appunite.example.debugutilsexample;

import android.os.Bundle;

import com.appunite.debughelper.base.DebugActivity;
import com.appunite.example.debugutilsexample.dagger.BaseActivityComponent;
import com.appunite.example.debugutilsexample.dagger.BaseActivityComponentProvider;

import javax.annotation.Nonnull;


public abstract class BaseActivity extends DebugActivity implements BaseActivityComponentProvider {

    private BaseActivityComponent activityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityComponent = createActivityComponent(savedInstanceState);
        super.onCreate(savedInstanceState);

    }

    @Nonnull
    public BaseActivityComponent getActivityComponent() {
        return activityComponent;
    }
}
