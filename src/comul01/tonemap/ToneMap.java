package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;


/**
  * This is the ToneMap class
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class ToneMap implements ToneMapConstants {

	public ToneMap(ToneMapFrame toneMapFrame ) {
	
		this.toneMapFrame = toneMapFrame;
		toneMapPanel = new ToneMapPanel(this);
		audioModel = toneMapFrame.getAudioModel();
		midiModel = toneMapFrame.getMidiModel();
		tunerModel = toneMapFrame.getTunerModel();
				
	}

	public void clear(){

		tunerModel.clear();
		midiModel.clear();
		audioModel.clear();
		toneMapMatrix = null;
		toneMapPanel.init();
	}
	
	public JPanel getPanel(){
		return toneMapPanel;
	}
	
	public boolean open(File file) {
		
		this.file = file;	
		fileName = file.getName();
		System.out.println("Opened filename, file: " + fileName + ", " + file);
			
		if (file.exists()) {
			try {
				FileInputStream fin = new FileInputStream(file);
				ObjectInputStream istrm = new ObjectInputStream(fin);
				ToneMapSerial toneMapSerial = (ToneMapSerial)istrm.readObject();
				toneMapMatrix = toneMapSerial.matrix;
				timeSet = toneMapSerial.timeSet;
				pitchSet = toneMapSerial.pitchSet; 
				toneMapPanel.init();
				midiModel.setTime(timeSet);
				tunerModel.setTime(timeSet);
				midiModel.setPitch(pitchSet);
				tunerModel.setPitch(pitchSet);
				toneMapPanel.processB.setEnabled(true);
				toneMapPanel.revalidate();
				toneMapPanel.repaint();		

				toneMapFrame.reportStatus(SC_TONEMAP_LOADED);
				return true;
				
			} catch (IOException io ){
				System.err.println("IoException: " + io.getMessage());
				return false;
			} catch (ClassNotFoundException cnf) {
				System.err.println("Class not founed: " + cnf.getMessage());
				return false;
			}
		} else {
			// should put out error message on status panel !!
			System.err.println("No such file: " + file);
			return false;
		}
	}
	
	public boolean save(File file) {
	
		this.file = file;
		fileName = file.getName();
		System.out.println("Save filename, file: " + fileName + ", " + file);
				
		if (file.exists()|| !file.exists()) {
			try {
				FileOutputStream fout = new FileOutputStream(file);
				ObjectOutputStream ostrm = new ObjectOutputStream(fout);
				toneMapSerial = new ToneMapSerial(toneMapMatrix, timeSet, pitchSet);
				ostrm.writeObject(toneMapSerial);
				ostrm.flush();
				return true;
			} catch (IOException io ){
				System.err.println("IoException: " + io.getMessage());
				io.printStackTrace();
				return false;
			} 
		} else {
			// should put out error message on status panel !!
			System.err.println("No such file: " + file);
			return false;
		}
	}
	
	public boolean loadAudio() {

		toneMapFrame.reportStatus(SC_TONEMAP_LOADING);

		if (audioModel.getFile() == null) {
			if (!audioModel.openFile()) return false;
		}
		
		timeSet =
				new TimeSet(audioModel.getStartTime(), 
							audioModel.getEndTime(), 
							audioModel.getSampleRate(), 
							audioModel.getSampleTimeSize());

		pitchSet = new PitchSet(audioModel.getLowPitch(), 
							audioModel.getHighPitch());

		if (!audioModel.transform(toneMapPanel)) return false;
		
		if (!buildMap()) return false;

		toneMapPanel.init();

		midiModel.setTime(timeSet);
		tunerModel.setTime(timeSet);
		midiModel.setPitch(pitchSet);
		tunerModel.setPitch(pitchSet);
		
		toneMapPanel.revalidate();
		toneMapPanel.repaint();		

		toneMapFrame.reportStatus(SC_TONEMAP_LOADED);
		return true;
	}

	public boolean process() {

		toneMapFrame.reportStatus(SC_TONEMAP_PROCESSING);
	
		if (!tunerModel.execute(toneMapPanel)) return false;

		if (tunerModel.getNoteList() != null) {
			midiModel.writeSequence(tunerModel.getNoteList());
		}
		toneMapPanel.setProgress(100);
		toneMapPanel.revalidate();
		toneMapPanel.repaint();

		toneMapFrame.reportStatus(SC_TONEMAP_PROCESSED);
		return true;
						
	}

	private boolean buildMap() {

		audioFTPower = audioModel.getAudioFTPower();
		matrixLength = audioFTPower.length;
		
		toneMapMatrix = new ToneMapMatrix(matrixLength, 
									timeSet, pitchSet);
		
		minFTPower = 0.0;
		maxFTPower = 0.0;
		for (int i=0; i < matrixLength; i++){
			if (audioFTPower[i] != 0.0){			
				if (maxFTPower < audioFTPower[i]) maxFTPower = audioFTPower[i];
				if (minFTPower == 0.0 || minFTPower > audioFTPower[i]) minFTPower = audioFTPower[i];
			}
		}

		
		logMinFTPower = Math.abs(Math.log(minFTPower/maxFTPower));

		mapIterator = toneMapMatrix.newIterator();
		
		mapIterator.firstPitch();
		
		do {
			mapIterator.firstTime();
			do {
				index = mapIterator.getIndex();
				if (audioFTPower[index] <= 0.0 ){
					amplitude = 0.0;
				}

				else {
					amplitude = (logMinFTPower
						-Math.abs(Math.log(audioFTPower[index]/maxFTPower)))/logMinFTPower;
			  	}
				
		  		mapIterator.newElement(amplitude, audioFTPower[index]);
				
			} while (mapIterator.nextTime() && index < audioFTPower.length);
			
		} while (mapIterator.nextPitch() && index < audioFTPower.length);
		
		toneMapMatrix.reset();
		
		return true;
		
	}
	
	public ToneMapMatrix getMatrix(){
		return toneMapMatrix;
	}
	
	public TimeSet getTimeSet() {
		return timeSet;
	}
	
	public PitchSet getPitchSet() {
		return pitchSet;
	}
	
	
	public AudioModel getAudioModel() {
		return audioModel;
	}
		
	private ToneMapFrame toneMapFrame;
	private ToneMapPanel toneMapPanel;
	
	private AudioModel audioModel;
	private MidiModel midiModel;
	private TunerModel tunerModel;
	
	private ToneMapMatrix toneMapMatrix;
	private ToneMapElement element;

	private double[] audioFTPower;
	private int matrixLength;
	
	private TimeSet timeSet;
	private PitchSet pitchSet;
	
	private double amplitude;
	private double minFTPower;
	private double maxFTPower;
	private double logMinFTPower;
	
	private File file;
	private ToneMapSerial toneMapSerial;
	private String fileName;
	
	private int index;
	
	private ToneMapMatrix.Iterator mapIterator;
	
	
} // End ToneMap