package comul01.tonemap;

import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (31/07/2001 18:36:42)
 * @author: 
 */
public class ToneMapStatus implements ToneMapConstants {

	public static StatusInfo[] SI = {
		new StatusInfo(SC_TONEMAP_LOADING, ST_INFO, "ToneMap Loading"),
		new StatusInfo(SC_TONEMAP_LOADED, ST_INFO, "ToneMap Loaded"),
		new StatusInfo(SC_TONEMAP_PROCESSING, ST_INFO, "ToneMap Processing"),
		new StatusInfo(SC_TONEMAP_PROCESSED, ST_INFO, "ToneMap Processed"),
	};
				
	static {
		synchronized(SI) {		
			Arrays.sort(SI);
		}
	}

/**
 * ToneMapStatus constructor comment.
 */
	public ToneMapStatus() {
		super();
	}

	public static StatusInfo getSI(int statusCode) {
		StatusInfo key = new StatusInfo(statusCode);

		int index = Arrays.binarySearch(SI, key) ;

		if(SI[index].sc == statusCode)
			return SI[index];
		else
			return null;
	}

}