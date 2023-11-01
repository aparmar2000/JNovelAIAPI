package aparmar.nai.data.response;

import java.io.IOException;

import lombok.Getter;
import okhttp3.Response;

public class TooManyRequestsException extends IOException {
	private static final long serialVersionUID = 7164213195434836212L;
	@Getter
	private final long retryAfter;

    public TooManyRequestsException(Response response) {
        super("Recieved 429: Too Many Requests");
        
        String retryAfterHeader = response.header("retry-after");
        if (retryAfterHeader != null) {
        	retryAfter = Long.parseLong(retryAfterHeader);
        } else {
        	retryAfter = -1;
        }
    }
}
