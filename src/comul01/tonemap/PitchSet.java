package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.geom.Line2D;
import javax.swing.event.*;
import java.util.Vector;
import java.util.Enumeration;
import javax.sound.sampled.*;
import java.awt.font.*;
import java.text.*;
import java.util.*;


/**
  * This is a class that encapsulates a set of Pitch data for a ToneMap 
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class PitchSet implements Serializable {
	
	public static double[] PITCH_FREQ;
	public static final double A440 = 440.00; 
	public static final int MAX_MIDI_NOTE = 120;
	public static final int MIN_MIDI_NOTE = 12;
	public static final int CENTS_OCTAVE = 1200;
	public static final int CENTS_HALFSTEP = 50;
	
	public static char[][] NOTE_SYMBOLS = {{'C', ' '},
										{'C', '#'},
										{'D', ' '},
										{'D', '#'},
										{'E', ' '},
										{'F', ' '},
										{'F', '#'},
										{'G', ' '},
										{'G', '#'},
										{'A', ' '},
										{'A', '#'},
										{'B', ' '},
										{'?', ' '}};
										
	static {
		
		PITCH_FREQ = new double[MAX_MIDI_NOTE - MIN_MIDI_NOTE + 1];
		
		for (int i = 0; i < PITCH_FREQ.length; i++){
			PITCH_FREQ[i] = getMidiFreq(MIN_MIDI_NOTE+i);
		}
	}
	
	public PitchSet(int lowNote, int highNote) {
		
		setLowIndex(lowNote - MIN_MIDI_NOTE);
		setHighIndex(highNote - MIN_MIDI_NOTE);
		setIndex(lowNote - MIN_MIDI_NOTE);
	}
			
	public static double getMidiFreq(int note){
		return ((A440/32)*(Math.pow(2.0, ((note - 9.0) /12.0))));
	}
	
	public static int noteSymbolToMidi(NoteSymbol noteSymbol){
		return 0;
	}
	
	public static NoteSymbol MidiNoteToSymbol(int note){
		NoteSymbol noteSymbol = new NoteSymbol();
		noteSymbol.noteChar = NOTE_SYMBOLS[note%12][0];
		noteSymbol.noteSharp = NOTE_SYMBOLS[note%12][1];
		noteSymbol.noteOctave = (int)Math.floor((double)note/12.0)-1;	
		return noteSymbol;
	}
	
	/** See craig tuner.java
	*/
	public static int freqToMidiNote(double freq){
	
		if (PITCH_FREQ[0] >= freq) {
			if (getFreqDiff(0, freq) >= -CENTS_HALFSTEP)
				return MIN_MIDI_NOTE;
			else
				return -1;
				
		}
		
		else if (PITCH_FREQ[PITCH_FREQ.length - 1] <= freq) {
			if (getFreqDiff(0, freq) <= CENTS_HALFSTEP)
				return MAX_MIDI_NOTE;
			else
				return -1;
		}
		
		for (int i=0; i < (PITCH_FREQ.length - 1); i++){
			if ((PITCH_FREQ[i] <= freq) && (freq <= PITCH_FREQ[i+1])){
				double d1 = Math.abs(PITCH_FREQ[i] - freq);
				double d2 = Math.abs(PITCH_FREQ[i+1]- freq);
				if (d1 >= d2)
					return ((i+1)+ MIN_MIDI_NOTE);
				else
					return (i + MIN_MIDI_NOTE);
			}
		}
		return -1;
	}
	
	public static int getFreqDiff(int note, double freq){
		if (note < MIN_MIDI_NOTE) note=MIN_MIDI_NOTE;
		if (note > MAX_MIDI_NOTE) note=MAX_MIDI_NOTE;
		double freqNote = PITCH_FREQ[note-MIN_MIDI_NOTE];
		return (int) (-CENTS_OCTAVE * Math.log(freqNote/freq)/Math.log(2.0));
	}
			
	public void setLowIndex(int index) {
		lowPitchIndex = index;
	}
	
	public void setHighIndex(int index) {
		highPitchIndex = index;
	}
	
	public void setIndex(int index) {
		currentPitchIndex = index;
	}

	public int pitchToIndex(int pitchNote) {
		setIndex(pitchNote - (lowPitchIndex + MIN_MIDI_NOTE));
		return currentPitchIndex;
	}
	
	
	public int getRange(){
		return (highPitchIndex - lowPitchIndex + 1);
	}
	
	public double getFreq(int index) {
		currentPitchIndex = index;
		System.out.println("PitchSet get freq " + index + ", " + lowPitchIndex);
		return PITCH_FREQ[lowPitchIndex + index];
	}
		
	public int getNote(int index) {
		return (lowPitchIndex + index + MIN_MIDI_NOTE);
	}
			
	public int getLowNote() {
		return (lowPitchIndex + MIN_MIDI_NOTE);
	}
		
	public int getHighNote() {
		return (highPitchIndex + MIN_MIDI_NOTE);
	}
	
	public double[] getFreqSet() {
		freqRange = getRange();
		freqSet = new double[freqRange];
		for (int i = 0; i < freqRange; i++){
			freqSet[i] = PITCH_FREQ[lowPitchIndex + i];
		}
		return freqSet;
	}
	
	private int lowPitchIndex;
	private int highPitchIndex;
	private int currentPitchIndex;
	private double freq;
	private double note;
	private double[] freqSet;
	private int freqRange;
	
} // End PitchSet