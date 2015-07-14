package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.util.*;


/**
  * This is the NoteSequence class encapsulates list of NoteSequenceElements
  *	
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class NoteSequence {

	public NoteSequenceElement get(int index) {
					
		this.index = index;
		return (NoteSequenceElement)noteSequence.get(index);
		
	}
	
	public void add(NoteSequenceElement element) {
	
		noteSequence.add(element);
		
	}
	
	public int size() {
	
		return noteSequence.size();
	
	}

	public void sort() {
	
		Collections.sort(noteSequence);
	
	}
	
	
	private ArrayList noteSequence = new ArrayList();
	private NoteSequenceElement element;
	private int index;
	
} // End NoteSequence