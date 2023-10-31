package main.java.aparmar.nai.utils;

import java.io.IOException;

import okhttp3.ResponseBody;

public class ZipParseFunction implements ResultParseFunction<ZipArchiveWrapper> {

	@Override
	public ZipArchiveWrapper apply(ResponseBody t) throws IOException {
	    return new ZipArchiveWrapper(t.bytes());
	}

}
