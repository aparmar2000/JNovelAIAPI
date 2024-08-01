package aparmar.nai.data.response;

import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;

import aparmar.nai.utils.ZipArchiveWrapper;

public class SingleImageWrapper extends ImageSetWrapper {

	public SingleImageWrapper(ZipArchiveWrapper zipArchive) throws IOException {
		super(zipArchive);
	}
	
	public IIOImage getImage() throws IOException {
		return super.getImage(0);
	}
	
	public void writeImageToFile(File outputFile) throws IOException {
		super.writeImageToFile(0, outputFile);
	}

}
