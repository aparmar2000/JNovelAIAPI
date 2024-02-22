package aparmar.nai.data.request.imagen;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public abstract class AbstractExtraImageParameters {
	public boolean compatibleWith(AbstractExtraImageParameters otherParameters) { return true; }
}
