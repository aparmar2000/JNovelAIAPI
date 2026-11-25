package aparmar.nai.data.request;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import aparmar.nai.data.request.imagen.DirectorReferenceParameter;
import aparmar.nai.data.request.imagen.DirectorReferenceParameter.DirectorReferenceCaption;
import aparmar.nai.data.request.imagen.FluentDirectorReferenceBuilder;

class UnitTestDirectorReferenceParameter {

	@Test
	void testFluentBuilderEntryPointsAndAllValidTransitions() {
		// Entry points
		assertEquals(
				DirectorReferenceCaption.CHARACTER_NOT_STYLE,
				FluentDirectorReferenceBuilder.character().build().getDescription().getCaption().getBaseCaption());
		assertEquals(
				DirectorReferenceCaption.STYLE_NOT_CHARACTER,
				FluentDirectorReferenceBuilder.style().build().getDescription().getCaption().getBaseCaption());
		assertEquals(
				DirectorReferenceCaption.COSTUME,
				FluentDirectorReferenceBuilder.outfit().build().getDescription().getCaption().getBaseCaption());

		// NonOutfit transitions
		assertEquals(
				DirectorReferenceCaption.CHARACTER_AND_STYLE_NOT_OUTFIT,
				FluentDirectorReferenceBuilder.character().withStyle().build().getDescription().getCaption().getBaseCaption());
		assertEquals(
				DirectorReferenceCaption.CHARACTER_AND_STYLE_NOT_OUTFIT,
				FluentDirectorReferenceBuilder.style().withCharacter().build().getDescription().getCaption().getBaseCaption());
		assertEquals(
				DirectorReferenceCaption.CHARACTER_NOT_STYLE,
				FluentDirectorReferenceBuilder.character().withStyle().withoutStyle().build().getDescription().getCaption().getBaseCaption());
		assertEquals(
				DirectorReferenceCaption.STYLE_NOT_CHARACTER,
				FluentDirectorReferenceBuilder.character().withStyle().withoutCharacter().build().getDescription().getCaption().getBaseCaption());

		// Outfit transitions
		assertEquals(
				DirectorReferenceCaption.CHARACTER_AND_OUTFIT,
				FluentDirectorReferenceBuilder.character().withOutfit().build().getDescription().getCaption().getBaseCaption());
		assertEquals(
				DirectorReferenceCaption.CHARACTER_AND_OUTFIT,
				FluentDirectorReferenceBuilder.outfit().withCharacter().build().getDescription().getCaption().getBaseCaption());
		assertEquals(
				DirectorReferenceCaption.COSTUME,
				FluentDirectorReferenceBuilder.outfit().withCharacter().withoutCharacter().build().getDescription().getCaption().getBaseCaption());
		assertEquals(
				DirectorReferenceCaption.CHARACTER_NOT_STYLE,
				FluentDirectorReferenceBuilder.outfit().withCharacter().withoutOutfit().build().getDescription().getCaption().getBaseCaption());
		assertEquals(
				DirectorReferenceCaption.CHARACTER_AND_STYLE,
				FluentDirectorReferenceBuilder.character().withOutfit().withStyle().build().getDescription().getCaption().getBaseCaption());
		assertEquals(
				DirectorReferenceCaption.CHARACTER_AND_STYLE,
				FluentDirectorReferenceBuilder.character().withStyle().withOutfit().build().getDescription().getCaption().getBaseCaption());
		assertEquals(
				DirectorReferenceCaption.CHARACTER_AND_OUTFIT,
				FluentDirectorReferenceBuilder.character().withOutfit().withStyle().withoutStyle().build().getDescription().getCaption().getBaseCaption());
		assertEquals(
				DirectorReferenceCaption.CHARACTER_AND_STYLE_NOT_OUTFIT,
				FluentDirectorReferenceBuilder.character().withOutfit().withStyle().withoutOutfit().build().getDescription().getCaption().getBaseCaption());
	}

	@Test
	void testFluentBuilderPreservesFieldsAcrossTransitionsAndBuilds() {
		Base64Image dummyImage = new Base64Image(new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB));

		DirectorReferenceParameter param = FluentDirectorReferenceBuilder.character()
				.strength(0.5f)
				.secondaryStrength(0.25f)
				.informationExtracted(0.75f)
				.legacyUC(true)
				.charCaption("girl")
				.charCaptions(Arrays.asList("blue eyes"))
				.withOutfit()
				.withStyle()
				.withoutOutfit()
				.build(dummyImage);

		assertEquals(DirectorReferenceCaption.CHARACTER_AND_STYLE_NOT_OUTFIT, param.getDescription().getCaption().getBaseCaption());
		assertEquals(dummyImage, param.getReferenceImage());
		assertEquals(0.5f, param.getStrength());
		assertEquals(0.25f, param.getSecondaryStrength());
		assertEquals(0.75f, param.getInformationExtracted());
		assertTrue(param.getDescription().isLegacyUC());
		assertEquals(Arrays.asList("girl", "blue eyes"), param.getDescription().getCaption().getCharCaptions());
	}

	@Test
	void testFluentBuilderDefaultsAndClearCharCaptions() {
		DirectorReferenceParameter param = FluentDirectorReferenceBuilder.style()
				.charCaption("temp")
				.clearCharCaptions()
				.build();

		assertNull(param.getReferenceImage());
		assertEquals(1.0f, param.getStrength());
		assertEquals(1.0f, param.getSecondaryStrength());
		assertEquals(1.0f, param.getInformationExtracted());
		assertTrue(param.getDescription().getCaption().getCharCaptions().isEmpty());
	}
}
