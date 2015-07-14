package comul01.tonemap;

import java.io.*;


/**
  * This is the ToneMapSerial class
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class ToneMapSerial implements Serializable {
	
	public ToneMapSerial(ToneMapMatrix matrix, 
							TimeSet timeSet,
							PitchSet pitchSet) {
		this.matrix = matrix;
		this.timeSet = timeSet;
		this.pitchSet = pitchSet;
	}
		
	ToneMapMatrix matrix;
	TimeSet timeSet;
	PitchSet pitchSet;
			
}