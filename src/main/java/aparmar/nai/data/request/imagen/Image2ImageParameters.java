package aparmar.nai.data.request.imagen;

import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.Base64Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class Image2ImageParameters extends AbstractExtraImageParameters {
	@Builder.Default
	@SerializedName("extra_noise_seed")
	protected long extraNoiseSeed = ThreadLocalRandom.current().nextLong();
	@Builder.Default
	protected double strength = 0.7;
	@Builder.Default
	protected double noise = 0.0;
	protected Base64Image image;
}
