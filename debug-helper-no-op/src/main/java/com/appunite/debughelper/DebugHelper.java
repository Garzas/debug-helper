package com.appunite.debughelper;

import com.appunite.debughelper.interceptor.SampleInterceptor;

import okhttp3.Interceptor;

public class DebugHelper {

    public static Interceptor getResponseInterceptor() {
        return new SampleInterceptor();
    }

}