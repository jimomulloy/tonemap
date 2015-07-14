package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;


/**
  * This is the NoteStatusElement 
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class NoteStatusElement {

	public NoteStatusElement(int note ) {
	
		this.note = note;
	}
		
	public int note;
	public int state;
	public boolean highFlag; 
	public double onTime;
	public double offTime;
	public int onIndex;
	public int offIndex;
	
} // End NoteStatusElement