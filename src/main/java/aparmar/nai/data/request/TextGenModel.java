package aparmar.nai.data.request;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import aparmar.nai.utils.tokenization.Tokenizers;

@Getter
public enum TextGenModel {
	@SerializedName("blue")
	BLUE,
	@SerializedName("red")
	RED,
	@SerializedName("green")
	GREEN,
	@SerializedName("purple")
	PURPLE,
	
	@SerializedName("hypebot")
	HYPEBOT,
	@SerializedName("infillmodel")
	INFILL,
	@SerializedName("cassandra")
	CASSANDRA,

	@SerializedName("2.7B")
	CALLIOPE,
	@SerializedName("genji-python-6b")
	SNEK,
	@SerializedName("genji-jp-6b")
	GENJI_OLD,
	@SerializedName("genji-jp-6b-v2")
	GENJI,
	@SerializedName("sigurd-2.9b-v1")
	SIGURD_OLD,
	@SerializedName("6B-v4")
	SIGURD(true, true, new PresetModulePrefixes[]
			{PresetModulePrefixes.TEXT_ADVENTURE,
			PresetModulePrefixes.PROSE_AUGMENTER,
			PresetModulePrefixes.CROSSGENRE,
			PresetModulePrefixes.AUTHOR_ALGERNON_BLACKWOOD,
			PresetModulePrefixes.AUTHOR_ARTHUR_CONAN_DOYLE,
			PresetModulePrefixes.AUTHOR_EDGAR_ALLAN_POE,
			PresetModulePrefixes.AUTHOR_H_P_LOVECRAFT,
			PresetModulePrefixes.AUTHOR_SHERIDAN_LE_FANU,
			PresetModulePrefixes.AUTHOR_JANE_AUSTEN,
			PresetModulePrefixes.AUTHOR_JULES_VERNE,
			PresetModulePrefixes.AUTHOR_WILLIAM_SHAKESPEARE}),
	@SerializedName("euterpe-v2")
	EUTERPE(true, true, new PresetModulePrefixes[]
			{PresetModulePrefixes.TEXT_ADVENTURE,
			PresetModulePrefixes.PROSE_AUGMENTER,
			PresetModulePrefixes.CROSSGENRE,
			PresetModulePrefixes.AUTHOR_ALGERNON_BLACKWOOD,
			PresetModulePrefixes.AUTHOR_ARTHUR_CONAN_DOYLE,
			PresetModulePrefixes.AUTHOR_EDGAR_ALLAN_POE,
			PresetModulePrefixes.AUTHOR_H_P_LOVECRAFT,
			PresetModulePrefixes.AUTHOR_SHERIDAN_LE_FANU,
			PresetModulePrefixes.AUTHOR_JANE_AUSTEN,
			PresetModulePrefixes.AUTHOR_JULES_VERNE,
			PresetModulePrefixes.AUTHOR_WILLIAM_SHAKESPEARE}),
	@SerializedName("krake-v2")
	KRAKE(false, true, new PresetModulePrefixes[]
			{PresetModulePrefixes.TEXT_ADVENTURE,
			PresetModulePrefixes.CROSSGENRE,
			PresetModulePrefixes.AUTHOR_ALGERNON_BLACKWOOD,
			PresetModulePrefixes.AUTHOR_ARTHUR_CONAN_DOYLE,
			PresetModulePrefixes.AUTHOR_EDGAR_ALLAN_POE,
			PresetModulePrefixes.AUTHOR_H_P_LOVECRAFT,
			PresetModulePrefixes.AUTHOR_SHERIDAN_LE_FANU,
			PresetModulePrefixes.AUTHOR_JANE_AUSTEN,
			PresetModulePrefixes.AUTHOR_JULES_VERNE,
			PresetModulePrefixes.AUTHOR_WILLIAM_SHAKESPEARE}),
	@SerializedName("clio-v1")
	CLIO(false, false, new PresetModulePrefixes[]
			{PresetModulePrefixes.TEXT_ADVENTURE,
			PresetModulePrefixes.INSTRUCT,
			PresetModulePrefixes.PROSE_AUGMENTER}),
	@SerializedName("kayra-v1")
	KAYRA(false, false, new PresetModulePrefixes[]
			{PresetModulePrefixes.TEXT_ADVENTURE,
			PresetModulePrefixes.INSTRUCT,
			PresetModulePrefixes.PROSE_AUGMENTER});

	private final boolean supportsCustomModules, supportsHiddenStates;
	private final PresetModulePrefixes[] compatiblePresetModules;
	
	private TextGenModel() {
		supportsCustomModules = false;
		supportsHiddenStates = true;
		compatiblePresetModules = new PresetModulePrefixes[] {PresetModulePrefixes.NO_MODULE};
	}
	private TextGenModel(boolean supportsCustomModules, boolean supportsHiddenStates,
			PresetModulePrefixes[] presetModules) {
		this.supportsCustomModules = supportsCustomModules;
		this.supportsHiddenStates = supportsHiddenStates;
		
		List<PresetModulePrefixes> moduleList = new LinkedList<>(Arrays.asList(presetModules));
		moduleList.add(PresetModulePrefixes.NO_MODULE);
		compatiblePresetModules = moduleList.toArray(new PresetModulePrefixes[0]);
	}
	
	public Tokenizers getTokenizerForModel() {
		return Tokenizers.getTokenizerForModel(this);
	}
}