package aparmar.nai.utils.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.ImmutableList;

import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SequentialImageFilter implements IImageFilter {
	private final ArrayList<IImageFilter> filters = new ArrayList<>();
	
	public SequentialImageFilter(IImageFilter... filters) {
		this(Arrays.asList(filters));
	}
	public SequentialImageFilter(Collection<IImageFilter> filters) {
		this.filters.addAll(filters);
	}
	
	public ImmutableList<IImageFilter> getFilters() {
		return ImmutableList.copyOf(filters);
	}

	@Override
	public Planar<GrayF32> applyPlanar(Planar<GrayF32> rgbaIn) {
		Planar<GrayF32> buffer = rgbaIn;
		for (IImageFilter filter : filters) {
			buffer = filter.applyPlanar(buffer);
		}
		return buffer;
	}

	@Override
	public IImageFilter andThen(IImageFilter next) {
		SequentialImageFilter newSequential = new SequentialImageFilter(filters);
		newSequential.filters.add(next);
		return newSequential;
	}

}
