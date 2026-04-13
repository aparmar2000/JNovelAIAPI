package aparmar.nai.utils;

import static aparmar.nai.utils.HelperConstants.TEXTGEN_PRESET_FOLDER;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import aparmar.nai.data.file.TextGenPresetDataFile;
import aparmar.nai.data.request.TextGenModel;
import aparmar.nai.data.request.textgen.TextGenerationParameters;
import lombok.Synchronized;
import lombok.val;

public class TextParameterPresets {
	private static final HashMap<UUID, TextGenPresetDataFile> presets = new HashMap<>();
	private static final HashBiMap<String, UUID> presetNameMap = HashBiMap.create();
	private static final ListMultimap<TextGenModel, UUID> modelPresetAssociations = MultimapBuilder
			.enumKeys(TextGenModel.class)
			.arrayListValues()
			.build();
	
	private static final Object loadLock = new Object();
	private static final Object presetDataLock = new Object();
	
	@Synchronized("loadLock")
	private static void loadPresetData() {
		if (!presets.isEmpty()) {
			return;
		}

		presetNameMap.clear();
		modelPresetAssociations.clear();
		try {
			InternalResourceLoader.walkInternalResourceFolderContents(TEXTGEN_PRESET_FOLDER, TextParameterPresets::loadInternalPresetDataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Synchronized("presetDataLock")
	private static void loadInternalPresetDataFile(Path path) {
		if (Files.isDirectory(path)) {
			return;
		}
		TextGenPresetDataFile presetDataFile = null;
		try (InputStream in = InternalResourceLoader.getInternalResourceAsStream(path)) {
			TextGenPresetDataFile loadedPresetDataFile = new TextGenPresetDataFile(path);
			loadedPresetDataFile.loadFromStream(in);
			
			presetDataFile = loadedPresetDataFile;
		} catch (IOException e) {
			return;
		}
		
		presets.put(presetDataFile.getId(), presetDataFile);
		presetNameMap.put(makeExtendedName(presetDataFile.getModel(), presetDataFile.getName()), presetDataFile.getId());
		modelPresetAssociations.put(presetDataFile.getModel(), presetDataFile.getId());
	}
	

	public static String makeExtendedName(TextGenModel model, String presetName) {
		return model.name()+" - "+presetName;
	}
	public static String[] getPresetExtendedNames() {
		loadPresetData();
		return presetNameMap.keySet().toArray(new String[0]);
	}
	public static String[] getAssociatedPresetExtendedNames(TextGenModel model) {
		loadPresetData();
		val presetUuids = modelPresetAssociations.get(model);
		return presetUuids.stream()
				.map(presetNameMap.inverse()::get)
				.filter(Objects::nonNull)
				.toArray(String[]::new);
	}
	public static TextGenPresetDataFile[] getAssociatedPresetDataFiles(TextGenModel model) {
		loadPresetData();
		val presetUuids = modelPresetAssociations.get(model);
		return presetUuids.stream()
				.map(presets::get)
				.filter(Objects::nonNull)
				.toArray(TextGenPresetDataFile[]::new);
	}
	public static TextGenerationParameters[] getAssociatedPresetParameters(TextGenModel model) {
		return Arrays.stream(getAssociatedPresetDataFiles(model))
				.map(TextGenPresetDataFile::getParameterInst)
				.toArray(TextGenerationParameters[]::new);
	}
	public static TextGenPresetDataFile getPresetFileByExtendedName(String presetName) {
		loadPresetData();
		return Optional.ofNullable(presetNameMap.get(presetName))
				.map(presets::get)
				.orElse(null);
	}
	public static TextGenerationParameters getPresetParametersByExtendedName(String presetName) {
		val presetFile = getPresetFileByExtendedName(presetName);
		return presetFile != null ? presetFile.getParameterInst() : null;
	}
	public static TextGenPresetDataFile getPresetFileByNameAndModel(TextGenModel model, String presetName) {
		return getPresetFileByExtendedName(makeExtendedName(model, presetName));
	}
	public static TextGenerationParameters getPresetParametersByNameAndModel(TextGenModel model, String presetName) {
		return getPresetParametersByExtendedName(makeExtendedName(model, presetName));
	}

	/**
	 * @deprecated since 5.6.0, use {@link TextParameterPresets#getPresetExtendedNames()} instead.
	 */
	@Deprecated
	public static String[] getPresetNames() {
		return getPresetExtendedNames();
	}
	/**
	 * @deprecated since 5.6.0, use {@link TextParameterPresets#getPresetParametersByExtendedName()} instead.
	 */
	@Deprecated
	public static TextGenerationParameters getPresetByExtendedName(String presetName) {
		return getPresetParametersByExtendedName(presetName);
	}
	/**
	 * @deprecated since 5.6.0, use {@link TextParameterPresets#getPresetParametersByNameAndModel()} instead.
	 */
	@Deprecated
	public static TextGenerationParameters getPresetByNameAndModel(TextGenModel model, String presetName) {
		return getPresetParametersByNameAndModel(model, presetName);
	}
	/**
	 * @deprecated since 5.6.0, use {@link TextParameterPresets#getAssociatedPresetExtendedNames()} instead.
	 */
	@Deprecated
	public static String[] getAssociatedPresets(TextGenModel model) {
		return getAssociatedPresetExtendedNames(model);
	}
	public static String trimPresetNameForDisplay(String presetName) {
		return presetName.replaceFirst("[A-Z_]+ - ", "");
	}
}
