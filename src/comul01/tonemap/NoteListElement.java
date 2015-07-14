package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;


/**
  * This is the NoteListElement class 
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class NoteListElement {

	public NoteListElement ( int note,
							int pitchIndex,
							double startTime,
							double endTime,
							int startTimeIndex,
							int endTimeIndex,
							double avgFTPower,
							double maxFTPower,
							double minFTPower,
							double avgAmp,
							double maxAmp,
							double minAmp,
							double percentMin ) {
		this.note = note;
		this.pitchIndex = pitchIndex;
		this.startTime = startTime;
		this.endTime = endTime;
		this.startTimeIndex = startTimeIndex;
		this.endTimeIndex = endTimeIndex;
		this.avgFTPower = avgFTPower;
		this.maxFTPower = maxFTPower;
		this.minFTPower = minFTPower;
		this.avgAmp = avgAmp;
		this.maxAmp = maxAmp;
		this.minAmp = minAmp;
		this.percentMin = percentMin; 
	}
							
	public int note;
	public int pitchIndex;
	public double startTime;
	public double endTime;
	public int startTimeIndex;
	public int endTimeIndex;
	public double avgFTPower;
	public double maxFTPower;
	public double minFTPower;
	public double avgAmp;
	public double maxAmp;
	public double minAmp;
	public double percentMin;
	public boolean underTone;
	public boolean overTone;
	
	
} // End NoteListElement