package aparmar.nai.data.request.textgen;

import aparmar.nai.data.request.TextGenModel;
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
	private TextGenerationParameters parameters;
}
