package aparmar.nai.data.request.textgen;

import com.google.gson.annotations.JsonAdapter;

import aparmar.nai.data.request.TextGenModel;
import aparmar.nai.utils.serialization.TextGenerationRequestParameterSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TextGenerationRequest {
	
	private String input;
	private TextGenModel model;
	@JsonAdapter(TextGenerationRequestParameterSerializer.class)
	private TextGenerationParameters parameters;
}
