package aparmar.nai.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChangeReportingWrappedList<L extends List<T>, T> implements List<T> {
	protected final L wrappedlist;
	protected final Runnable changeFunc;

	@Override
	public int size() {
		return wrappedlist.size();
	}

	@Override
	public boolean isEmpty() {
		return wrappedlist.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return wrappedlist.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return new ChangeReportingWrappedIterator<>(wrappedlist.iterator(), changeFunc);
	}

	@Override
	public Object[] toArray() {
		return wrappedlist.toArray();
	}

	@Override
	public <T1> T1[] toArray(T1[] a) {
		return wrappedlist.toArray(a);
	}

	@Override
	public boolean add(T d) {
		boolean changed = wrappedlist.add(d);
		if (changed) {
			changeFunc.run();
		}
		return changed;
	}

	@Override
	public boolean remove(Object o) {
		boolean changed = wrappedlist.remove(o);
		if (changed) {
			changeFunc.run();
		}
		return changed;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return wrappedlist.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean changed = wrappedlist.addAll(c);
		if (changed) {
			changeFunc.run();
		}
		return changed;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		boolean changed = wrappedlist.addAll(index, c);
		if (changed) {
			changeFunc.run();
		}
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = wrappedlist.removeAll(c);
		if (changed) {
			changeFunc.run();
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = wrappedlist.retainAll(c);
		if (changed) {
			changeFunc.run();
		}
		return changed;
	}

	@Override
	public void clear() {
		if (!wrappedlist.isEmpty()) {
			wrappedlist.clear();
			changeFunc.run();
		}
	}

	@Override
	public T get(int index) {
		return wrappedlist.get(index);
	}

	@Override
	public T set(int index, T element) {
		T previous = wrappedlist.set(index, element);
		changeFunc.run();
		return previous;
	}

	@Override
	public void add(int index, T element) {
		wrappedlist.add(index, element);
		changeFunc.run();
	}

	@Override
	public T remove(int index) {
		T removed = wrappedlist.remove(index);
		changeFunc.run();
		return removed;
	}

	@Override
	public int indexOf(Object o) {
		return wrappedlist.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return wrappedlist.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ChangeReportingWrappedListIterator<>(wrappedlist.listIterator(), changeFunc);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new ChangeReportingWrappedListIterator<>(wrappedlist.listIterator(index), changeFunc);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return new ChangeReportingWrappedList<>(wrappedlist.subList(fromIndex, toIndex), changeFunc);
	}
}
