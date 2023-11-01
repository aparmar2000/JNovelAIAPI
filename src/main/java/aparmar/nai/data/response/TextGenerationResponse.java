package aparmar.nai.data.response;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import aparmar.nai.utils.tokenization.TokenizedChunk;

@Data
public class TextGenerationResponse {
	private TokenizedChunk output;
	private LogProbStep[] logprobs;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LogProbStep {
		private LogProb[] chosen, optionsBefore, optionsAfter;
		
		public LogProb getChosen() {
			return (chosen!=null && chosen.length>0)?chosen[0]:null;
		}
		
		public static LogProbStep singletonLogProbStep(int token) {
			LogProb singleLogProb = new LogProb(token, Math.log(1), Math.log(1));
			LogProb[] singleLogProbArray = new LogProb[] { singleLogProb };
			
			return new LogProbStep(singleLogProbArray, singleLogProbArray, singleLogProbArray);
		}
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class LogProb implements JsonDeserializer<LogProb> {
		private int token;
		private double logProbBefore, logProbAfter;
		
		public double getPercentageBefore() {
			return Math.pow(Math.E, logProbBefore);
		}
		public double getPercentageAfter() {
			return Math.pow(Math.E, logProbAfter);
		}
		
		@Override
		public LogProb deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			if(json.isJsonNull()) { return null; }
			
			if (!json.isJsonArray()) {
				throw new JsonParseException("LogProb in JSON is expected to be an array!");
			}
			JsonArray jsonRootArray = json.getAsJsonArray();
			if (jsonRootArray.size()!=2) {
				throw new JsonParseException("Root LogProb array in JSON isn't of size 2!");
			}
			
			JsonElement tokenElement = jsonRootArray.get(0);
			int newToken = tokenElement.getAsInt();

			if (!jsonRootArray.get(1).isJsonArray()) {
				throw new JsonParseException("Prob element in LogProb array in JSON isn't an array!");
			}
			JsonArray jsonProbArray = jsonRootArray.get(1).getAsJsonArray();
			if (jsonProbArray.size()!=2) {
				throw new JsonParseException("LogProb prob array in JSON isn't of size 2!");
			}
			double newBeforeLogProb = jsonProbArray.get(0).getAsDouble();
			double newAfterLogProb = jsonProbArray.get(1).getAsDouble();
			
			return new LogProb(newToken, newBeforeLogProb, newAfterLogProb);
		}
	}
}
