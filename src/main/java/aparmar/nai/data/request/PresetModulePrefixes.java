package aparmar.nai.data.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PresetModulePrefixes {
	NO_MODULE("vanilla"),

	CROSSGENRE("general_crossgenre"),
	TEXT_ADVENTURE("theme_textadventure"),
	INSTRUCT("special_instruct"),
	PROSE_AUGMENTER("special_proseaugmenter"),
	/** Expected format:
	 * <pre>***\n
	 *⁂\n
	 *[ generation_type: generation_prompt ]\n</pre>
	 */
	LOREBOOK_GENERATION("utility_lorebookgenerator"),
	OPENINGS("special_openings"), // Hidden module, maybe only Kayra has this?
	
	AUTHOR_ALGERNON_BLACKWOOD("style_algernonblackwood"),
	AUTHOR_ARTHUR_CONAN_DOYLE("style_arthurconandoyle"),
	AUTHOR_EDGAR_ALLAN_POE("style_edgarallanpoe"),
	AUTHOR_H_P_LOVECRAFT("style_hplovecraft"),
	AUTHOR_SHERIDAN_LE_FANU("style_shridanlefanu"),
	AUTHOR_JANE_AUSTEN("style_janeausten"),
	AUTHOR_JULES_VERNE("style_julesverne"),
	AUTHOR_WILLIAM_SHAKESPEARE("style_williamshakespeare"),
	
	THEME_19TH_CENTURY_ROMANCE("theme_19thcenturyromance"),
	THEME_ACTION_ARCHEOLOGY("theme_actionarcheology"),
	THEME_ARTIFICIAL_INTELLIGENCE("theme_ai"),
	THEME_ANCIENT_CHINA("theme_ancientchina"),
	THEME_ANCIENT_GREECE("theme_ancientgreek"),
	THEME_ANCIENT_INDIA("theme_india"),
	THEME_ANIMAL_FICTION("theme_animalfiction"),
	THEME_ANTHROPOMORPHIC_ANIMALS("theme_anthropomorphicanimals"),
	THEME_CHILDRENS_FICTION("theme_childrens"),
	THEME_CHRISTMAS("theme_christmas"),
	THEME_COMEDIC_FANTASY("theme_comedicfantasy"),
	THEME_CONTEMPORARY("theme_contemporary"),
	THEME_CYBERPUNK("theme_cyberpunk"),
	THEME_DARK_FANTASY("theme_darkfantasy"),
	THEME_DRAGONS("theme_dragons"),
	THEME_EGYPT("theme_egypt"),
	THEME_FEUDAL_JAPAN("theme_feudaljapan"),
	THEME_GAMING("theme_gaming"),
	THEME_GENERAL_FANTASY("theme_generalfantasy"),
	THEME_GOLDEN_AGE_SCIFI("theme_goldenagescifi"),
	THEME_HARD_SCIFI("theme_hardsf"),
	THEME_HISTORY("theme_history"),
	THEME_HORROR("theme_horror"),
	THEME_HUNTER_GATHERER("theme_huntergatherer"),
	THEME_LITRPG("theme_litrpg"),
	THEME_MAGIC_ACADEMY("theme_magicacademy"),
	THEME_MAGIC_LIBRARY("theme_libraries"),
	THEME_LIGHT_NOVELS("theme_lightnovels"),
	THEME_MARS_COLONIZATION("theme_mars"),
	THEME_MEDIEVAL("theme_medieval"),
	THEME_MILITARY_SCIFI("theme_militaryscifi"),
	THEME_MUSIC("theme_music"),
	THEME_MYSTERY("theme_mystery"),
	THEME_NATURE("theme_nature"),
	THEME_NAVAL_AGE_OF_DISCOVERY("theme_naval"),
	THEME_NOIR("theme_noir"),
	THEME_PHILOSOPHY("theme_philosophy"),
	THEME_PIRATES("theme_pirates"),
	THEME_POETIC_FANTASY("theme_poeticfantasy"),
	THEME_POST_APOCALYPTIC("theme_postapocalyptic"),
	THEME_RATS("theme_rats"),
	THEME_ROMAN_EMPIRE("theme_romanempire"),
	THEME_SCIENCE_FANTASY("theme_sciencefantasy"),
	THEME_SPACE_OPERA("theme_spaceopera"),
	THEME_SUPERHEROES("theme_superheroes"),
	THEME_STEAMPUNK("theme_airships"),
	THEME_TRAVEL("theme_travel"),
	THEME_URBAN_FANTASY("theme_urbanfantasy"),
	THEME_VALENTINES("theme_valentines"),
	THEME_VIKINGS("theme_vikings"),
	THEME_WEIRD_WEST("theme_weirdwest"),
	THEME_WESTERN_ROMANCE("theme_westernromance"),
	
	INSPIRATION_CRAB_SNAIL_AND_MONKEY("inspiration_crabsnailandmonkey"),
	INSPIRATION_MERCANTILE_WOLFGIRL_ROMANCE("inspiration_mercantilewolfgirlromance"),
	INSPIRATION_NERVEGEAR("inspiration_nervegear"),
	INSPIRATION_ROMANCE_OF_THE_THREE_KINGDOMS("theme_romanceofthreekingdoms"),
	INSPIRATION_THRONEWARS("inspiration_thronewars"),
	INSPIRATION_WITCH_AT_LEVEL_CAP("inspiration_witchatlevelcap");
	
	private final String prefix;
}