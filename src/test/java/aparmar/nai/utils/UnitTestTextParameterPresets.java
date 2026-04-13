package aparmar.nai.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import aparmar.nai.data.request.TextGenModel;

class UnitTestTextParameterPresets {

	@Test
	void testGetPresetNames() {
		String[] sortedPresetNames = TextParameterPresets.getPresetExtendedNames();
		Arrays.sort(sortedPresetNames);
		
		assertArrayEquals(
				new String[] {"CLIO - Edgewise", "CLIO - Fresh Coffee", "CLIO - Long Press", "CLIO - Talker C", "CLIO - Vingt-Un", "ERATO - Dragonfruit", "ERATO - Golden Arrow", "ERATO - Wilder", "ERATO - Zany Scribe", "ERATO - 小説家", "EUTERPE - Ace of Spades", "EUTERPE - All-Nighter", "EUTERPE - Basic Coherence", "EUTERPE - Fandango", "EUTERPE - Genesis", "EUTERPE - Low Rider", "EUTERPE - Moonlit Chronicler", "EUTERPE - Morpho", "EUTERPE - Ouroboros", "EUTERPE - Pro Writer", "GLM_4_6 - Default", "KAYRA - Asper", "KAYRA - Carefree", "KAYRA - Fresh Coffee", "KAYRA - Stelenes", "KAYRA - Writer's Daemon", "KRAKE - 20BC+", "KRAKE - Blue Adder", "KRAKE - Blue Lighter", "KRAKE - Calibrated", "KRAKE - Calypso", "KRAKE - Iris", "KRAKE - Krait", "KRAKE - Redjack", "KRAKE - Reverie", "KRAKE - Top Gun Beta", "SIGURD - Best Guess", "SIGURD - Coherent Creativity", "SIGURD - Emperor Moth", "SIGURD - Genji Default", "SIGURD - Luna Moth", "SIGURD - Pleasing Results", "SIGURD - Sphinx Moth", "SIGURD - Storywriter", "SIGURD - Storywriter (Snek)", "XIALONG - Default"}, 
				sortedPresetNames);
	}

	@Test
	void testGetPresetByExtendedName() {
		assertNotNull(TextParameterPresets.getPresetFileByExtendedName("KAYRA - Carefree"));
		assertNotNull(TextParameterPresets.getPresetParametersByExtendedName("KAYRA - Carefree"));
	}

	@Test
	void testGetPresetByNameAndModel() {
		assertNotNull(TextParameterPresets.getPresetFileByNameAndModel(TextGenModel.KRAKE, "Blue Lighter"));
		assertNotNull(TextParameterPresets.getPresetParametersByNameAndModel(TextGenModel.KRAKE, "Blue Lighter"));
	}

	@Test
	void testGetAssociatedPresets() {
		String[] sortedPresetNames = TextParameterPresets.getAssociatedPresetExtendedNames(TextGenModel.KAYRA);
		Arrays.sort(sortedPresetNames);
		
		assertArrayEquals(
				new String[] {"KAYRA - Asper", "KAYRA - Carefree", "KAYRA - Fresh Coffee", "KAYRA - Stelenes", "KAYRA - Writer's Daemon"}, 
				sortedPresetNames);
	}

	@Test
	void testTrimPresetNameForDisplay() {
		assertEquals("Preset Name", TextParameterPresets.trimPresetNameForDisplay("MODEL_NAME - Preset Name"));
	}

}
