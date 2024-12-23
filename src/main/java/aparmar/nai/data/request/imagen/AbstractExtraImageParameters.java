package aparmar.nai.data.request.imagen;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public abstract class AbstractExtraImageParameters {
	public boolean compatibleWith(AbstractExtraImageParameters otherParameters) { return true; }
}
