package aparmar.nai.utils.filter;

public interface IPalettizeImageFilter extends IImageFilter {
	boolean isUseOklabColorSpace();

	void setUseOklabColorSpace(boolean useOklabColorSpace);
}
