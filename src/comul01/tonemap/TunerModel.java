
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


/**
  * This is a class that encapsulates the Tuner properties associated with the ToneMap 
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class TunerModel implements ToneMapConstants {

	public TunerModel(ToneMapFrame toneMapFrame ) {
	
		this.toneMapFrame = toneMapFrame;
		tunerPanel = new TunerPanel(this);
		
	}

	public void clear() {
		noteList = null;
		noteStatus = null;
	}
	
	public boolean execute(ProgressListener progressListener) {
		
		this.progressListener = progressListener;
		toneMap = toneMapFrame.getToneMap();
		
		timeSet = toneMap.getTimeSet();
		pitchSet = toneMap.getPitchSet();
		timeRange = timeSet.getRange();
		pitchRange = pitchSet.getRange();
		
		toneMapMatrix = toneMap.getMatrix();
		toneMapMatrix.reset();
		
		initOvertoneSet();
		harmonics = overtoneSet.getHarmonics();
		formants = overtoneSet.getFormants();
		
		switch(processMode)
			{	case NOTE_MODE:
					if (!noteScan()) return false;
					break;
					
				case BEAT_MODE:
					if (!beatScan()) return false;
					break;
					
				default:
					break;
			}
		
		return true;		
	}

	public double getDuration() {
		return duration;
	}
	public double getEndTime() {
		return timeEnd;
	}
	public int getHighPitch() {
		return pitchHigh;
	}
	public int getLowPitch() {
		return pitchLow;
	}
	public double getStartTime() {
		return timeStart;
	}
	public JPanel getPanel() {
		return tunerPanel;
	}
	
	public void setTime(TimeSet timeSet) {
		
		tunerPanel.timeControl.setTimeMax((int)(timeSet.getEndTime()-timeSet.getStartTime()));
		
	}
	
	public void setPitch(PitchSet pitchSet) {
		tunerPanel.pitchControl.setPitchRange((int)(pitchSet.getLowNote()), (int)(pitchSet.getHighNote()));
		
	}
	
	private boolean noteScan() {
		
		timeRange = timeSet.getRange();
		pitchRange = pitchSet.getRange();
		
		noteList = new NoteList();
				
		noteStatus = new NoteStatus(pitchSet);
			
		ToneMapMatrix.Iterator mapIterator = toneMapMatrix.newIterator(); 
		mapIterator.firstPitch();
		mapIterator.setPitchIndex(pitchSet.pitchToIndex(getLowPitch()));
			
		do {
			progressListener.setProgress((int)(((double)mapIterator.getIndex()/(double)toneMapMatrix.getMatrixSize())*100.0));
			note = pitchSet.getNote(mapIterator.getPitchIndex());
			noteStatusElement = noteStatus.getNote(note);
			mapIterator.firstTime();
			mapIterator.setTimeIndex(timeSet.timeToIndex(getStartTime()));
			do {
				index = mapIterator.getIndex();
				
				// ACTUALLY USE PROCESSED AMPLITUDE
				amplitude = mapIterator.getElement().postAmplitude;

				toneMapElement = mapIterator.getElement();
				if (toneMapElement == null || toneMapElement.preAmplitude == -1) continue;
					
				time = timeSet.getTime(mapIterator.getTimeIndex());
			    
				switch(noteStatusElement.state)
				{	case OFF:
						if (amplitude >= (double)noteLow/100.0) {
							noteStatusElement.state = ON;
							noteStatusElement.onTime = time;
							noteStatusElement.onIndex = index;
							noteStatusElement.offTime = 0.0;
							noteStatusElement.offIndex = 0;
							if (amplitude >= (double)noteHigh/100.0) {
								noteStatusElement.highFlag = true;
							}
						}
						break;
					
					case ON:
						if (amplitude < (double)noteLow/100.0) {
							noteStatusElement.state = PENDING;
							noteStatusElement.offTime = time;
							noteStatusElement.offIndex = index-1;
						} else {
							if (amplitude >= (double)noteHigh/100.0) {
								noteStatusElement.highFlag = true;
							}
						}
						break;
						
					case PENDING:
						if (amplitude >= (double)noteLow/100.0) {
							if ((time - noteStatusElement.offTime) < 
									(noteSustain)) { 
								noteStatusElement.state = ON;
								noteStatusElement.offTime = 0.0;
								noteStatusElement.offIndex = 0;
								if (amplitude >= (double)noteHigh/100.0) {
									noteStatusElement.highFlag = true;
								}
							} else {
								processNote();
								noteStatusElement.state = ON;
								noteStatusElement.onTime = time; 
								noteStatusElement.onIndex = index;
								noteStatusElement.offTime = 0.0;
								noteStatusElement.offIndex = 0;
								if (amplitude >= (double)noteHigh/100.0) {
									noteStatusElement.highFlag = true;
								}
							}
						} else { 
					 		if ((time - noteStatusElement.offTime) >= 
									(noteSustain)) { 
								processNote();
								noteStatusElement.state = OFF;
								noteStatusElement.onTime = 0.0; 
								noteStatusElement.onIndex = 0;
								noteStatusElement.offTime = 0.0;
								noteStatusElement.offIndex = 0;
								noteStatusElement.highFlag = false;
							}
						}
						
						break;
					
					default:
						break;
				}
				
				
			} while (mapIterator.nextTime() && timeSet.timeToIndex(getEndTime()) >= mapIterator.getTimeIndex());
			
			switch(noteStatusElement.state)
			{	case OFF:
					break;
			
				case ON:
					noteStatusElement.offTime = time;
					noteStatusElement.offIndex = index-1;
					
				case PENDING:
					
					processNote();
					noteStatusElement.state = OFF;
					noteStatusElement.onTime = 0.0;
					noteStatusElement.onIndex = 0;
					noteStatusElement.offTime = 0.0;
					noteStatusElement.offIndex = 0;
					noteStatusElement.highFlag = false;
					break;
								
				default:
					break;
			}
			
												
		} while (mapIterator.nextPitch() && pitchSet.pitchToIndex(getHighPitch()) >= mapIterator.getPitchIndex());
		
		return true;	
	}
	
	private void processNote () {
		
		if (noteStatusElement.highFlag == false) {
			System.out.println("Discard note - no high flag");
			return;
		}

		if ((noteStatusElement.offTime 
					- noteStatusElement.onTime) < (double)noteMinDuration ){
			System.out.println("Discard note < min duration");
			return;
		}
	
		
		int index;
		
		int numSlots = 0;
		int numLowSlots = 0;
		double amplitude; 
		double ampSum = 0;
		double FTPower; 
		double FTPowerSum = 0;
		double minAmp = 0;
		double maxAmp = 0;
		double avgAmp = 0;
		double minFTPower = 0;
		double maxFTPower = 0;
		double avgFTPower = 0;
		double percentMin = 0;
		double startTime, endTime;
		int pitchIndex, startTimeIndex, endTimeIndex, startIndex, endIndex;
		
		ToneMapMatrix.Iterator mapIterator = toneMapMatrix.newIterator(); 
		mapIterator.setIndex(noteStatusElement.onIndex);
		startIndex = noteStatusElement.onIndex;
		endIndex = noteStatusElement.offIndex;
		
		do {

			numSlots++;
			
			FTPower = mapIterator.getElement().postFTPower;
			FTPowerSum = FTPowerSum + FTPower;
			if (maxFTPower < FTPower) {
				maxFTPower = FTPower;
				if (peakSwitch) {
					startIndex = mapIterator.getIndex();
				}
			}
			if ((minFTPower == 0) || (minFTPower > FTPower)) minFTPower = FTPower;
			
			amplitude = mapIterator.getElement().postAmplitude;
			ampSum = ampSum + amplitude;
			if (maxAmp < amplitude) maxAmp = amplitude;
			if ((minAmp == 0) || (minAmp > amplitude)) minAmp = amplitude;

			if (amplitude < (double)noteLow/100.0) numLowSlots++;
			if (peakSwitch && (amplitude >= (double)noteHigh/100.0)) endIndex = mapIterator.getIndex();

			mapIterator.getElement().noteState = ON;
			
		} while (mapIterator.nextTime() 
						&& mapIterator.getIndex() <= noteStatusElement.offIndex );
		
		if (startIndex > endIndex) return;
		if (startIndex == endIndex) endIndex++; 
		
		mapIterator.setIndex(startIndex);
		mapIterator.getElement().noteState = START;
		
		pitchIndex = mapIterator.getPitchIndex();
		
		startTime = timeSet.getTime(mapIterator.getTimeIndex());
		startTimeIndex = mapIterator.getTimeIndex();
		
		mapIterator.setIndex(endIndex);
		mapIterator.getElement().noteState = END;
		endTime = timeSet.getTime(mapIterator.getTimeIndex());
		endTimeIndex = mapIterator.getTimeIndex();

		avgFTPower= FTPowerSum/numSlots;
		avgAmp = ampSum/numSlots;
		percentMin = numLowSlots/numSlots;
		
		NoteListElement noteListElement
					= new NoteListElement ( note,
											pitchIndex,
											startTime,
											endTime,
											startTimeIndex,
											endTimeIndex,
											avgFTPower,
											maxFTPower,
											minFTPower,
											avgAmp,
											maxAmp,
											minAmp,
											percentMin );
		
		mapIterator.setIndex(startIndex);
		
		do { 
			
			mapIterator.getElement().noteListElement = noteListElement;
		
		} while (mapIterator.nextTime() 
						&& mapIterator.getIndex() <= endIndex );

		if (harmonicSwitch == true) {
			processOvertones(noteListElement);
		}
		
		if (undertoneSwitch == true) {
			processUndertones(noteListElement);
		}
	
		noteList.add(noteListElement);
	}
			
	private void processOvertones(NoteListElement noteListElement) {
		
		
		ToneMapMatrix.Iterator mapIterator = toneMapMatrix.newIterator(); 
		
		mapIterator.first();
		mapIterator.setPitchIndex(noteListElement.pitchIndex);
		mapIterator.setTimeIndex(noteListElement.startTimeIndex);
	
		double f0 = pitchSet.getFreq(mapIterator.getPitchIndex());
		int lastNote = pitchSet.getNote(mapIterator.getPitchIndex());
	
		double overToneFTPower;
		double overToneAmplitude; 

		double freq;
		int note;
		int n = 2;
		
		for (int i = 0; i < harmonics.length; i++) {
			freq = n * f0;
			note = pitchSet.freqToMidiNote(freq);
			if (note == -1 || note > pitchSet.getHighNote()) break;			
			mapIterator.setTimeIndex(noteListElement.startTimeIndex);
			mapIterator.setPitchIndex(mapIterator.getPitchIndex() + note - lastNote);
			do {
				
				if (mapIterator.getElement() == null || mapIterator.getElement().preAmplitude == -1) continue;
				
				attenuate(mapIterator.getElement(), noteListElement.avgFTPower, harmonics[i]);
			} while (mapIterator.nextTime() 
						&& mapIterator.getTimeIndex() <= noteListElement.endTimeIndex );
			
			lastNote = note;
			n++;
		
		}	
	}

	private void attenuate(ToneMapElement overToneElement, double fundamental, double harmonic) {

		
		double overToneData = fundamental * harmonic;

		if ( overToneElement.postFTPower <= overToneData ) {
			overToneElement.postFTPower = 0;
		} else {
			overToneElement.postFTPower -= overToneData;
		}

		overToneElement.postAmplitude = toneMapMatrix.FTPowerToAmp(overToneElement.postFTPower);
		
	}
		
	

	private void processUndertones(NoteListElement noteListElement) {
		
		NoteListElement underNote = null;
		
		ToneMapMatrix.Iterator mapIterator = toneMapMatrix.newIterator(); 
		
		mapIterator.first();
		mapIterator.setPitchIndex(noteListElement.pitchIndex);
		mapIterator.setTimeIndex(noteListElement.startTimeIndex);
		if(mapIterator.prevPitch()){
		
			int startTime = noteListElement.startTimeIndex;
			int endTime = noteListElement.endTimeIndex;
			
			do { 
				underNote = mapIterator.getElement().noteListElement;
				if (underNote != null) {
	
					if (startTime == underNote.startTimeIndex 
						&& endTime == underNote.endTimeIndex) {
					// check this note note higher amplitude;
						if (noteListElement.avgAmp < underNote.avgAmp) {
							noteListElement.underTone = true;
							System.out.println("B - this note is undertone");
						
						} else {
							underNote.underTone = true;
							System.out.println("C - lower note is undertone");
						
						}
						break;
					}
	
					if (startTime >= underNote.startTimeIndex 
							&& endTime <= underNote.endTimeIndex) {
						// check this note note higher amplitude;
						noteListElement.underTone = true;
						System.out.println("A - this note is undertone ");
						break;
					}
					

					if (endTime < underNote.endTimeIndex) {
						System.out.println("D - end < lower end ");
						break;
					}	
					
					if (startTime <= underNote.startTimeIndex) {
						underNote.underTone = true;
						System.out.println("E - lower note is undertone");
						
					}
					System.out.println("F - next");
	
					mapIterator.setTimeIndex(underNote.endTimeIndex);
						
				}
	
			} while (mapIterator.nextTime() 
						&& mapIterator.getTimeIndex() <= endTime );

		}
		
	}

	private boolean beatScan() {

		double amplitude;
		int numSlots; 
		double ampSum = 0;
		double avgAmp = 0;
		int startTimeIndex=0, endTimeIndex=0;
		boolean powerON = false, powerHigh;
		
		
		timeRange = timeSet.getRange();
		pitchRange = pitchSet.getRange();
		
		noteList = new NoteList();

		ToneMapMatrix.Iterator mapIterator = toneMapMatrix.newIterator(); 
		mapIterator.firstTime();
		mapIterator.setTimeIndex(timeSet.timeToIndex(getStartTime()));

		do {
			progressListener.setProgress((int)(((double)mapIterator.getIndex()/(double)toneMapMatrix.getMatrixSize())*100.0));
			time = timeSet.getTime(mapIterator.getTimeIndex());
			mapIterator.firstPitch();
			mapIterator.setPitchIndex(pitchSet.pitchToIndex(getLowPitch()));
			numSlots = 0;
			ampSum=0;
			
			do {
				numSlots++;
				index = mapIterator.getIndex();
				note = pitchSet.getNote(mapIterator.getPitchIndex());
				amplitude = mapIterator.getElement().preAmplitude;
				ampSum = ampSum + amplitude;
				
							
			} while (mapIterator.nextPitch() && pitchSet.pitchToIndex(getHighPitch()) >= mapIterator.getPitchIndex());
		
			avgAmp = ampSum/numSlots;
			System.out.println("beat avg amp"+avgAmp);
			if (powerON) {
				if (avgAmp < (double)noteLow/100.0) {
					powerON = false;
					endTimeIndex = mapIterator.getTimeIndex();
					processBeat(startTimeIndex, endTimeIndex);
				}
			} else {
				if (avgAmp >= (double)noteLow/100.0) {
					powerON = true;
					startTimeIndex = mapIterator.getTimeIndex();
				}
			}
												
		} while (mapIterator.nextTime() && timeSet.timeToIndex(getEndTime()) >= mapIterator.getTimeIndex());
		
		if (powerON) {
			powerON = false;
			endTimeIndex = mapIterator.getTimeIndex();
			processBeat(startTimeIndex, endTimeIndex);
		}
		
		return true;	
	}

	private void processBeat(int startTimeIndex, int endTimeIndex) {

		System.out.println("beat process"+startTimeIndex+", "+endTimeIndex);
		int index;
		int numSlots = 0;
		double amplitude; 
		double ampSum = 0;
		double FTPower; 
		double FTPowerSum = 0;
		double minAmp = 0;
		double maxAmp = 0;
		double avgAmp = 0;
		double minFTPower = 0;
		double maxFTPower = 0;
		double avgFTPower = 0;
		double percentMin = 0;
		double startTime, endTime;
		int pitchIndex=0, startIndex=0, endIndex=0;
		double maxAmpSum = 0;
		int maxNote=0;

		if (startTimeIndex > endTimeIndex) return;
		if (startTimeIndex == endTimeIndex) endTimeIndex++; 
		
		ToneMapMatrix.Iterator mapIterator = toneMapMatrix.newIterator(); 
	
		mapIterator.firstPitch();
		mapIterator.setPitchIndex(pitchSet.pitchToIndex(getLowPitch()));
		
		do {
			mapIterator.firstTime();
			mapIterator.setTimeIndex(startTimeIndex);
			ampSum=0;
			
			do {
				index = mapIterator.getIndex();
				note = pitchSet.getNote(mapIterator.getPitchIndex());
				numSlots++;
				amplitude = mapIterator.getElement().preAmplitude;
				ampSum = ampSum + amplitude;					
							
			} while (mapIterator.nextTime() && endTimeIndex >= mapIterator.getTimeIndex());

			if (ampSum > maxAmpSum) {
				maxAmpSum = ampSum;
				maxNote = note;
				pitchIndex = mapIterator.getPitchIndex();
			}
			
		} while (mapIterator.nextPitch() && pitchSet.pitchToIndex(getHighPitch()) >= mapIterator.getPitchIndex());
					
		mapIterator.firstPitch();
		mapIterator.firstTime();
		mapIterator.setPitchIndex(pitchIndex);
		mapIterator.setTimeIndex(startTimeIndex);
		startIndex = mapIterator.getIndex();
		startTime = timeSet.getTime(mapIterator.getTimeIndex());
		mapIterator.setTimeIndex(endTimeIndex);
		endIndex = mapIterator.getIndex();
		endTime = timeSet.getTime(mapIterator.getTimeIndex());
	
		avgFTPower= toneMapMatrix.getAvgFTPower();
		avgAmp = toneMapMatrix.getAvgAmplitude();
		maxFTPower= toneMapMatrix.getMaxFTPower();
		minFTPower= toneMapMatrix.getMinFTPower();
		maxAmp= toneMapMatrix.getMaxAmplitude();
		minAmp= toneMapMatrix.getMinAmplitude();

		System.out.println("notelist "+maxNote+", "+startTime+", "+endTime+", "+startIndex+", "+endIndex);		
		percentMin = 0;
		
		NoteListElement noteListElement
					= new NoteListElement ( maxNote,
											pitchIndex,
											startTime,
											endTime,
											startTimeIndex,
											endTimeIndex,
											avgFTPower,
											maxFTPower,
											minFTPower,
											avgAmp,
											maxAmp,
											minAmp,
											percentMin );
		
		mapIterator.setIndex(startIndex);
		
		do { 
			
			mapIterator.getElement().noteListElement = noteListElement;
		
		} while (mapIterator.nextTime() 
						&& mapIterator.getIndex() <= endIndex );
	
		noteList.add(noteListElement);
	}
	
	public NoteList getNoteList() {
		return noteList;
	}
	
		
	private ToneMapFrame toneMapFrame;
	private ToneMap toneMap;

	private TunerPanel tunerPanel;

	private double sampleRate;
	private int numChannels;
	private double sampleBitSize;
	private ProgressListener progressListener;
	private String errStr;
	
	private double duration, seconds;
	
	public double timeStart=INIT_TIME_START;
	public double timeEnd=INIT_TIME_END;

	public int pitchHigh=INIT_PITCH_HIGH;
	public int pitchLow=INIT_PITCH_LOW;
	
	public int noteLow=INIT_NOTE_LOW;
	public int noteHigh=INIT_NOTE_HIGH;
	public int noteSustain=INIT_NOTE_SUSTAIN;
	public int noteMinDuration=INIT_NOTE_MIN_DURATION;
	public int noteMaxDuration=INIT_NOTE_MAX_DURATION;
	
	public int noiseLow=INIT_NOISE_LOW;
	public int noiseHigh=INIT_NOISE_HIGH;

	public int harmonic1Setting = 100;
	public int harmonic2Setting = 100;
	public int harmonic3Setting = 100;
	public int harmonic4Setting = 100;
	public int formantLowSetting = 100;
	public int formantMiddleSetting = 100;
	public int formantHighSetting = 100;
	public int formantFreqSetting = 1000;

	public int droneSetting = 100;
	public int undertoneSetting = 100;
	public int spikeSetting = 100;
	
	public boolean harmonicSwitch;
	public boolean formantSwitch;
	public boolean undertoneSwitch;
	public boolean droneSwitch;
	public boolean spikeSwitch;
	public boolean peakSwitch;
	
	public int processMode = NOTE_MODE;

	private ToneMapMatrix toneMapMatrix;
	private ToneMapElement element;

	private int matrixLength;
	
	private TimeSet timeSet;
	private PitchSet pitchSet;
	
	private int timeRange;
	private int pitchRange;
	private double amplitude;

	private int index;
	private NoteSequence noteSequence;
	private NoteSequenceElement noteSequenceElement;
	private NoteList noteList;
	private NoteListElement noteListElement;
	private ToneMapMatrix.Iterator mapIterator;
	private ToneMapElement toneMapElement;

	OvertoneSet overtoneSet;
	double[] harmonics;
	double[][] formants;	
	
	private long tick;
	private double time;
	
	private NoteStatus noteStatus;
	private NoteStatusElement noteStatusElement;
	private int note;
	private int velocity;

	private void initOvertoneSet(){

		overtoneSet = new OvertoneSet();
		
		double[] initHarmonics = { (double)harmonic1Setting/100.0,
								(double)harmonic2Setting/100.0,
								(double)harmonic3Setting/100.0,
								(double)harmonic4Setting/100.0 };

		double[][] initFormants = { {PitchSet.getMidiFreq(pitchSet.getLowNote()), (double)formantLowSetting},
									{(double)formantFreqSetting, (double)formantMiddleSetting },
									{PitchSet.getMidiFreq(pitchSet.getHighNote()), (double)formantHighSetting} };
	
		overtoneSet.setHarmonics(initHarmonics);
		overtoneSet.setFormants(initFormants);
		 
	}}