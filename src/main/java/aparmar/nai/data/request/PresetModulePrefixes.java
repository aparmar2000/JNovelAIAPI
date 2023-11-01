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
	OPENINGS("special_openings"), // Hidden module, maybe only Kayra has this?
	
	AUTHOR_ALGERNON_BLACKWOOD("style_algernonblackwood"),
	AUTHOR_ARTHUR_CONAN_DOYLE("style_arthurconandoyle"),
	AUTHOR_EDGAR_ALLAN_POE("style_edgarallanpoe"),
	AUTHOR_H_P_LOVECRAFT("style_hplovecraft"),
	AUTHOR_SHERIDAN_LE_FANU("style_shridanlefanu"),
	AUTHOR_JANE_AUSTEN("style_janeausten"),
	AUTHOR_JULES_VERNE("style_julesverne"),
	AUTHOR_WILLIAM_SHAKESPEARE("style_williamshakespeare"); // TODO: The rest of the default modules
	
	private final String prefix;
}