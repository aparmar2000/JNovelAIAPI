package aparmar.nai.data.file;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import aparmar.nai.NAIAPI;
import aparmar.nai.data.request.Base64Image;
import aparmar.nai.data.request.ImageVibeEncodeRequest;
import aparmar.nai.data.request.V4VibeData;
import aparmar.nai.data.request.V4VibeData.VibeEncodingType;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import aparmar.nai.utils.ByteArrayEncodings;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Locked;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class V4VibeDataFile extends DataFile<V4VibeDataFile> {
	protected int version = 1;
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	protected Base64Image image = new Base64Image();
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Getter(value = AccessLevel.NONE)
	protected final ReentrantReadWriteLock encodingMapLock = new ReentrantReadWriteLock();
	@Getter(value = AccessLevel.NONE)
	protected final Multimap<VibeEncodingType, EmbeddingData> encodingMap = MultimapBuilder
			.enumKeys(VibeEncodingType.class)
			.hashSetValues()
			.build();
	protected long createdAt = System.currentTimeMillis();
	protected ImportInfo importInfo = new ImportInfo();

	public V4VibeDataFile(Path filePath) {
		super(filePath);
	}

	protected V4VibeDataFile(Path filePath, int version, BufferedImage image, Multimap<VibeEncodingType, EmbeddingData> embeddingMap, long createdAt, ImportInfo importInfo) {
		super(filePath);
		this.version = version;
		this.image = new Base64Image(image);
		if (embeddingMap != null) {
			this.encodingMap.putAll(embeddingMap);
		}
		this.createdAt = createdAt;
		this.importInfo = importInfo;
	}
	

	@Override
	public String getFileExt() {
		return "naiv4vibe";
	}
	
	public BufferedImage getImage() {
		return image.getImage();
	}

	@EqualsAndHashCode.Include
	@ToString.Include
	public String getImageId() {
		return image.generateSha256();
	}
	
	public ImageVibeEncodeRequest buildEncodeRequest(ImageGenModel model, float informationExtracted) {		
		return ImageVibeEncodeRequest.builder()
				.image(image)
				.model(model)
				.informationExtracted(informationExtracted)
				.build();
	}

	@Locked.Read("encodingMapLock")
	public List<V4VibeData> getVibeDataForEncodingType(VibeEncodingType encodingType) {
		return encodingMap.get(encodingType)
				.stream()
				.map(d->new V4VibeData(d.informationExtracted, getImageId(), encodingType, d.encoding))
				.collect(Collectors.toList());
	}
	@Locked.Read("encodingMapLock")
	public List<V4VibeData> getVibeDataForModel(ImageGenModel model) {
		return model.getSupportedVibeEncodingTypes()
			.stream()
			.map(this::getVibeDataForEncodingType)
			.flatMap(List::stream)
			.collect(Collectors.toList());
	}

	@Locked.Read("encodingMapLock")
	public Optional<V4VibeData> tryGetVibeData(ImageGenModel model, float informationExtracted, float maxDeviation) {
		return getVibeDataForModel(model)
				.stream()
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

	@Locked.Write("encodingMapLock")
	public V4VibeData getOrRequestVibeData(NAIAPI nai, ImageGenModel model, float informationExtracted, float maxDeviation) throws IOException {
		Optional<V4VibeData> result = tryGetVibeData(model, informationExtracted, maxDeviation);
		if (result.isPresent()) {
			return result.get();
		}
		result = Optional.of(nai.encodeImageVibe(buildEncodeRequest(model, informationExtracted)));
		addVibeData(result.get());
		return tryGetVibeData(model, informationExtracted, maxDeviation).get();
	}
	public V4VibeData getOrRequestVibeData(NAIAPI nai, ImageGenModel model, float informationExtracted) throws IOException {
		return getOrRequestVibeData(nai, model, informationExtracted, 0.001f);
	}
	
	public void setImage(BufferedImage image) {
		this.image = new Base64Image(image);
		markChanged();
	}

	@Locked.Write("encodingMapLock")
	public void clearVibeData() {
		encodingMap.clear();
		markChanged();
	}

	@Locked.Write("encodingMapLock")
	public void clearVibeDataForEncodingType(VibeEncodingType encodingType) {
		encodingMap.removeAll(encodingType);
		markChanged();
	}

	@Locked.Write("encodingMapLock")
	public boolean addVibeData(V4VibeData vibeData) {
		boolean added = encodingMap.put(vibeData.getEncodingType(), new EmbeddingData(vibeData));
		if (added) { markChanged(); }
		return added;
	}

	@Locked.Write("encodingMapLock")
	public boolean removeVibeData(V4VibeData vibeData) {
		boolean removed = encodingMap.remove(vibeData.getEncodingType(), new EmbeddingData(vibeData));
		if (removed) { markChanged(); }
		return removed;
	}
	
	public void setImportInfo(ImportInfo importInfo) {
		this.importInfo = importInfo;
		markChanged();
	}
	

	@Override
	public void saveToStream(OutputStream outputStream) throws IOException {
		JsonObject root = new JsonObject();
		
		root.addProperty("identifier", "novelai-vibe-transfer");
		root.addProperty("version", version);
		root.addProperty("type", "image");
		root.add("image", gson.toJsonTree(image));
		root.addProperty("id", image.generateSha256());
		
		JsonObject encodingsRoot = new JsonObject();
		for (VibeEncodingType encodingType : encodingMap.keySet()) {
			JsonObject encodingTypeRoot = new JsonObject();
			
			for (EmbeddingData encodingData : encodingMap.get(encodingType)) {
				JsonObject encodingRoot = new JsonObject();
				
				encodingRoot.addProperty("encoding", ByteArrayEncodings.encodeByteArrayToB64(encodingData.getEncoding()));
				JsonObject paramsRoot = new JsonObject();
				paramsRoot.addProperty("information_extracted", encodingData.getInformationExtracted());
				encodingRoot.add("params", paramsRoot);
				
				encodingTypeRoot.add(encodingData.getParamSha256(), encodingRoot);
			}
			
			encodingsRoot.add(gson.toJson(encodingType), encodingTypeRoot);
		}
		root.add("encodings", encodingsRoot);
		
		root.addProperty("name", getFilePath().getFileName().toString());
		int thumbHeight = image.getTargetHeight();
		int thumbWidth = image.getTargetWidth();
		if (thumbHeight>thumbWidth) {
			thumbWidth = (int) Math.max(256, Math.round(thumbWidth * (256D/thumbHeight)));
			thumbHeight = 256;
		} else {
			thumbHeight = (int) Math.max(256, Math.round(thumbHeight * (256D/thumbWidth)));
			thumbWidth = 256;
		}
		root.add("thumbnail", gson.toJsonTree(new Base64Image(image.getImage(), thumbHeight, thumbWidth, false)));
		root.addProperty("createdAt", createdAt);
		root.add("importInfo", gson.toJsonTree(importInfo));
		
		try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
			gson.toJson(root, outputStreamWriter);
		}
	}

	@Override
	public V4VibeDataFile loadFromStream(InputStream inputStream) throws IOException {
		JsonObject root;
		try (InputStreamReader reader = new InputStreamReader(inputStream)) {
			root = gson.fromJson(reader, JsonObject.class);
		}
		version = root.get("version").getAsInt();
		image = gson.fromJson(root.get("image"), Base64Image.class);
		
		JsonObject encodingsRoot = root.getAsJsonObject("encodings");
		encodingMap.clear();
		for (Entry<String, JsonElement> modelEntry : encodingsRoot.entrySet()) {
			VibeEncodingType encodingType = gson.fromJson(modelEntry.getKey(), VibeEncodingType.class);
			
			for (Entry<String, JsonElement> encodingEntry : modelEntry.getValue().getAsJsonObject().entrySet()) {
				JsonObject encodingEntryValue = encodingEntry.getValue().getAsJsonObject();
				JsonObject encodingEntryParams = encodingEntryValue.getAsJsonObject("params");
				encodingMap.put(encodingType, new EmbeddingData(
						ByteArrayEncodings.decodeB64ToByteArray(encodingEntryValue.get("encoding").getAsString()),
						encodingEntryParams.get("information_extracted").getAsFloat()
						));
			}
		}
		
		createdAt = root.get("createdAt").getAsLong();
		importInfo = gson.fromJson(root.get("importInfo"), ImportInfo.class);
		
		return this;
	}

	@Override
	protected V4VibeDataFile innerCloneWithNewPath(Path path) {
		return new V4VibeDataFile(
				path, 
				version, 
				image.getImage(), 
				encodingMap,
				createdAt, 
				importInfo);
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
	
	@Value
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	@Builder(toBuilder = true)
	public static class EmbeddingData {
		
		public EmbeddingData(V4VibeData vibeData) {
			this.encoding = vibeData.getEncoding();
			this.informationExtracted = vibeData.getInfoExtracted();
		}
		
		@Builder.Default
		protected final byte[] encoding = new byte[0];
		@Builder.Default
		protected final float informationExtracted = 1;
		
		public String getParamSha256() {
			return ByteArrayEncodings.encodeStringBytesToSha256Hex(String.format("information_extracted:%s", informationExtracted));
		}
	}

}
