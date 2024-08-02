package aparmar.nai.data.response;

import java.io.IOException;

import javax.imageio.IIOImage;

import aparmar.nai.utils.ZipArchiveWrapper;

public class ImageSetWrapperRemoveBackground extends ImageSetWrapper {
	private static final String MASKED_NAME = "image_0.png";
	private static final String GENERATED_NAME = "image_1.png";
	private static final String BLEND_NAME = "image_2.png";
	
	protected final int maskedIndex, generatedIndex, blendIndex;

	public ImageSetWrapperRemoveBackground(ZipArchiveWrapper zipArchive) throws IOException {
		super(zipArchive);
		
		int loadedMaskedIndex = -1;
		int loadedGeneratedIndex = -1;
		int loadedBlendIndex = -1;
		if (zipArchive.getEntryCount() != 3) { throw new IOException("Expected 3 images, but found "+zipArchive.getEntryCount()); }
		for (int i=0;i<zipArchive.getEntryCount();i++) {
			if (zipArchive.getEntry(i).getName().equals(MASKED_NAME)) {
				loadedMaskedIndex = i;
			}
			if (zipArchive.getEntry(i).getName().equals(GENERATED_NAME)) {
				loadedGeneratedIndex = i;
			}
			if (zipArchive.getEntry(i).getName().equals(BLEND_NAME)) {
				loadedBlendIndex = i;
			}
		}
		
		if (loadedMaskedIndex == -1) { throw new IOException("'Masked' result image not found"); }
		if (loadedGeneratedIndex == -1) { throw new IOException("'Generated' result image not found"); }
		if (loadedBlendIndex == -1) { throw new IOException("'Blend' result image not found"); }
		maskedIndex = loadedMaskedIndex;
		generatedIndex = loadedGeneratedIndex;
		blendIndex = loadedBlendIndex;
	}
	
	public IIOImage getMaskedImage() throws IOException { return this.getImage(maskedIndex); }
	public IIOImage getGeneratedImage() throws IOException { return this.getImage(generatedIndex); }
	public IIOImage getBlendImage() throws IOException { return this.getImage(blendIndex); }

}
