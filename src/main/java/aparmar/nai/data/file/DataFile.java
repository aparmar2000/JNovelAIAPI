package aparmar.nai.data.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import com.google.gson.Gson;

import aparmar.nai.utils.GsonProvider;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.val;

@EqualsAndHashCode
@ToString
public abstract class DataFile<T extends DataFile<? extends T>> {
	protected static transient final Gson gson = GsonProvider.buildGsonInstance();
	
	@Getter
	@Nullable
	@EqualsAndHashCode.Include
	private transient final Path filePath;
	@EqualsAndHashCode.Exclude
	private transient AtomicBoolean changed = new AtomicBoolean(false);
	
	public DataFile(@Nullable Path filePath) {
		if (filePath != null) {
			val pathFile = filePath.toFile();
			if (pathFile.isDirectory()) {
				throw new IllegalArgumentException("Path points to a directory!");
			}
		}
		this.filePath = filePath;
	}
	
	protected boolean isChanged() {
		return changed.get();
	}
	protected void markChanged() {
		changed.set(true);
	}
	protected void resetChanged() {
		changed.set(false);
	}
	
	public abstract String getFileExt();
	public abstract void saveToStream(OutputStream outputStream) throws IOException;
	public void save() throws IOException {
		if (filePath == null) { throw new UnsupportedOperationException("This DataFile has no path to save to."); }
		try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
			saveToStream(fileOut);
		}
		resetChanged();
	}
	public void saveIfChanged() throws IOException {
		if (!isChanged()) { return; }
		save();
	}
	public T saveToFile(File file) throws IOException {
		T clone = cloneWithNewPath(file.toPath());
		clone.save();
		return clone;
	}
	
	public abstract T loadFromStream(InputStream inputStream) throws IOException;
	public T load() throws IOException {
		if (filePath == null) { throw new UnsupportedOperationException("This DataFile has no path to load from."); }
		T result;
		try (FileInputStream fileOut = new FileInputStream(filePath.toFile())) {
			result = loadFromStream(fileOut);
		}
		resetChanged();
		return result;
	}

	protected abstract T innerCloneWithNewPath(@Nullable Path path);
	public T cloneWithNewPath(@Nullable Path path) {
		if (path != null) {
			val pathFile = path.toFile();
			if (pathFile.isDirectory()) {
				throw new IllegalArgumentException("Path points to a directory!");
			}
		}
		return innerCloneWithNewPath(path);
	}
}
