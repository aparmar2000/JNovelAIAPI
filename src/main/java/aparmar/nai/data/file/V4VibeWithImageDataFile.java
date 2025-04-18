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

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class V4VibeWithImageDataFile extends V4VibeDataFile<V4VibeWithImageDataFile> {
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@NonNull
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

	public V4VibeWithImageDataFile(Path filePath) {
		super(filePath);
	}

	protected V4VibeWithImageDataFile(Path filePath, int version, long createdAt, ImportInfo importInfo, @NonNull BufferedImage image, @Nullable Multimap<VibeEncodingType, EmbeddingData> embeddingMap) {
		super(filePath, version, createdAt, importInfo);
		this.image = new Base64Image(image);
		if (embeddingMap != null) {
			this.encodingMap.putAll(embeddingMap);
		}
	}

	@Override
	public String getId() {
		return image.generateSha256();
	}

	@Override
	public VIBE_FILE_TYPE getType() {
		return VIBE_FILE_TYPE.IMAGE;
	}
	
	public BufferedImage getImage() {
		return image.getImage();
	}
	
	public ImageVibeEncodeRequest buildEncodeRequest(ImageGenModel model, float informationExtracted) {		
		return ImageVibeEncodeRequest.builder()
				.image(image)
				.model(model)
				.informationExtracted(informationExtracted)
				.build();
	}

	@Override
	@Locked.Read("encodingMapLock")
	public int getEncodingCount() {
		return encodingMap.size();
	}

	@Override
	@Locked.Read("encodingMapLock")
	public List<V4VibeData> getVibeDataForEncodingType(VibeEncodingType encodingType) {
		return encodingMap.get(encodingType)
				.stream()
				.map(d->new V4VibeData(d.informationExtracted, image.generateSha256(), encodingType, d.encoding))
				.collect(Collectors.toList());
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
	
	public void setImage(@NonNull BufferedImage image) {
		this.image = new Base64Image(image);
		markChanged();
	}

	@Override
	@Locked.Write("encodingMapLock")
	public void clearVibeData() {
		encodingMap.clear();
		markChanged();
	}

	@Override
	@Locked.Write("encodingMapLock")
	public void clearVibeDataForEncodingType(VibeEncodingType encodingType) {
		encodingMap.removeAll(encodingType);
		markChanged();
	}

	@Override
	@Locked.Write("encodingMapLock")
	public boolean addVibeData(V4VibeData vibeData) {
		boolean added = encodingMap.put(vibeData.getEncodingType(), new EmbeddingData(vibeData));
		if (added) { markChanged(); }
		return added;
	}

	@Override
	@Locked.Write("encodingMapLock")
	public boolean removeVibeData(V4VibeData vibeData) {
		boolean removed = encodingMap.remove(vibeData.getEncodingType(), new EmbeddingData(vibeData));
		if (removed) { markChanged(); }
		return removed;
	}
	

	@Override
	public void saveToStream(OutputStream outputStream) throws IOException {
		if (getEncodingCount() == 0) {
			throw new IOException("Cannot save a vibe encoding file containing no encodings!");
		}
		JsonObject root = new JsonObject();
		
		root.addProperty("identifier", "novelai-vibe-transfer");
		root.addProperty("version", version);
		root.add("image", gson.toJsonTree(image));
		root.add("type", gson.toJsonTree(getType()));
		root.addProperty("id", getId());
		
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
		if (image != null) {
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
		}
		root.addProperty("createdAt", createdAt);
		root.add("importInfo", gson.toJsonTree(importInfo));
		
		try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
			gson.toJson(root, outputStreamWriter);
		}
	}

	@Override
	public V4VibeWithImageDataFile loadFromStream(InputStream inputStream) throws IOException {
		JsonObject root;
		try (InputStreamReader reader = new InputStreamReader(inputStream)) {
			root = gson.fromJson(reader, JsonObject.class);
		}
		version = root.get("version").getAsInt();
		boolean hasImage = root.get("type").getAsString()=="image";
		if (hasImage) {
			image = gson.fromJson(root.get("image"), Base64Image.class);
		} else {
			image = null;
		}
		
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
	protected V4VibeWithImageDataFile innerCloneWithNewPath(Path path) {
		return new V4VibeWithImageDataFile(
				path, 
				version, 
				createdAt, 
				importInfo,
				image.getImage(), 
				encodingMap);
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
