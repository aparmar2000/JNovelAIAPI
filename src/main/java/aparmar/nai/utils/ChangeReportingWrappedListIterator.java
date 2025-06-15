package aparmar.nai.utils;

import java.util.ListIterator;

public class ChangeReportingWrappedListIterator<I extends ListIterator<T>, T> extends ChangeReportingWrappedIterator<I, T> implements ListIterator<T> {

	public ChangeReportingWrappedListIterator(I wrappedIterator, Runnable changeFunc) {
		super(wrappedIterator, changeFunc);
	}

	@Override
	public boolean hasPrevious() {
		return wrappedIterator.hasPrevious();
	}

	@Override
	public T previous() {
		return wrappedIterator.previous();
	}

	@Override
	public int nextIndex() {
		return wrappedIterator.nextIndex();
	}

	@Override
	public int previousIndex() {
		return wrappedIterator.previousIndex();
	}

	@Override
	public void set(T e) {
		wrappedIterator.set(e);
		changeFunc.run();
	}

	@Override
	public void add(T e) {
		wrappedIterator.add(e);
		changeFunc.run();
	}
	
	
}
