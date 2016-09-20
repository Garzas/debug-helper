package com.appunite.debughelper;

import com.appunite.debughelper.interceptor.SampleInterceptor;

import javax.annotation.Nonnull;

import okhttp3.Interceptor;

public class DebugHelper {

    @Nonnull
    public static Interceptor getResponseInterceptor() {
        return new SampleInterceptor();
    }

}