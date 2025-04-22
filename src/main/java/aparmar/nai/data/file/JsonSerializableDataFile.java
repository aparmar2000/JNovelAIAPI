package aparmar.nai.data.file;

import java.io.IOException;

import com.google.gson.JsonObject;

public interface JsonSerializableDataFile<T extends DataFile<T>> {

	public JsonObject saveToJson(JsonObject rootElement) throws IOException;
	
	public T loadFromJson(JsonObject rootElement) throws IOException;
}
