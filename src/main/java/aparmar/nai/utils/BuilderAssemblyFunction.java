package aparmar.nai.utils;

import java.util.Objects;

import okhttp3.HttpUrl;

public interface BuilderAssemblyFunction {
	HttpUrl.Builder apply(HttpUrl.Builder t);
	
    default BuilderAssemblyFunction andThen(BuilderAssemblyFunction after) {
        Objects.requireNonNull(after);
        return (HttpUrl.Builder t) -> after.apply(apply(t));
    }
}
