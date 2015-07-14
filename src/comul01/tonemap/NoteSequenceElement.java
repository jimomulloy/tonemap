package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;


/**
  * This is the NoteSequenceElement class 
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class NoteSequenceElement implements Comparable {

	public NoteSequenceElement ( int note,
							int state,
							long tick,
							int velocity ) {
		this.note = note;
		this.state = state;
		this.tick = tick;
		this.velocity = velocity;
	}

	public int compareTo(Object o){
		
		long otick = ((NoteSequenceElement)o).tick;
		return (tick < otick ? -1 : (tick == otick ? 0 : 1 ));
	}
	
	public long tick;					
	public int note;
	public int state;
	public int velocity;
	
} // End NoteSequenceElement