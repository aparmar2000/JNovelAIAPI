package aparmar.nai.data.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import aparmar.nai.data.request.TextGenModel;
import aparmar.nai.data.request.textgen.TextGenerationParameters;
import aparmar.nai.data.request.textgen.TextGenerationRequest;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TextGenPresetDataFile extends DataFile<TextGenPresetDataFile> implements JsonSerializableDataFile<TextGenPresetDataFile> {
	protected int version = 3;
	protected String name;
	protected UUID id;
	@Nullable
	protected UUID remoteId;
	
	protected TextGenModel model;
	@Getter(value = AccessLevel.PROTECTED)
	protected TextGenerationParameters parameters;

	public TextGenPresetDataFile(Path filePath) {
		super(filePath);
	}
	
	public TextGenPresetDataFile(Path filePath, @NonNull String name, @Nullable UUID id, @Nullable UUID remoteId, @NonNull TextGenModel model, @NonNull TextGenerationParameters generationParameters) {
		this(filePath);
		
		this.name = name;
		this.id = id != null ? id : UUID.randomUUID();
		this.remoteId = remoteId;
		this.model = model;
		this.parameters = generationParameters;
	}
	
	protected TextGenPresetDataFile(Path filePath, int version, @NonNull String name, @Nullable UUID id, @Nullable UUID remoteId, @NonNull TextGenModel model, @NonNull TextGenerationParameters generationParameters) {
		this(filePath, name, id, remoteId, model, generationParameters);
		
		this.version = version;
	}

	@Override
	public String getFileExt() {
		return "preset";
	}
	
	public boolean isForModel(TextGenModel model) {
		return this.model == model;
	}
	
	public TextGenerationParameters getParameterInst() {
		return parameters.toBuilder()
				.build();
	}
	
	public TextGenerationRequest buildRequest(String input) {
		return TextGenerationRequest.builder()
				.model(model)
				.parameters(getParameterInst())
				.input(input)
				.build();
	}
	

	@Override
	public JsonObject saveToJson(JsonObject rootElement) throws IOException {
		
		rootElement.addProperty("presetVersion", version);
		rootElement.addProperty("name", name);
		rootElement.addProperty("id", id.toString());
		rootElement.addProperty("remoteId", remoteId != null ? remoteId.toString() : "");
		
		rootElement.add("model", gson.toJsonTree(model));
		rootElement.add("parameters", gson.toJsonTree(parameters));
		
		return rootElement;
	}

	@Override
	public TextGenPresetDataFile loadFromJson(JsonObject rootElement) throws IOException {
		
		int nVersion = rootElement.get("presetVersion").getAsInt();
		if (nVersion != 3) {
			throw new UnsupportedOperationException("preset version is "+nVersion+"; only version 3 preset files are supported");
		}
		version = nVersion;
		name = rootElement.get("name").getAsString();
		id = UUID.fromString(rootElement.get("id").getAsString());
		remoteId = null;
		if (rootElement.has("remoteId") && !rootElement.get("remoteId").getAsString().isEmpty()) {
			remoteId = UUID.fromString(rootElement.get("remoteId").getAsString());
		}
		
		model = gson.fromJson(rootElement.get("model"), TextGenModel.class);
		parameters = gson.fromJson(rootElement.get("parameters"), TextGenerationParameters.class);
		
		return this;
	}

	@Override
	public void saveToStream(OutputStream outputStream) throws IOException {
		JsonSerializableDataFile.saveToStreamViaJson(this, outputStream);
	}

	@Override
	public TextGenPresetDataFile loadFromStream(InputStream inputStream) throws IOException {
		return JsonSerializableDataFile.loadFromStreamViaJson(this, inputStream);
	}

	@Override
	protected TextGenPresetDataFile innerCloneWithNewPath(Path path) {
		return new TextGenPresetDataFile(path, version, name, id, remoteId, model, parameters.toBuilder().build());
	}

}
