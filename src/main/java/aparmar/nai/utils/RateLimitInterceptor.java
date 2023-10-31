package main.java.aparmar.nai.utils;

import java.io.IOException;

import com.google.common.util.concurrent.RateLimiter;

import okhttp3.Interceptor;
import okhttp3.Response;

public class RateLimitInterceptor implements Interceptor {
    private final RateLimiter rateLimiter;
    
    public RateLimitInterceptor(double requestsPerSecond) {
    	rateLimiter = RateLimiter.create(requestsPerSecond);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        rateLimiter.acquire(1);
        return chain.proceed(chain.request());
    }
}