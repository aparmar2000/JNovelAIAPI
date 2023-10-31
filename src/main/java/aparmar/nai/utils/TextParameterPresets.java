package main.java.aparmar.nai.utils;

import static main.java.aparmar.nai.utils.HelperConstants.PRESET_FILENAME;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ObjectArrays;
import com.google.gson.Gson;

import main.java.aparmar.nai.data.request.TextGenModel;
import main.java.aparmar.nai.data.request.TextGenerationParameters;

public class TextParameterPresets {
	private static final Gson gson = new Gson();
	
	private static final HashMap<String, TextGenerationParameters> presets = new HashMap<>();
	private static final HashMap<TextGenModel, String[]> modelPresetAssociations = new HashMap<>();
	
	private static final String LINE_PARSE_REGEX = "(([A-Z_]+) - [^:]+): *?(\\{.+\\})$";
    private static final Pattern LINE_PARSE_PATTERN = Pattern.compile(LINE_PARSE_REGEX, Pattern.MULTILINE);
	private static void parsePresetFileLine(String line) {
		Matcher matcher = LINE_PARSE_PATTERN.matcher(line);
		if (!matcher.find()) { return; }
		String presetName = matcher.group(1);
		String modelName = matcher.group(2);
		TextGenerationParameters loadedParameters = gson.fromJson(matcher.group(3),TextGenerationParameters.class);
		
		presets.put(presetName, loadedParameters);
		modelPresetAssociations.merge(
				TextGenModel.valueOf(modelName.trim()), 
				new String[] {presetName}, 
				(a,b)->ObjectArrays.concat(a, b, String.class));
	}
	
	private static synchronized void loadPresetData() {
		if (!presets.isEmpty()) {
			return;
		}

		modelPresetAssociations.clear();
		try (BufferedReader fIn = new BufferedReader(new FileReader(
				InternalResourceLoader.getInternalFile(PRESET_FILENAME)))) {
			fIn.lines()
				.filter(s->!s.trim().isEmpty())
				.sequential()
				.forEach(TextParameterPresets::parsePresetFileLine);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[] getPresetNames() {
		loadPresetData();
		return presets.keySet().toArray(new String[0]);
	}
	public static TextGenerationParameters getPresetByExtendedName(String presetName) {
		loadPresetData();
		return presets.get(presetName);
	}
	public static TextGenerationParameters getPresetByNameAndModel(TextGenModel model, String presetName) {
		loadPresetData();
		return presets.get(model.name()+" - "+presetName);
	}
	public static String[] getAssociatedPresets(TextGenModel model) {
		loadPresetData();
		return modelPresetAssociations.getOrDefault(model, new String[0]);
	}
	public static String trimPresetNameForDisplay(String presetName) {
		return presetName.replaceFirst("[A-Z_]+ - ", "");
	}
}
