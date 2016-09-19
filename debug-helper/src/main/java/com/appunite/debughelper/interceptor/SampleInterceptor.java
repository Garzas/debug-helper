package com.appunite.debughelper.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class SampleInterceptor implements Interceptor {
    @Override
    public Response intercept(final Chain chain) throws IOException {
        return DebugInterceptor.fakeResponse(chain);
    }

}