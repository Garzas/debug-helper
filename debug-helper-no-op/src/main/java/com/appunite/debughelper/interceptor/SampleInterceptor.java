package com.appunite.debughelper.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * A no-op version of DebugHelper that can be used in release builds.
 */

public class SampleInterceptor implements Interceptor {
    @Override
    public Response intercept(final Chain chain) throws IOException {
        return chain.proceed(chain.request());
    }

}