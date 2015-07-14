package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
  * This is the NoteStatus class encapsulates Note state control values
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class NoteStatus {

	public NoteStatus(PitchSet pitchSet){
	
		noteStatus = new NoteStatusElement[pitchSet.getRange()];
		
		lowNote = pitchSet.getLowNote();
		highNote = pitchSet.getHighNote();
		
		for (note = lowNote; note <= highNote; note++) { 
			noteStatus[note - lowNote] = new NoteStatusElement(note); 
		}
	
	}

	public NoteStatusElement get() {
					
		return noteStatus[index];
		
	}
		
	public NoteStatusElement getNote(int note) {
		//System.out.println("getNote: " + note + ", " + lowNote + ", " + highNote);
		if (note <= highNote && note >= lowNote) {
			index = note - lowNote;
		}
		
		return noteStatus[index];
	
	}
		
	public int size() {
	
		return noteStatus.length;
	
	}

	public NoteStatusElement next() {
		if (index < size() ) {
			index++;
			return noteStatus[index];
		}
		else {
			return null;
		}
	}
						
	private NoteStatusElement[] noteStatus;
	private NoteStatusElement element;
	private int index;
	private int lowNote;
	private int highNote;
	private int note;
		
} // End NoteStatus