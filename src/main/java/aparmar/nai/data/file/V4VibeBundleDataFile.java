package aparmar.nai.data.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class V4VibeBundleDataFile<D extends V4VibeDataFile<?>> extends NestedDataFile<V4VibeBundleDataFile<D>, D> implements JsonSerializableDataFile<V4VibeBundleDataFile<D>> {
	protected int version = 1;

	public V4VibeBundleDataFile(Path filePath) {
		super(filePath);
	}
	
	public V4VibeBundleDataFile(Path filePath, int version, List<D> dataFiles) {
		super(filePath, dataFiles);
		this.version = version;
	}

	@Override
	public String getFileExt() {
		return "naiv4vibebundle";
	}


	@Override
	public JsonObject saveToJson(JsonObject rootElement) throws IOException {
		if (isEmpty()) {
			throw new IOException("Cannot save a vibe encoding bundle file containing no encodings!");
		}
		
		rootElement.addProperty("identifier", "novelai-vibe-transfer-bundle");
		rootElement.addProperty("version", version);
		
		JsonArray vibeArrayElement = new JsonArray();
		for (D vibeFile : dataFiles) {
			vibeArrayElement.add(vibeFile.saveToJson(new JsonObject()));
		}
		rootElement.add("vibes", vibeArrayElement);
		
		return rootElement;
	}
	@Override
	public void saveToStream(OutputStream outputStream) throws IOException {
		try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
			gson.toJson(saveToJson(new JsonObject()), outputStreamWriter);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public V4VibeBundleDataFile<D> loadFromJson(JsonObject rootElement) throws IOException {
		version = rootElement.get("version").getAsInt();
		clear();
		for (JsonElement vibeFileElement : rootElement.getAsJsonArray("vibes")) {
			try {
				add((D) V4VibeDataFile.loadUnknownV4VibeDataFileFromJson(vibeFileElement.getAsJsonObject(), null));
			} catch (ClassCastException e) {
				throw new IOException("JSON contained V4VibeDataFile of the wrong type!", e);
			}
		}
		
		return this;
	}
	@Override
	public V4VibeBundleDataFile<D> loadFromStream(InputStream inputStream) throws IOException {
		try (InputStreamReader reader = new InputStreamReader(inputStream)) {
			return loadFromJson(gson.fromJson(reader, JsonObject.class));
		}
	}
	

	@Override
	protected V4VibeBundleDataFile<D> innerCloneWithNewPath(Path path) {
		return new V4VibeBundleDataFile<D>(path, this.version, this.dataFiles);
	}

}
