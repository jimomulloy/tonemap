package comul01.tonemap;

import java.util.Vector;
import java.util.Enumeration;


/**
  * This is a class that encapsulates a set of Overtone control data for a ToneMap 
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class OvertoneSet {
	
	public static double[] GUITAR_HARMONICS = { 0.1, 0.05, 0.02, 0.01 };
	public static double[][] GUITAR_FORMANTS = {{0, 1.0},
												{1000,1.2},
												{2000,1.0}};
							
	
	static {
		
	}
	
	public OvertoneSet() {
		harmonics = GUITAR_HARMONICS;
		formants = GUITAR_FORMANTS;			
		
	}
	
	public double[] getHarmonics () {
		return harmonics;
	}
	
	public double[][] getFormants () {
		return formants;
	}

	public void setHarmonics (double[] harmonics) {
		this.harmonics = harmonics;
	}
	
	public void setFormants (double[][] formants) {
		this.formants = formants;
	}
	
	private double[] harmonics;
	private double[][] formants;


} // End OvertoneSet