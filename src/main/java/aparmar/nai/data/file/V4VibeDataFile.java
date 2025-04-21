package aparmar.nai.data.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.V4VibeData;
import aparmar.nai.data.request.V4VibeData.VibeEncodingType;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class V4VibeDataFile<T extends V4VibeDataFile<T>> extends DataFile<T> implements JsonSerializableDataFile<T> {
	protected int version = 1;
	protected long createdAt = System.currentTimeMillis();
	protected ImportInfo importInfo = new ImportInfo();
	
	public static enum VibeFileType {
		@SerializedName("image")
		IMAGE,
		@SerializedName("encoding")
		ENCODING;
	}

	public V4VibeDataFile(Path filePath) {
		super(filePath);
	}

	public V4VibeDataFile(Path filePath, int version, long createdAt, ImportInfo importInfo) {
		super(filePath);
		this.version = version;
		this.createdAt = createdAt;
		this.importInfo = importInfo;
	}
	

	@Override
	public String getFileExt() {
		return "naiv4vibe";
	}
	
	public abstract VibeFileType getType();
	
	public abstract int getEncodingCount();

	@EqualsAndHashCode.Include
	@ToString.Include
	public abstract String getId();

	public abstract List<V4VibeData> getVibeDataForEncodingType(VibeEncodingType encodingType);
	public List<V4VibeData> getVibeDataForModel(ImageGenModel model) {
		return model.getSupportedVibeEncodingTypes()
			.stream()
			.map(this::getVibeDataForEncodingType)
			.flatMap(List::stream)
			.collect(Collectors.toList());
	}

	public Optional<V4VibeData> tryGetVibeData(ImageGenModel model, float informationExtracted, float maxDeviation) {
		return getVibeDataForModel(model)
				.stream()
				.filter(d->d.getInfoExtracted()!=null)
				.filter(d->Math.abs(d.getInfoExtracted()-informationExtracted) <= maxDeviation)
				.reduce((a,b)->{
					float aDelta = Math.abs(a.getInfoExtracted()-informationExtracted);
					float bDelta = Math.abs(b.getInfoExtracted()-informationExtracted);
					if (aDelta<=bDelta) {
						return a;
					}
					return b;
				});
	}
	public Optional<V4VibeData> tryGetVibeData(ImageGenModel model, float informationExtracted) {
		return tryGetVibeData(model, informationExtracted, 0.001f);
	}

	public abstract void clearVibeData();

	public abstract void clearVibeDataForEncodingType(VibeEncodingType encodingType);

	public abstract boolean addVibeData(V4VibeData vibeData);

	public abstract boolean removeVibeData(V4VibeData vibeData);
	
	public void setImportInfo(ImportInfo importInfo) {
		this.importInfo = importInfo;
		markChanged();
	}
	
	public abstract JsonObject saveToJson(JsonObject rootElement) throws IOException;
	@Override
	public void saveToStream(OutputStream outputStream) throws IOException {
		try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
			JsonObject root = new JsonObject();
			
			root.addProperty("version", version);
			root.addProperty("identifier", "novelai-vibe-transfer");
			saveToJson(root);
			root.add("importInfo", gson.toJsonTree(importInfo));
			
			gson.toJson(root, outputStreamWriter);
		}
	}

	public abstract T loadFromJson(JsonObject rootElement) throws IOException;
	@Override
	public T loadFromStream(InputStream inputStream) throws IOException {
		try (InputStreamReader reader = new InputStreamReader(inputStream)) {
			JsonObject root = gson.fromJson(reader, JsonObject.class);
			
			version = root.get("version").getAsInt();
			importInfo = gson.fromJson(root.get("importInfo"), ImportInfo.class);
			
			return loadFromJson(root);
		}
	}
	

	public static V4VibeDataFile<?> loadUnknownV4VibeDataFileFromJson(JsonObject rootElement, @Nullable Path filePath) throws IOException {
		if (!rootElement.has("type")) {
			throw new IOException("File has no 'type' field!");
		}
		VibeFileType vibeType = gson.fromJson(rootElement.get("type"), VibeFileType.class);
		
		switch (vibeType) {
		case IMAGE:
			return new V4VibeWithImageDataFile(filePath).loadFromJson(rootElement);
		case ENCODING:
			return new V4VibeEncodingOnlyDataFile(filePath).loadFromJson(rootElement);
		default:
			break;
		}
		throw new IOException(String.format("Failed to handle vibe file type %s!", vibeType));
	}
	public static V4VibeDataFile<?> loadUnknownV4VibeDataFileFromJson(JsonObject rootElement) throws IOException {
		return loadUnknownV4VibeDataFileFromJson(rootElement, null);
	}
	public static V4VibeDataFile<?> loadUnknownV4VibeDataFileFromStream(InputStream inputStream, @Nullable Path filePath) throws IOException {
		try (InputStreamReader reader = new InputStreamReader(inputStream)) {
			return loadUnknownV4VibeDataFileFromJson(gson.fromJson(reader, JsonObject.class), filePath);
		}
		
	}
	public static V4VibeDataFile<?> loadUnknownV4VibeDataFileFromStream(InputStream inputStream) throws IOException {
		return loadUnknownV4VibeDataFileFromStream(inputStream, null);
	}
	public static V4VibeDataFile<?> loadUnknownV4VibeDataFile(Path filePath) throws IOException {
		try (FileInputStream fileOut = new FileInputStream(filePath.toFile())) {
			return loadUnknownV4VibeDataFileFromStream(fileOut, filePath);
		}
	}
	
	
	@Value
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	@Builder(toBuilder = true)
	public static class ImportInfo {
		@Builder.Default
		protected final ImageGenModel model = ImageGenModel.ANIME_V4_FULL;
		@Builder.Default
		@SerializedName("information_extracted")
		protected final float informationExtracted = 1;
		@Builder.Default
		protected final float strength = 0.6f;
	}

}
