package aparmar.nai.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import aparmar.nai.data.request.TextGenModel;

class IntegrationTestTextParameterPresets {

	@Test
	void testGetPresetNames() {
		assertArrayEquals(
				new String[] {"EUTERPE - Ouroboros","KAYRA - Asper","EUTERPE - Fandango","KRAKE - Krait","KAYRA - Writer's Daemon","CLIO - Flat-Out","KRAKE - Calibrated","KAYRA - Green Active Writer","EUTERPE - Genesis","EUTERPE - Low Rider","CLIO - Blended Coffee","KAYRA - Fresh Coffee","KRAKE - Blue Lighter","CLIO - Talker C","KRAKE - Reverie","KAYRA - Tesseract","CLIO - Vingt-Un","CLIO - Long Press","CLIO - Fresh Coffee","KRAKE - 20BC+","EUTERPE - Pro Writer","CLIO - Edgewise","EUTERPE - Moonlit Chronicler","KAYRA - Blended Coffee","KAYRA - Plotfish","KAYRA - Blook","KRAKE - Top Gun Beta","EUTERPE - All-Nighter","KRAKE - Iris","CLIO - Edgewise CFG","KRAKE - Calypso","EUTERPE - Basic Coherence","EUTERPE - Morpho","KAYRA - Stelenes","KRAKE - Redjack","KRAKE - Blue Adder","KAYRA - Carefree","EUTERPE - Ace of Spades"}, 
				TextParameterPresets.getPresetNames());
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
