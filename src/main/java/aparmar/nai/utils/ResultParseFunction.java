package main.java.aparmar.nai.utils;

import java.io.IOException;

import okhttp3.ResponseBody;

@FunctionalInterface
public interface ResultParseFunction<R> {
    R apply(ResponseBody t) throws IOException;
}
