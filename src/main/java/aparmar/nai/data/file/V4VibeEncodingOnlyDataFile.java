package aparmar.nai.data.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import aparmar.nai.data.request.V4VibeData;
import aparmar.nai.data.request.V4VibeData.VibeEncodingType;
import aparmar.nai.utils.ByteArrayEncodings;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class V4VibeEncodingOnlyDataFile extends V4VibeDataFile<V4VibeEncodingOnlyDataFile> {
	@Getter(value = AccessLevel.NONE)
	@Nullable
	protected EncodingEntry encodingEntry;

	public V4VibeEncodingOnlyDataFile(Path filePath) {
		super(filePath);
	}

	protected V4VibeEncodingOnlyDataFile(Path filePath, int version, long createdAt, ImportInfo importInfo, @Nullable EncodingEntry encodingEntry) {
		super(filePath, version, createdAt, importInfo);
		this.encodingEntry = encodingEntry;
	}

	@Override
	public String getId() {
		return ByteArrayEncodings.encodeStringBytesToSha256Hex(ByteArrayEncodings.encodeByteArrayToB64(encodingEntry.getEncoding()));
	}

	@Override
	public VibeFileType getType() {
		return VibeFileType.ENCODING;
	}

	@Override
	public int getEncodingCount() {
		return encodingEntry == null?0:1;
	}

	@Override
	public List<V4VibeData> getVibeDataForEncodingType(VibeEncodingType encodingType) {
		if (encodingEntry == null || encodingEntry.getEncodingType() != encodingType) {
			return Collections.emptyList();
		}
		return Collections.singletonList(new V4VibeData(null, null, encodingEntry.getEncodingType(), encodingEntry.getEncoding()));
	}

	public void clearVibeData() {
		setVibeData(null);
	}

	public void clearVibeDataForEncodingType(VibeEncodingType encodingType) {
		if (encodingEntry == null || encodingEntry.getEncodingType() != encodingType) {
			return;
		}
		setVibeData(null);
	}

	public boolean addVibeData(V4VibeData vibeData) {
		if (encodingEntry != null) {
			throw new UnsupportedOperationException("Cannot add more than one encoding to a V4VibeEncodingOnlyDataFile!");
		}
		setVibeData(vibeData);
		return true;
	}

	public boolean removeVibeData(V4VibeData vibeData) {
		if (encodingEntry == null) {
			return false;
		}
		if (encodingEntry.getEncodingType() == vibeData.getEncodingType() && Arrays.equals(encodingEntry.getEncoding(), vibeData.getEncoding())) {
			encodingEntry = null;
			markChanged();
			return true;
		}
		return false;
	}

	public void setVibeData(@Nullable V4VibeData vibeData) {
		if (vibeData != null) {
			encodingEntry = new EncodingEntry(vibeData);
		} else {
			encodingEntry = null;
		}
		markChanged();
	}
	

	@Override
	public void saveToStream(OutputStream outputStream) throws IOException {
		if (getEncodingCount() == 0) {
			throw new IOException("Cannot save a vibe encoding file containing no encodings!");
		}
		JsonObject root = new JsonObject();
		
		root.addProperty("identifier", "novelai-vibe-transfer");
		root.addProperty("version", version);
		root.addProperty("type", "encoding");
		root.addProperty("id", getId());
		
		JsonObject encodingsRoot = new JsonObject();
		JsonObject encodingTypeRoot = new JsonObject();
		JsonObject encodingRoot = new JsonObject();
		
		encodingRoot.addProperty("encoding", ByteArrayEncodings.encodeByteArrayToB64(encodingEntry.getEncoding()));
		
		encodingTypeRoot.add("unknown", encodingRoot);
		encodingsRoot.add(gson.toJson(encodingEntry.getEncodingType()), encodingTypeRoot);
		root.add("encodings", encodingsRoot);
		
		root.addProperty("name", getFilePath().getFileName().toString());
		root.addProperty("createdAt", createdAt);
		root.add("importInfo", gson.toJsonTree(importInfo));
		
		try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
			gson.toJson(root, outputStreamWriter);
		}
	}

	@Override
	public V4VibeEncodingOnlyDataFile loadFromStream(InputStream inputStream) throws IOException {
		JsonObject root;
		try (InputStreamReader reader = new InputStreamReader(inputStream)) {
			root = gson.fromJson(reader, JsonObject.class);
		}
		version = root.get("version").getAsInt();
		
		this.encodingEntry = null;
		JsonObject encodingsRoot = root.getAsJsonObject("encodings");
		for (Entry<String, JsonElement> modelEntry : encodingsRoot.entrySet()) {
			VibeEncodingType encodingType = gson.fromJson(modelEntry.getKey(), VibeEncodingType.class);
			
			for (Entry<String, JsonElement> encodingEntry : modelEntry.getValue().getAsJsonObject().entrySet()) {
				if (this.encodingEntry != null) {
					throw new IOException("More than one encoding found in vibe data file of type encoding!");
				}
				JsonObject encodingEntryValue = encodingEntry.getValue().getAsJsonObject();
				this.encodingEntry = new EncodingEntry( ByteArrayEncodings.decodeB64ToByteArray(encodingEntryValue.get("encoding").getAsString()), encodingType );
			}
		}
		
		createdAt = root.get("createdAt").getAsLong();
		importInfo = gson.fromJson(root.get("importInfo"), ImportInfo.class);
		
		return this;
	}

	@Override
	protected V4VibeEncodingOnlyDataFile innerCloneWithNewPath(Path path) {
		return new V4VibeEncodingOnlyDataFile(
				path, 
				version, 
				createdAt, 
				importInfo,
				encodingEntry);
	}
	
	@Value
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	@Builder(toBuilder = true)
	public static class EncodingEntry {
		
		public EncodingEntry(V4VibeData vibeData) {
			this.encoding = vibeData.getEncoding();
			this.encodingType = vibeData.getEncodingType();
		}
		
		@Builder.Default
		protected final byte[] encoding = new byte[0];
		@NonNull
		protected final VibeEncodingType encodingType;
	}

}
