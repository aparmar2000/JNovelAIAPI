package aparmar.nai.data.request.imagen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import aparmar.nai.data.request.Base64Image;
import aparmar.nai.data.request.imagen.DirectorReferenceParameter.DirectorReferenceCaption;
import lombok.NonNull;

/**
 * Type-safe fluent builder for {@link DirectorReferenceParameter}.
 */
public abstract class FluentDirectorReferenceBuilder<T extends FluentDirectorReferenceBuilder<T>> {
	protected Base64Image referenceImage;
	protected float informationExtracted = 1;
	protected float secondaryStrength = 1;
	protected float strength = 1;
	protected boolean legacyUC = false;
	protected List<String> charCaptions = new ArrayList<>();

	protected FluentDirectorReferenceBuilder() {}

	protected FluentDirectorReferenceBuilder(FluentDirectorReferenceBuilder<?> other) {
		this.referenceImage = other.referenceImage;
		this.informationExtracted = other.informationExtracted;
		this.secondaryStrength = other.secondaryStrength;
		this.strength = other.strength;
		this.legacyUC = other.legacyUC;
		this.charCaptions = new ArrayList<>(other.charCaptions);
	}

	@SuppressWarnings("unchecked")
	protected T self() {
		return (T) this;
	}

	protected abstract DirectorReferenceCaption getCaption();

	public T referenceImage(Base64Image referenceImage) {
		this.referenceImage = referenceImage;
		return self();
	}

	public T informationExtracted(float informationExtracted) {
		this.informationExtracted = informationExtracted;
		return self();
	}

	public T secondaryStrength(float secondaryStrength) {
		this.secondaryStrength = secondaryStrength;
		return self();
	}

	public T strength(float strength) {
		this.strength = strength;
		return self();
	}

	public T legacyUC(boolean legacyUC) {
		this.legacyUC = legacyUC;
		return self();
	}

	public T charCaption(@NonNull String charCaption) {
		this.charCaptions.add(charCaption);
		return self();
	}

	public T charCaptions(@NonNull Collection<? extends String> charCaptions) {
		this.charCaptions.addAll(charCaptions);
		return self();
	}

	public T clearCharCaptions() {
		this.charCaptions.clear();
		return self();
	}

	public DirectorReferenceParameter build() {
		return buildInternal(referenceImage);
	}

	public DirectorReferenceParameter build(@NonNull Base64Image referenceImage) {
		return buildInternal(referenceImage);
	}

	protected DirectorReferenceParameter buildInternal(Base64Image referenceImage) {
		return DirectorReferenceParameter.builder()
				.description(new DirectorReferenceParameter.DirectorReferenceDescription(getCaption(), legacyUC, new ArrayList<>(charCaptions)))
				.referenceImage(referenceImage)
				.informationExtracted(informationExtracted)
				.secondaryStrength(secondaryStrength)
				.strength(strength)
				.build();
	}

	public static NonOutfit.Character character() {
		return new NonOutfit.Character();
	}

	public static NonOutfit.Style style() {
		return new NonOutfit.Style();
	}

	public static Outfit.OutfitOnly outfit() {
		return new Outfit.OutfitOnly();
	}

	public abstract static class NonOutfit<T extends NonOutfit<T>> extends FluentDirectorReferenceBuilder<T> {
		protected NonOutfit() {}
		protected NonOutfit(FluentDirectorReferenceBuilder<?> other) {
			super(other);
		}

		public static class Character extends NonOutfit<Character> {
			protected Character() {}
			protected Character(FluentDirectorReferenceBuilder<?> other) {
				super(other);
			}

			@Override
			protected DirectorReferenceCaption getCaption() {
				return DirectorReferenceCaption.CHARACTER_NOT_STYLE;
			}

			public NonOutfit.CharacterAndStyle withStyle() {
				return new NonOutfit.CharacterAndStyle(this);
			}

			public Outfit.Character withOutfit() {
				return new Outfit.Character(this);
			}
		}

		public static class Style extends NonOutfit<Style> {
			protected Style() {}
			protected Style(FluentDirectorReferenceBuilder<?> other) {
				super(other);
			}

			@Override
			protected DirectorReferenceCaption getCaption() {
				return DirectorReferenceCaption.STYLE_NOT_CHARACTER;
			}

			public NonOutfit.CharacterAndStyle withCharacter() {
				return new NonOutfit.CharacterAndStyle(this);
			}
		}

		public static class CharacterAndStyle extends NonOutfit<CharacterAndStyle> {
			protected CharacterAndStyle() {}
			protected CharacterAndStyle(FluentDirectorReferenceBuilder<?> other) {
				super(other);
			}

			@Override
			protected DirectorReferenceCaption getCaption() {
				return DirectorReferenceCaption.CHARACTER_AND_STYLE_NOT_OUTFIT;
			}

			public NonOutfit.Character withoutStyle() {
				return new NonOutfit.Character(this);
			}

			public NonOutfit.Style withoutCharacter() {
				return new NonOutfit.Style(this);
			}

			public Outfit.CharacterAndStyle withOutfit() {
				return new Outfit.CharacterAndStyle(this);
			}
		}
	}

	public abstract static class Outfit<T extends Outfit<T>> extends FluentDirectorReferenceBuilder<T> {
		protected Outfit() {}
		protected Outfit(FluentDirectorReferenceBuilder<?> other) {
			super(other);
		}

		public static class OutfitOnly extends Outfit<OutfitOnly> {
			protected OutfitOnly() {}
			protected OutfitOnly(FluentDirectorReferenceBuilder<?> other) {
				super(other);
			}

			@Override
			protected DirectorReferenceCaption getCaption() {
				return DirectorReferenceCaption.COSTUME;
			}

			public Outfit.Character withCharacter() {
				return new Outfit.Character(this);
			}
		}

		public static class Character extends Outfit<Character> {
			protected Character() {}
			protected Character(FluentDirectorReferenceBuilder<?> other) {
				super(other);
			}

			@Override
			protected DirectorReferenceCaption getCaption() {
				return DirectorReferenceCaption.CHARACTER_AND_OUTFIT;
			}

			public Outfit.OutfitOnly withoutCharacter() {
				return new Outfit.OutfitOnly(this);
			}

			public Outfit.CharacterAndStyle withStyle() {
				return new Outfit.CharacterAndStyle(this);
			}

			public NonOutfit.Character withoutOutfit() {
				return new NonOutfit.Character(this);
			}
		}

		public static class CharacterAndStyle extends Outfit<CharacterAndStyle> {
			protected CharacterAndStyle() {}
			protected CharacterAndStyle(FluentDirectorReferenceBuilder<?> other) {
				super(other);
			}

			@Override
			protected DirectorReferenceCaption getCaption() {
				return DirectorReferenceCaption.CHARACTER_AND_STYLE;
			}

			public Outfit.Character withoutStyle() {
				return new Outfit.Character(this);
			}

			public NonOutfit.CharacterAndStyle withoutOutfit() {
				return new NonOutfit.CharacterAndStyle(this);
			}
		}
	}
}