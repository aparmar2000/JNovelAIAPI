package aparmar.nai.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class InternalResourceLoader {
	public static InputStream getInternalResourceAsStream(String filename) throws FileNotFoundException {
		InputStream foundResourceStream = new InternalResourceLoader()
				.getClass().getClassLoader()
				.getResourceAsStream(filename);
		if (foundResourceStream == null) {
			throw new FileNotFoundException(filename+" was not found!");
		}
		
		return foundResourceStream;
	}
}
