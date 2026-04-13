package aparmar.nai.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import lombok.val;

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
	public static InputStream getInternalResourceAsStream(Path path) throws FileNotFoundException {
		Path adjPath = path;
		if (path.isAbsolute()) {
			Path rootPath = null;
			try {
				val rootUri = new InternalResourceLoader().getClass().getResource("/").toURI();
			    
			    if (rootUri.getScheme().equals("jar")) {
			        try (FileSystem fs = FileSystems.newFileSystem(rootUri, Collections.emptyMap())) {
			        	rootPath = fs.getPath("/");
			        }
			    } else {
			    	rootPath = Paths.get(rootUri);
			    }
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
			if (rootPath.getParent() != null) {
				rootPath = rootPath.getParent();
			}
			adjPath = rootPath.relativize(adjPath);
			adjPath = adjPath.subpath(1, adjPath.getNameCount());
		}
		return getInternalResourceAsStream("\\"+adjPath.toString());
	}
	
	public static void walkStreamInternalResourceFolderContents(String path, Consumer<Stream<Path>> action, int maxDepth) throws IOException {
		URL resourceUrl = new InternalResourceLoader().getClass().getResource(path);
		if (resourceUrl == null) {
			throw new FileNotFoundException(path+" was not found!");
		}
	    URI uri;
		try {
			uri = resourceUrl.toURI();
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
		
		UnaryOperator<Path> relativizer = UnaryOperator.identity();
	    
	    if (uri.getScheme().equals("jar")) {
	        try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
	            Path resourcePath = fs.getPath(path);
	            try (Stream<Path> walk = Files.walk(resourcePath, maxDepth)) {
	            	action.accept(walk
	            			.filter(p->!p.equals(resourcePath))
	            			.map(relativizer));
	            }
	        }
	    } else {
	        Path resourcePath = Paths.get(uri);
	        try (Stream<Path> walk = Files.walk(resourcePath, maxDepth)) {
            	action.accept(walk
            			.filter(p->!p.equals(resourcePath))
            			.map(relativizer));
	        }
	    }
	}
	public static void walkStreamInternalResourceFolderContents(String path, Consumer<Stream<Path>> action) throws IOException {
		walkStreamInternalResourceFolderContents(path, action, 1);
	}
	public static void walkInternalResourceFolderContents(String path, Consumer<Path> action, int maxDepth) throws IOException {
		walkStreamInternalResourceFolderContents(path, s->s.forEach(action), maxDepth);
	}
	public static void walkInternalResourceFolderContents(String path, Consumer<Path> action) throws IOException {
		walkInternalResourceFolderContents(path, action, 1);
	}
	
	public static List<Path> getInternalResourceFolderContentsAsList(String path) throws IOException {
		ArrayList<Path> result = new ArrayList<>();
		
		walkInternalResourceFolderContents(path, result::add);
		
		return result;
	}
}
