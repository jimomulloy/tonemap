package comul01.tonemap;

/**
 *  WaveletJNI analysis class
 */

public final class WaveletJNI {

	public native void waveletConvert(	double[] wave,
					double[] s,
	   	 			double[] freq,
	   	 			double dt,
	   	 			double pFactor,
	   	 			double tFactor,   
	   				int	 skip,
	   				int	 mm,
	   				int	 nn, 
	   				ProgressListener progressListener);
}