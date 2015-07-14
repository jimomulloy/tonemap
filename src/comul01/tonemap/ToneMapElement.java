package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;

/**
  * This is the ToneMapElement class encapsulates Data Element contained within 
  * ToneMapMatrix associated with a ToneMap object.
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class ToneMapElement implements Serializable {

	public ToneMapElement(double amplitude, double FTPower, 
							int index, int timeIndex, int pitchIndex) {
	
		this.preAmplitude = amplitude;
		this.preFTPower = FTPower;
		this.postAmplitude = amplitude;
		this.postFTPower = FTPower;
		this.index = index;
		this.timeIndex = timeIndex;
		this.pitchIndex = pitchIndex;
	}
		
	public double preAmplitude;
	public double preFTPower;
	public double postAmplitude;
	public double postFTPower;
	public int noteState;
	public NoteListElement noteListElement;
	
	private int index;
	private int timeIndex;
	private int pitchIndex;
	
	
} // End ToneMapElement