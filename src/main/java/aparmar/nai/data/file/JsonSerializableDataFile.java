package aparmar.nai.data.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.google.gson.JsonObject;

public interface JsonSerializableDataFile<T extends DataFile<T>> {

	public JsonObject saveToJson(JsonObject rootElement) throws IOException;
	
	public T loadFromJson(JsonObject rootElement) throws IOException;
	
	public static <F extends DataFile<F>> void saveToStreamViaJson(JsonSerializableDataFile<F> dataFile, OutputStream outputStream) throws IOException {
		try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {			
			DataFile.getGson().toJson(dataFile.saveToJson(new JsonObject()), outputStreamWriter);
		}
	}
	
	public static <F extends DataFile<F>> F loadFromStreamViaJson(JsonSerializableDataFile<F> dataFile, InputStream inputStream) throws IOException {
		try (InputStreamReader reader = new InputStreamReader(inputStream)) {			
			return dataFile.loadFromJson(DataFile.getGson().fromJson(reader, JsonObject.class));
		}
	}
}
