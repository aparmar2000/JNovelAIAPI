package main.java.aparmar.nai.data.request;

import java.util.Map;
import java.util.Map.Entry;

import okhttp3.HttpUrl;

public interface IQueryStringPayload {
	public Map<String, ? extends Object> getParameterValues();
	
	default HttpUrl.Builder appendQueryParameters(HttpUrl.Builder urlBuilder) {
		for (Entry<String, ? extends Object> entry : getParameterValues().entrySet()) {
			urlBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
		}
		
		return urlBuilder;
	}
}
