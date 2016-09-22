package com.appunite.debughelper;

import android.content.Context;

import com.appunite.debughelper.interceptor.SampleInterceptor;

import okhttp3.Interceptor;

/**
 * A no-op version of DebugHelper that can be used in release builds.
 */

public class DebugHelper {

    public static Interceptor getResponseInterceptor() {
        return new SampleInterceptor();
    }

    public static void install(Context context) {
    }
}