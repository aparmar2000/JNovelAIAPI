package main.java.aparmar.nai.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;

public class InternalResourceLoader {
	private static final Hashtable<String, File> resourceCache = new Hashtable<String, File>();
	
	public static File getInternalFile(String filename) throws FileNotFoundException {
		if (resourceCache.contains(filename)) {
			return resourceCache.get(filename);
		}
		URL foundResource = new InternalResourceLoader().getClass().getClassLoader().getResource(filename);
		if (foundResource == null) {
			throw new FileNotFoundException(filename+" was not found!");
		}
		
		try {
			File foundFile = new File(foundResource.toURI());
			resourceCache.put(filename, foundFile);
			return foundFile;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		throw new AssertionError("The resource URL returned by getResource("+filename+") couldn't be converted to a URI.");
	}
}
