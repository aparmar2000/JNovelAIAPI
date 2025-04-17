package aparmar.nai.data.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.gson.Gson;

import aparmar.nai.utils.GsonProvider;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class DataFile<T extends DataFile<T>> {
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	protected transient final Gson gson = GsonProvider.buildGsonInstance();
	
	@Getter
	@Nullable
	@EqualsAndHashCode.Include
	private transient final Path filePath;
	@Getter
	@EqualsAndHashCode.Exclude
	private transient boolean changed = false;
	
	protected void markChanged() {
		changed = true;
	}
	
	public abstract Pattern getFileExtPattern();
	protected abstract void innerSave() throws IOException;
	public void save() throws IOException {
		if (filePath == null) { throw new UnsupportedOperationException("This DataFile has no path to save to."); }
		innerSave();
		changed = false;
	}
	public void saveIfChanged() throws IOException {
		if (!changed) { return; }
		save();
	}
	public T saveToFile(File file) throws IOException {
		T clone = cloneWithNewPath(file.toPath());
		clone.save();
		return clone;
	}
	
	protected abstract T innerLoad() throws IOException;
	public T load() throws IOException {
		if (filePath == null) { throw new UnsupportedOperationException("This DataFile has no path to save to."); }
		changed = false;
		return innerLoad();
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
