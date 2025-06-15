package aparmar.nai.utils;

import java.util.Iterator;
import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChangeReportingWrappedIterator<I extends Iterator<T>, T> implements Iterator<T> {
	protected final I wrappedIterator;
	protected final Runnable changeFunc;

	@Override
	public boolean hasNext() {
		return wrappedIterator.hasNext();
	}

	@Override
	public T next() {
		return wrappedIterator.next();
	}

	@Override
	public void remove() {
		wrappedIterator.remove();
		changeFunc.run();
	}

	@Override
	public void forEachRemaining(Consumer<? super T> action) {
		wrappedIterator.forEachRemaining(action);
	}
	
	
}
