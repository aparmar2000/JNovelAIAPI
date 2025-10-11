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
		String[] sortedPresetNames = TextParameterPresets.getPresetNames();
		Arrays.sort(sortedPresetNames);
		
		assertArrayEquals(
				new String[] {"CLIO - Blended Coffee","CLIO - Edgewise","CLIO - Edgewise CFG","CLIO - Flat-Out","CLIO - Fresh Coffee","CLIO - Long Press","CLIO - Talker C","CLIO - Vingt-Un","EUTERPE - Ace of Spades","EUTERPE - All-Nighter","EUTERPE - Basic Coherence","EUTERPE - Fandango","EUTERPE - Genesis","EUTERPE - Low Rider","EUTERPE - Moonlit Chronicler","EUTERPE - Morpho","EUTERPE - Ouroboros","EUTERPE - Pro Writer","GLM_4_6 - Default","KAYRA - Asper","KAYRA - Blended Coffee","KAYRA - Blook","KAYRA - Carefree","KAYRA - Fresh Coffee","KAYRA - Green Active Writer","KAYRA - Plotfish","KAYRA - Stelenes","KAYRA - Tesseract","KAYRA - Writer's Daemon","KRAKE - 20BC+","KRAKE - Blue Adder","KRAKE - Blue Lighter","KRAKE - Calibrated","KRAKE - Calypso","KRAKE - Iris","KRAKE - Krait","KRAKE - Redjack","KRAKE - Reverie","KRAKE - Top Gun Beta","SIGURD - Best Guess","SIGURD - Coherent Creativity","SIGURD - Emperor Moth","SIGURD - Luna Moth","SIGURD - Pleasing Results","SIGURD - Sphinx Moth","SIGURD - Storywriter"}, 
				sortedPresetNames);
	}

	@Test
	void testGetPresetByExtendedName() {
		assertNotNull(TextParameterPresets.getPresetByExtendedName("KAYRA - Carefree"));
	}

	@Test
	void testGetPresetByNameAndModel() {
		assertNotNull(TextParameterPresets.getPresetByNameAndModel(TextGenModel.KRAKE, "Blue Lighter"));
	}

	@Test
	void testGetAssociatedPresets() {
		assertArrayEquals(
				new String[] {"KAYRA - Carefree","KAYRA - Stelenes","KAYRA - Fresh Coffee","KAYRA - Asper","KAYRA - Writer's Daemon","KAYRA - Blook","KAYRA - Tesseract","KAYRA - Blended Coffee","KAYRA - Plotfish","KAYRA - Green Active Writer"}, 
				TextParameterPresets.getAssociatedPresets(TextGenModel.KAYRA));
	}

	@Test
	void testTrimPresetNameForDisplay() {
		assertEquals("Preset Name", TextParameterPresets.trimPresetNameForDisplay("MODEL_NAME - Preset Name"));
	}

}
