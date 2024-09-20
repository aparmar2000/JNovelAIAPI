package aparmar.nai.data.request;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import aparmar.nai.utils.HelperConstants;
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
	SIGURD(true, true, HelperConstants.GENERAL_API_ROOT, new PresetModulePrefixes[]
			{PresetModulePrefixes.TEXT_ADVENTURE,
			PresetModulePrefixes.PROSE_AUGMENTER,
			PresetModulePrefixes.CROSSGENRE,
			PresetModulePrefixes.LOREBOOK_GENERATION,
			
			PresetModulePrefixes.AUTHOR_ALGERNON_BLACKWOOD,
			PresetModulePrefixes.AUTHOR_ARTHUR_CONAN_DOYLE,
			PresetModulePrefixes.AUTHOR_EDGAR_ALLAN_POE,
			PresetModulePrefixes.AUTHOR_H_P_LOVECRAFT,
			PresetModulePrefixes.AUTHOR_SHERIDAN_LE_FANU,
			PresetModulePrefixes.AUTHOR_JANE_AUSTEN,
			PresetModulePrefixes.AUTHOR_JULES_VERNE,
			PresetModulePrefixes.AUTHOR_WILLIAM_SHAKESPEARE,
			
			PresetModulePrefixes.THEME_19TH_CENTURY_ROMANCE,
			PresetModulePrefixes.THEME_ACTION_ARCHEOLOGY,
			PresetModulePrefixes.THEME_ARTIFICIAL_INTELLIGENCE,
			PresetModulePrefixes.THEME_ANCIENT_CHINA,
			PresetModulePrefixes.THEME_ANCIENT_GREECE,
			PresetModulePrefixes.THEME_ANCIENT_INDIA,
			PresetModulePrefixes.THEME_ANIMAL_FICTION,
			PresetModulePrefixes.THEME_ANTHROPOMORPHIC_ANIMALS,
			PresetModulePrefixes.THEME_CHILDRENS_FICTION,
			PresetModulePrefixes.THEME_CHRISTMAS,
			PresetModulePrefixes.THEME_COMEDIC_FANTASY,
			PresetModulePrefixes.THEME_CONTEMPORARY,
			PresetModulePrefixes.THEME_CYBERPUNK,
			PresetModulePrefixes.THEME_DARK_FANTASY,
			PresetModulePrefixes.THEME_DRAGONS,
			PresetModulePrefixes.THEME_EGYPT,
			PresetModulePrefixes.THEME_FEUDAL_JAPAN,
			PresetModulePrefixes.THEME_GAMING,
			PresetModulePrefixes.THEME_GENERAL_FANTASY,
			PresetModulePrefixes.THEME_GOLDEN_AGE_SCIFI,
			PresetModulePrefixes.THEME_HARD_SCIFI,
			PresetModulePrefixes.THEME_HISTORY,
			PresetModulePrefixes.THEME_HORROR,
			PresetModulePrefixes.THEME_HUNTER_GATHERER,
			PresetModulePrefixes.THEME_LITRPG,
			PresetModulePrefixes.THEME_MAGIC_ACADEMY,
			PresetModulePrefixes.THEME_MAGIC_LIBRARY,
			PresetModulePrefixes.THEME_LIGHT_NOVELS,
			PresetModulePrefixes.THEME_MARS_COLONIZATION,
			PresetModulePrefixes.THEME_MEDIEVAL,
			PresetModulePrefixes.THEME_MILITARY_SCIFI,
			PresetModulePrefixes.THEME_MUSIC,
			PresetModulePrefixes.THEME_MYSTERY,
			PresetModulePrefixes.THEME_NATURE,
			PresetModulePrefixes.THEME_NAVAL_AGE_OF_DISCOVERY,
			PresetModulePrefixes.THEME_NOIR,
			PresetModulePrefixes.THEME_PHILOSOPHY,
			PresetModulePrefixes.THEME_PIRATES,
			PresetModulePrefixes.THEME_POETIC_FANTASY,
			PresetModulePrefixes.THEME_POST_APOCALYPTIC,
			PresetModulePrefixes.THEME_RATS,
			PresetModulePrefixes.THEME_ROMAN_EMPIRE,
			PresetModulePrefixes.THEME_SCIENCE_FANTASY,
			PresetModulePrefixes.THEME_SPACE_OPERA,
			PresetModulePrefixes.THEME_SUPERHEROES,
			PresetModulePrefixes.THEME_STEAMPUNK,
			PresetModulePrefixes.THEME_TRAVEL,
			PresetModulePrefixes.THEME_URBAN_FANTASY,
			PresetModulePrefixes.THEME_VALENTINES,
			PresetModulePrefixes.THEME_VIKINGS,
			PresetModulePrefixes.THEME_WEIRD_WEST,
			PresetModulePrefixes.THEME_WESTERN_ROMANCE,
			
			PresetModulePrefixes.INSPIRATION_CRAB_SNAIL_AND_MONKEY,
			PresetModulePrefixes.INSPIRATION_MERCANTILE_WOLFGIRL_ROMANCE,
			PresetModulePrefixes.INSPIRATION_NERVEGEAR,
			PresetModulePrefixes.INSPIRATION_ROMANCE_OF_THE_THREE_KINGDOMS,
			PresetModulePrefixes.INSPIRATION_THRONEWARS,
			PresetModulePrefixes.INSPIRATION_WITCH_AT_LEVEL_CAP}),
	@SerializedName("euterpe-v2")
	EUTERPE(true, true, HelperConstants.GENERAL_API_ROOT, new PresetModulePrefixes[]
			{PresetModulePrefixes.TEXT_ADVENTURE,
			PresetModulePrefixes.PROSE_AUGMENTER,
			PresetModulePrefixes.CROSSGENRE,
			PresetModulePrefixes.LOREBOOK_GENERATION,
			
			PresetModulePrefixes.AUTHOR_ALGERNON_BLACKWOOD,
			PresetModulePrefixes.AUTHOR_ARTHUR_CONAN_DOYLE,
			PresetModulePrefixes.AUTHOR_EDGAR_ALLAN_POE,
			PresetModulePrefixes.AUTHOR_H_P_LOVECRAFT,
			PresetModulePrefixes.AUTHOR_SHERIDAN_LE_FANU,
			PresetModulePrefixes.AUTHOR_JANE_AUSTEN,
			PresetModulePrefixes.AUTHOR_JULES_VERNE,
			PresetModulePrefixes.AUTHOR_WILLIAM_SHAKESPEARE,
			
			PresetModulePrefixes.THEME_19TH_CENTURY_ROMANCE,
			PresetModulePrefixes.THEME_ACTION_ARCHEOLOGY,
			PresetModulePrefixes.THEME_ARTIFICIAL_INTELLIGENCE,
			PresetModulePrefixes.THEME_ANCIENT_CHINA,
			PresetModulePrefixes.THEME_ANCIENT_GREECE,
			PresetModulePrefixes.THEME_ANCIENT_INDIA,
			PresetModulePrefixes.THEME_ANIMAL_FICTION,
			PresetModulePrefixes.THEME_ANTHROPOMORPHIC_ANIMALS,
			PresetModulePrefixes.THEME_CHILDRENS_FICTION,
			PresetModulePrefixes.THEME_CHRISTMAS,
			PresetModulePrefixes.THEME_COMEDIC_FANTASY,
			PresetModulePrefixes.THEME_CONTEMPORARY,
			PresetModulePrefixes.THEME_CYBERPUNK,
			PresetModulePrefixes.THEME_DARK_FANTASY,
			PresetModulePrefixes.THEME_DRAGONS,
			PresetModulePrefixes.THEME_EGYPT,
			PresetModulePrefixes.THEME_FEUDAL_JAPAN,
			PresetModulePrefixes.THEME_GAMING,
			PresetModulePrefixes.THEME_GENERAL_FANTASY,
			PresetModulePrefixes.THEME_GOLDEN_AGE_SCIFI,
			PresetModulePrefixes.THEME_HARD_SCIFI,
			PresetModulePrefixes.THEME_HISTORY,
			PresetModulePrefixes.THEME_HORROR,
			PresetModulePrefixes.THEME_HUNTER_GATHERER,
			PresetModulePrefixes.THEME_LITRPG,
			PresetModulePrefixes.THEME_MAGIC_ACADEMY,
			PresetModulePrefixes.THEME_MAGIC_LIBRARY,
			PresetModulePrefixes.THEME_LIGHT_NOVELS,
			PresetModulePrefixes.THEME_MARS_COLONIZATION,
			PresetModulePrefixes.THEME_MEDIEVAL,
			PresetModulePrefixes.THEME_MILITARY_SCIFI,
			PresetModulePrefixes.THEME_MUSIC,
			PresetModulePrefixes.THEME_MYSTERY,
			PresetModulePrefixes.THEME_NATURE,
			PresetModulePrefixes.THEME_NAVAL_AGE_OF_DISCOVERY,
			PresetModulePrefixes.THEME_NOIR,
			PresetModulePrefixes.THEME_PHILOSOPHY,
			PresetModulePrefixes.THEME_PIRATES,
			PresetModulePrefixes.THEME_POETIC_FANTASY,
			PresetModulePrefixes.THEME_POST_APOCALYPTIC,
			PresetModulePrefixes.THEME_RATS,
			PresetModulePrefixes.THEME_ROMAN_EMPIRE,
			PresetModulePrefixes.THEME_SCIENCE_FANTASY,
			PresetModulePrefixes.THEME_SPACE_OPERA,
			PresetModulePrefixes.THEME_SUPERHEROES,
			PresetModulePrefixes.THEME_STEAMPUNK,
			PresetModulePrefixes.THEME_TRAVEL,
			PresetModulePrefixes.THEME_URBAN_FANTASY,
			PresetModulePrefixes.THEME_VALENTINES,
			PresetModulePrefixes.THEME_VIKINGS,
			PresetModulePrefixes.THEME_WEIRD_WEST,
			PresetModulePrefixes.THEME_WESTERN_ROMANCE,
			
			PresetModulePrefixes.INSPIRATION_CRAB_SNAIL_AND_MONKEY,
			PresetModulePrefixes.INSPIRATION_MERCANTILE_WOLFGIRL_ROMANCE,
			PresetModulePrefixes.INSPIRATION_NERVEGEAR,
			PresetModulePrefixes.INSPIRATION_ROMANCE_OF_THE_THREE_KINGDOMS,
			PresetModulePrefixes.INSPIRATION_THRONEWARS,
			PresetModulePrefixes.INSPIRATION_WITCH_AT_LEVEL_CAP}),
	@SerializedName("krake-v2")
	KRAKE(false, true, HelperConstants.GENERAL_API_ROOT, new PresetModulePrefixes[]
			{PresetModulePrefixes.TEXT_ADVENTURE,
			PresetModulePrefixes.CROSSGENRE,
			
			PresetModulePrefixes.AUTHOR_ALGERNON_BLACKWOOD,
			PresetModulePrefixes.AUTHOR_ARTHUR_CONAN_DOYLE,
			PresetModulePrefixes.AUTHOR_EDGAR_ALLAN_POE,
			PresetModulePrefixes.AUTHOR_H_P_LOVECRAFT,
			PresetModulePrefixes.AUTHOR_SHERIDAN_LE_FANU,
			PresetModulePrefixes.AUTHOR_JANE_AUSTEN,
			PresetModulePrefixes.AUTHOR_JULES_VERNE,
			PresetModulePrefixes.AUTHOR_WILLIAM_SHAKESPEARE,
			
			PresetModulePrefixes.THEME_19TH_CENTURY_ROMANCE,
			PresetModulePrefixes.THEME_ACTION_ARCHEOLOGY,
			PresetModulePrefixes.THEME_ARTIFICIAL_INTELLIGENCE,
			PresetModulePrefixes.THEME_ANCIENT_CHINA,
			PresetModulePrefixes.THEME_ANCIENT_GREECE,
			PresetModulePrefixes.THEME_ANCIENT_INDIA,
			PresetModulePrefixes.THEME_ANIMAL_FICTION,
			PresetModulePrefixes.THEME_ANTHROPOMORPHIC_ANIMALS,
			PresetModulePrefixes.THEME_CHILDRENS_FICTION,
			PresetModulePrefixes.THEME_CHRISTMAS,
			PresetModulePrefixes.THEME_COMEDIC_FANTASY,
			PresetModulePrefixes.THEME_CONTEMPORARY,
			PresetModulePrefixes.THEME_CYBERPUNK,
			PresetModulePrefixes.THEME_DARK_FANTASY,
			PresetModulePrefixes.THEME_DRAGONS,
			PresetModulePrefixes.THEME_EGYPT,
			PresetModulePrefixes.THEME_FEUDAL_JAPAN,
			PresetModulePrefixes.THEME_GAMING,
			PresetModulePrefixes.THEME_GENERAL_FANTASY,
			PresetModulePrefixes.THEME_GOLDEN_AGE_SCIFI,
			PresetModulePrefixes.THEME_HARD_SCIFI,
			PresetModulePrefixes.THEME_HISTORY,
			PresetModulePrefixes.THEME_HORROR,
			PresetModulePrefixes.THEME_HUNTER_GATHERER,
			PresetModulePrefixes.THEME_LITRPG,
			PresetModulePrefixes.THEME_MAGIC_ACADEMY,
			PresetModulePrefixes.THEME_MAGIC_LIBRARY,
			PresetModulePrefixes.THEME_LIGHT_NOVELS,
			PresetModulePrefixes.THEME_MARS_COLONIZATION,
			PresetModulePrefixes.THEME_MEDIEVAL,
			PresetModulePrefixes.THEME_MILITARY_SCIFI,
			PresetModulePrefixes.THEME_MUSIC,
			PresetModulePrefixes.THEME_MYSTERY,
			PresetModulePrefixes.THEME_NATURE,
			PresetModulePrefixes.THEME_NAVAL_AGE_OF_DISCOVERY,
			PresetModulePrefixes.THEME_NOIR,
			PresetModulePrefixes.THEME_PHILOSOPHY,
			PresetModulePrefixes.THEME_PIRATES,
			PresetModulePrefixes.THEME_POETIC_FANTASY,
			PresetModulePrefixes.THEME_POST_APOCALYPTIC,
			PresetModulePrefixes.THEME_RATS,
			PresetModulePrefixes.THEME_ROMAN_EMPIRE,
			PresetModulePrefixes.THEME_SCIENCE_FANTASY,
			PresetModulePrefixes.THEME_SPACE_OPERA,
			PresetModulePrefixes.THEME_SUPERHEROES,
			PresetModulePrefixes.THEME_STEAMPUNK,
			PresetModulePrefixes.THEME_TRAVEL,
			PresetModulePrefixes.THEME_URBAN_FANTASY,
			PresetModulePrefixes.THEME_VALENTINES,
			PresetModulePrefixes.THEME_VIKINGS,
			PresetModulePrefixes.THEME_WEIRD_WEST,
			PresetModulePrefixes.THEME_WESTERN_ROMANCE,
			
			PresetModulePrefixes.INSPIRATION_CRAB_SNAIL_AND_MONKEY,
			PresetModulePrefixes.INSPIRATION_MERCANTILE_WOLFGIRL_ROMANCE,
			PresetModulePrefixes.INSPIRATION_NERVEGEAR,
			PresetModulePrefixes.INSPIRATION_ROMANCE_OF_THE_THREE_KINGDOMS,
			PresetModulePrefixes.INSPIRATION_THRONEWARS,
			PresetModulePrefixes.INSPIRATION_WITCH_AT_LEVEL_CAP}),
	@SerializedName("clio-v1")
	CLIO(false, false, HelperConstants.GENERAL_API_ROOT, new PresetModulePrefixes[]
			{PresetModulePrefixes.TEXT_ADVENTURE,
			PresetModulePrefixes.INSTRUCT,
			PresetModulePrefixes.PROSE_AUGMENTER,
			PresetModulePrefixes.LOREBOOK_GENERATION}),
	@SerializedName("kayra-v1")
	KAYRA(false, false, HelperConstants.TEXT_API_ROOT, new PresetModulePrefixes[]
			{PresetModulePrefixes.TEXT_ADVENTURE,
			PresetModulePrefixes.INSTRUCT,
			PresetModulePrefixes.PROSE_AUGMENTER,
			PresetModulePrefixes.OPENINGS,
			PresetModulePrefixes.LOREBOOK_GENERATION});

	private final boolean supportsCustomModules, supportsHiddenStates;
	private final String endpoint;
	private final PresetModulePrefixes[] compatiblePresetModules;
	
	private TextGenModel() {
		supportsCustomModules = false;
		supportsHiddenStates = true;
		endpoint = HelperConstants.GENERAL_API_ROOT;
		compatiblePresetModules = new PresetModulePrefixes[] {PresetModulePrefixes.NO_MODULE};
	}
	private TextGenModel(boolean supportsCustomModules, boolean supportsHiddenStates,
			String apiRoot,
			PresetModulePrefixes[] presetModules) {
		this.supportsCustomModules = supportsCustomModules;
		this.supportsHiddenStates = supportsHiddenStates;
		this.endpoint = apiRoot;
		
		List<PresetModulePrefixes> moduleList = new LinkedList<>(Arrays.asList(presetModules));
		moduleList.add(PresetModulePrefixes.NO_MODULE);
		compatiblePresetModules = moduleList.toArray(new PresetModulePrefixes[0]);
	}
	
	public Tokenizers getTokenizerForModel() {
		return Tokenizers.getTokenizerForModel(this);
	}
}