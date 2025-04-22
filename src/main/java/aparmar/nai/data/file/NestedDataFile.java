package aparmar.nai.data.file;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Delegate;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class NestedDataFile<T extends DataFile<T>, D extends DataFile<D>> extends DataFile<T> implements List<D> {
	
	@Delegate
	protected final List<D> dataFiles = new ArrayList<>();

	public NestedDataFile(Path filePath) {
		super(filePath);
	}

	public NestedDataFile(Path filePath, List<D> dataFiles) {
		super(filePath);
		this.dataFiles.addAll(dataFiles);
	}
	
	public ImmutableList<D> getDataFiles() {
		return dataFiles.stream().collect(ImmutableList.toImmutableList());
	}

}
