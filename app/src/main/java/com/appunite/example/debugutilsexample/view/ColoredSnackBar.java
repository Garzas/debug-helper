package com.appunite.example.debugutilsexample.view;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.appunite.example.debugutilsexample.R;

import javax.annotation.Nonnull;

public class ColoredSnackBar {

    private static View getSnackBarLayout(@Nonnull Snackbar snackbar) {
        return snackbar.getView();
    }

    public static Snackbar colorSnackBar(@Nonnull View view, @Nonnull CharSequence text, int length, int color) {
        Snackbar snackbar = Snackbar.make(view, text, length);
        getSnackBarLayout(snackbar).setBackgroundColor(color);
        return snackbar;
    }

    public static Snackbar colorSnackBar(@Nonnull View view, int text, int length, int color) {
        Snackbar snackbar = Snackbar.make(view, text, length);
        getSnackBarLayout(snackbar).setBackgroundColor(color);
        return snackbar;
    }

    public static Snackbar success(@Nonnull View view, int text, int length) {
        return colorSnackBar(view, text, length, view.getResources().getColor(R.color.snackBar_success));
    }

    public static Snackbar success(@Nonnull View view, @Nonnull CharSequence text, int length) {
        return colorSnackBar(view, text, length, view.getResources().getColor(R.color.snackBar_success));
    }

    public static Snackbar error(@Nonnull View view, int text, int length) {
        return colorSnackBar(view, text, length, view.getResources().getColor(R.color.snackBar_error));
    }

    public static Snackbar error(@Nonnull View view, @Nonnull CharSequence text, int length) {
        return colorSnackBar(view, text, length, view.getResources().getColor(R.color.snackBar_error));
    }
}