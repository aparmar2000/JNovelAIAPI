package main.java.aparmar.nai.data.response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import lombok.Data;

@Data
public class AudioWrapper {
	private final byte[] bytes;
	private final AudioWrapperFormat audioWrapperFormat;
	
	public enum AudioWrapperFormat {
		MP3,
		WEBA;
	}
	
	public File writeToFile(File outFile) throws FileNotFoundException, IOException {
		try (final FileOutputStream fOut = new FileOutputStream(outFile)) {
			fOut.write(bytes);
		}
		
		return outFile;
	}
}
