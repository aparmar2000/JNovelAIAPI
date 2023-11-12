package aparmar.nai.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Optional;

import com.google.gson.Gson;

import lombok.Data;

public class InternalResourceLoader {
	public static final File CACHE_FOLDER = new File("./cache");
	public static final File CACHE_MANIFEST = CACHE_FOLDER.toPath()
			.resolve("manifest.json")
			.toFile();
	
	private static final Gson gson = new Gson();
	private static final Hashtable<String, File> resourceCache = new Hashtable<String, File>();
	
	public static InputStream getInternalResourceAsStream(String filename) throws FileNotFoundException {
		InputStream foundResourceStream = new InternalResourceLoader()
				.getClass().getClassLoader()
				.getResourceAsStream(filename);
		if (foundResourceStream == null) {
			throw new FileNotFoundException(filename+" was not found!");
		}
		
		return foundResourceStream;
	}
	
	public static File getInternalFile(String filename) throws FileNotFoundException {
		if (resourceCache.contains(filename)) {
			return resourceCache.get(filename);
		}
		URL foundResource = new InternalResourceLoader().getClass().getClassLoader().getResource(filename);
		if (foundResource == null) {
			throw new FileNotFoundException(filename+" was not found!");
		}
		
		File foundFile = new File(foundResource.toExternalForm());
		resourceCache.put(filename, foundFile);
		return foundFile;
		
//		throw new AssertionError("The resource URL returned by getResource("+filename+") couldn't be converted to a URI.");
	}
	
//	public static File getInternalResourceTempFile(String filename) throws IOException {
//		if (CACHE_FOLDER.isDirectory()) {
//			ManifestEntry[] manifestEntries = null;
//			try (FileReader in = new FileReader(CACHE_MANIFEST)) {
//				manifestEntries = gson.fromJson(in, ManifestEntry[].class);
//			}
//			
//			Optional<File> cachedFile = Arrays.stream(CACHE_FOLDER.listFiles())
//					.filter(f->f.getName().equalsIgnoreCase(filename))
//					.findAny();
//			if (cachedFile.isPresent()) { return cachedFile.get(); }
//		} else {
//			CACHE_FOLDER.mkdir();
//			CACHE_MANIFEST.createNewFile();
//		}
//		
//		InputStream resourceStream = getInternalResourceAsStream(filename);
//		
//		throw new AssertionError("The resource URL returned by getResource("+filename+") couldn't be converted to a URI.");
//	}
}
