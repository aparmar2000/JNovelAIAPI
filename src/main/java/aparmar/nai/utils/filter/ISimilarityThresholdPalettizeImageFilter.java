package aparmar.nai.utils.filter;

public interface ISimilarityThresholdPalettizeImageFilter extends IPalettizeImageFilter {
	float getSimilarityThreshold();

	void setSimilarityThreshold(float similarityThreshold);
}
