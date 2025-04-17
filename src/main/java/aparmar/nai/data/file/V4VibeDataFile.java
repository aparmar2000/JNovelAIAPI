package aparmar.nai.data.file;

import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.Base64Image;
import aparmar.nai.data.request.V4VibeData;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import aparmar.nai.utils.ByteArrayEncodings;
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
public class V4VibeDataFile extends DataFile<V4VibeDataFile> {
	protected int version = 1;
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	protected Base64Image image = new Base64Image();
	@Getter(value = AccessLevel.NONE)
	protected final Multimap<ImageGenModel, EmbeddingData> encodingMap = MultimapBuilder
			.enumKeys(ImageGenModel.class)
			.hashSetValues()
			.build();
	protected long createdAt = 0;
	protected ImportInfo importInfo = new ImportInfo();

	public V4VibeDataFile(Path filePath) {
		super(filePath);
	}

	protected V4VibeDataFile(Path filePath, int version, BufferedImage image, Multimap<ImageGenModel, EmbeddingData> embeddingMap, long createdAt, ImportInfo importInfo) {
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
	
	public List<V4VibeData> getVibeDataForModel(ImageGenModel model) {
		return encodingMap.get(model)
				.stream()
				.map(d->new V4VibeData(d.informationExtracted, getImageId(), model, d.encoding))
				.collect(Collectors.toList());
	}
	
	public void setImage(BufferedImage image) {
		this.image = new Base64Image(image);
		markChanged();
	}
	
	public void clearVibeData() {
		encodingMap.clear();
		markChanged();
	}
	
	public void clearVibeDataForModel(ImageGenModel model) {
		encodingMap.removeAll(model);
		markChanged();
	}
	
	public boolean addVibeData(V4VibeData vibeData) {
		boolean added = encodingMap.put(vibeData.getModel(), new EmbeddingData(vibeData));
		if (added) { markChanged(); }
		return added;
	}
	
	public boolean removeVibeData(V4VibeData vibeData) {
		boolean removed = encodingMap.remove(vibeData.getModel(), new EmbeddingData(vibeData));
		if (removed) { markChanged(); }
		return removed;
	}
	
	public void setImportInfo(ImportInfo importInfo) {
		this.importInfo = importInfo;
		markChanged();
	}
	

	@Override
	protected void innerSave() throws IOException {
		JsonObject root = new JsonObject();
		
		root.addProperty("identifier", "novelai-vibe-transfer");
		root.addProperty("version", version);
		root.addProperty("type", "image");
		root.add("image", gson.toJsonTree(image));
		root.addProperty("id", image.generateSha256());
		
		JsonObject encodingsRoot = new JsonObject();
		for (ImageGenModel model : encodingMap.keySet()) {
			JsonObject modelEncodingRoot = new JsonObject();
			
			for (EmbeddingData encodingData : encodingMap.get(model)) {
				JsonObject encodingRoot = new JsonObject();
				
				encodingRoot.addProperty("encoding", ByteArrayEncodings.encodeByteArrayToB64(encodingData.getEncoding()));
				JsonObject paramsRoot = new JsonObject();
				paramsRoot.addProperty("information_extracted", encodingData.getInformationExtracted());
				encodingRoot.add("params", paramsRoot);
				
				modelEncodingRoot.add(encodingData.getParamSha256(), encodingRoot);
			}
			
			encodingsRoot.add(gson.toJson(model), modelEncodingRoot);
		}
		root.add("encodings", encodingsRoot);
		
		root.addProperty("name", getFilePath().getFileName().toString());
		root.add("thumbnail", gson.toJsonTree(new Base64Image(image.getImage(), 256, 256, false))); // TODO: Not quite right - NAI preserves aspect ratio
		root.addProperty("createdAt", createdAt);
		root.add("importInfo", gson.toJsonTree(importInfo));
		
		try (FileWriter writer = new FileWriter(getFilePath().toFile())) {
			gson.toJson(root, writer);
		}
	}

	@Override
	protected V4VibeDataFile innerLoad() throws IOException {
		JsonObject root;
		try (FileReader reader = new FileReader(getFilePath().toFile())) {
			root = gson.fromJson(reader, JsonObject.class);
		}
		version = root.get("version").getAsInt();
		image = gson.fromJson(root.get("image"), Base64Image.class);
		
		JsonObject encodingsRoot = root.getAsJsonObject("encodings");
		encodingMap.clear();
		for (Entry<String, JsonElement> modelEntry : encodingsRoot.entrySet()) {
			ImageGenModel model = gson.fromJson(modelEntry.getKey(), ImageGenModel.class);
			
			for (Entry<String, JsonElement> encodingEntry : modelEntry.getValue().getAsJsonObject().entrySet()) {
				JsonObject encodingEntryValue = encodingEntry.getValue().getAsJsonObject();
				JsonObject encodingEntryParams = encodingEntryValue.getAsJsonObject("params");
				encodingMap.put(model, new EmbeddingData(
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
