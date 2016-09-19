package com.appunite.debughelper.base;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.appunite.debughelper.DebugHelper;
import com.appunite.debughelper.dialog.OptionsDialog;
import com.appunite.debughelper.model.SelectOption;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import javax.annotation.Nonnull;

public abstract class RxDebugActivity extends RxAppCompatActivity implements OptionsDialog.OnSelectOptionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugHelper.setActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(DebugHelper.setContentView(layoutResID));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(DebugHelper.setContentView(view));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(DebugHelper.setContentView(view), params);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DebugHelper.unSubscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugHelper.reSubscribe(this);
    }

    @Override
    public void onSelectOption(@Nonnull final SelectOption option) {
        DebugHelper.updateOption(option);
    }
}
