package com.appunite.debughelper.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.appunite.debughelper.model.SelectOption;
import com.squareup.haha.guava.collect.FluentIterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class OptionsDialog extends DialogFragment {

    private static int currentItem;
    @Nonnull
    private static SelectOption selectOption;

    public OptionsDialog() {
    }

    public interface OnSelectOptionListener {

        void onSelectOption(@Nonnull final SelectOption option);
    }

    public static OptionsDialog newInstance(SelectOption option) {
        currentItem = option.getCurrentPosition();
        selectOption = option;
        return new OptionsDialog();
    }

    @Nonnull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        final ArrayList<String> stringArrayList = new ArrayList<>();
        for (final Integer value : selectOption.getValues()) {
            stringArrayList.add(value.toString());
        }

        final String[] strings = (String[]) stringArrayList.toArray();

        return new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Dialog)
                .setSingleChoiceItems(strings, selectOption.getCurrentPosition(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentItem = which;
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                        selectOption.setCurrentPosition(currentItem);
                        OnSelectOptionListener listener = (OnSelectOptionListener) getActivity();
                        listener.onSelectOption(selectOption);
                    }
                })
                .create();
    }

}