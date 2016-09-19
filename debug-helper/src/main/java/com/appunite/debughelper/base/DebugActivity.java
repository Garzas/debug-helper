package com.appunite.debughelper.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.appunite.debughelper.DebugHelper;
import com.appunite.debughelper.dialog.OptionsDialog;
import com.appunite.debughelper.macro.FieldManager;
import com.appunite.debughelper.macro.MacroFragment;
import com.appunite.debughelper.macro.MacroService;
import com.appunite.debughelper.model.SelectOption;

import javax.annotation.Nonnull;

public abstract class DebugActivity extends AppCompatActivity implements OptionsDialog.OnSelectOptionListener,
        MacroFragment.MacroFragmentListener, MacroService.DeployMacroListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugHelper.setActivity(this);
    }

    @Override
    public void setContentView(final int layoutResID) {
        super.setContentView(DebugHelper.setContentView(layoutResID));
    }

    @Override
    public void setContentView(@Nonnull final View view) {
        super.setContentView(DebugHelper.setContentView(view));
    }

    @Override
    public void setContentView(@Nonnull final View view, @Nonnull final ViewGroup.LayoutParams params) {
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

    @Override
    public void onFinishDialog() {
        DebugHelper.hide();
    }

    @Override
    public void deployMacro() {
        FieldManager.fillFields(this);
    }
}
