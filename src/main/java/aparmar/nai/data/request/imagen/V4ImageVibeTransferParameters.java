package aparmar.nai.data.request.imagen;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import aparmar.nai.data.request.V4VibeData;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.val;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class V4ImageVibeTransferParameters extends AbstractExtraImageParameters implements JsonSerializer<V4ImageVibeTransferParameters> {
	protected List<VibeTransferData> vibeDatas;
	
	@Nullable
	public ImageGenModel getEncodingModel() {
		if (vibeDatas==null || vibeDatas.isEmpty()) {
			return null;
		}
		return vibeDatas.get(0).getVibeData().getModel();
	}
	
	protected byte[][] getVibeDataEncodings() {
		return vibeDatas.stream().map(VibeTransferData::getVibeData).map(V4VibeData::getEncoding).toArray(byte[][]::new);
	}
	protected double[] getVibeDataReferenceStrengthPerVibe() {
		return vibeDatas.stream().mapToDouble(VibeTransferData::getReferenceStrength).toArray();
	}
	
	@Data
	@Builder
	@AllArgsConstructor
	public static class VibeTransferData {
		@Builder.Default
		protected double referenceStrength = 0.6;
		protected V4VibeData vibeData;
	}
	
	public static class V4ImageVibeTransferParametersBuilder {
		public V4ImageVibeTransferParametersBuilder vibeData(VibeTransferData vibeData) {
			if (this.vibeDatas == null) {
				this.vibeDatas = new ArrayList<>();
			} else {
				val testModel = vibeData.getVibeData().getModel();
				val mismatchedData = vibeDatas.stream().filter(d->d.getVibeData().getModel()!=testModel).findAny();
				if (mismatchedData.isPresent()) {
					throw new IllegalArgumentException(String.format("All VibeTransferData must be encoded for the same model - found mismatching models %s and %s", testModel, mismatchedData.get().getVibeData().getModel()));
				}
			}
			
			this.vibeDatas.add(vibeData);
			return this;
		}
		
		public V4ImageVibeTransferParametersBuilder vibeDatas(List<VibeTransferData> vibeDatas) {
			if (!vibeDatas.isEmpty()) {
				val testModel = vibeDatas.get(0).getVibeData().getModel();
				val mismatchedData = vibeDatas.stream().filter(d->d.getVibeData().getModel()!=testModel).findAny();
				if (mismatchedData.isPresent()) {
					throw new IllegalArgumentException(String.format("All VibeTransferData must be encoded for the same model - found mismatching models %s and %s", testModel, mismatchedData.get().getVibeData().getModel()));
				}
			}
			
			this.vibeDatas = vibeDatas;
			return this;
		}
	}
	
	//===== Serialization

	@Override
	public JsonElement serialize(V4ImageVibeTransferParameters src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject wrapper = new JsonObject();
		val encodedVibes = Arrays.stream(src.getVibeDataEncodings()).map(b->Base64.getMimeEncoder().encodeToString(b)).toArray(String[]::new);
		wrapper.add("reference_image_multiple", context.serialize(encodedVibes, String[].class));
		wrapper.add("reference_strength_multiple", context.serialize(src.getVibeDataReferenceStrengthPerVibe(), double[].class));
		
		return wrapper;
	}
}
