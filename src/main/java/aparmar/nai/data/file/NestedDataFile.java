package aparmar.nai.data.file;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import aparmar.nai.utils.ChangeReportingWrappedList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class NestedDataFile<T extends DataFile<T>, D extends DataFile<?>> extends DataFile<T> implements List<D> {
	
	protected final ChangeReportingWrappedList<List<D>, D> dataFiles = new ChangeReportingWrappedList<>(new ArrayList<>(), this::markChanged);

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
	
	@Override
	public boolean isChanged() {
		return super.isChanged() || dataFiles.stream().anyMatch(DataFile::isChanged);
	}
	
	// === List Interface Methods

	@Override
	public int size() {
		return dataFiles.size();
	}

	@Override
	public boolean isEmpty() {
		return dataFiles.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return dataFiles.contains(o);
	}

	@Override
	public java.util.Iterator<D> iterator() {
		return dataFiles.iterator();
	}

	@Override
	public Object[] toArray() {
		return dataFiles.toArray();
	}

	@Override
	public <T1> T1[] toArray(T1[] a) {
		return dataFiles.toArray(a);
	}

	@Override
	public boolean add(D e) {
		return dataFiles.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return dataFiles.remove(o);
	}

	@Override
	public boolean containsAll(java.util.Collection<?> c) {
		return dataFiles.containsAll(c);
	}

	@Override
	public boolean addAll(java.util.Collection<? extends D> c) {
		return dataFiles.addAll(c);
	}

	@Override
	public boolean addAll(int index, java.util.Collection<? extends D> c) {
		return dataFiles.addAll(index, c);
	}

	@Override
	public boolean removeAll(java.util.Collection<?> c) {
		return dataFiles.removeAll(c);
	}

	@Override
	public boolean retainAll(java.util.Collection<?> c) {
		return dataFiles.retainAll(c);
	}

	@Override
	public void clear() {
		dataFiles.clear();
	}

	@Override
	public D get(int index) {
		return dataFiles.get(index);
	}

	@Override
	public D set(int index, D element) {
		return dataFiles.set(index, element);
	}

	@Override
	public void add(int index, D element) {
		dataFiles.add(index, element);
	}

	@Override
	public D remove(int index) {
		return dataFiles.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return dataFiles.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return dataFiles.lastIndexOf(o);
	}

	@Override
	public java.util.ListIterator<D> listIterator() {
		return dataFiles.listIterator();
	}

	@Override
	public java.util.ListIterator<D> listIterator(int index) {
		return dataFiles.listIterator(index);
	}

	@Override
	public List<D> subList(int fromIndex, int toIndex) {
		return dataFiles.subList(fromIndex, toIndex);
	}
}
