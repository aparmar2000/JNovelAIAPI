package aparmar.nai.data.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class UserKeystore {
	/** Not returned from the endpoint when using a persistent key, for privacy reasons. */
	private String keystore;
}
