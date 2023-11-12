package aparmar.nai.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InternalResourceLoader {
	public static InputStream getInternalFile(String filename) throws FileNotFoundException {
		InputStream foundResourceStream = new InternalResourceLoader()
				.getClass().getClassLoader()
				.getResourceAsStream(filename);
		if (foundResourceStream == null) {
			throw new FileNotFoundException(filename+" was not found!");
		}
		
		return foundResourceStream;
	}
	
	public static Path getInternalFilePath(String filename) throws FileNotFoundException {
		URL foundResource = new InternalResourceLoader()
				.getClass().getClassLoader()
				.getResource(filename);
		if (foundResource == null) {
			throw new FileNotFoundException(filename+" was not found!");
		}
		
		try {
			return Paths.get(foundResource.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		throw new AssertionError("The resource URL returned by getResource("+filename+") couldn't be converted to a URI.");
	}
}
