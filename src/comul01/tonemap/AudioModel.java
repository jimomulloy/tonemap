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
  * This is a class that encapsulates the Audio properties associated with the ToneMap 
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class AudioModel implements PlayerInterface, ToneMapConstants {

	private class ClipListener implements LineListener {
		public void update(LineEvent event) {
			
			if (event.getType() == LineEvent.Type.STOP && playState != PAUSED) {
				audioEOM = true;
				if (playState != STOPPED) { 
					clip.stop();
					playState = EOM;
				}
			
			}
		}
	}

	public double timeStart = (double) INIT_TIME_START;
	public double timeEnd = (double) INIT_TIME_END;
	public double sampleTimeSize = (double) INIT_SAMPLE_SIZE;
	public int panSetting = INIT_PAN_SETTING;
	public int pitchHigh = INIT_PITCH_HIGH;
	public int pitchLow = INIT_PITCH_LOW;
	public int gainSetting = INIT_VOLUME_SETTING;
	public int resolution=1;
	public int tFactor=10;
	public int pFactor=60;
	public int transformMode=TRANSFORM_MODE_JAVA;

	private int playState = STOPPED;

	private ToneMapFrame toneMapFrame;
	private Player player;
	private AudioPanel audioPanel;
	private ToneMapMatrix toneMapMatrix;
	
	private ToneMap toneMap;
	private TimeSet timeSet;
	private PitchSet pitchSet;

	private double sampleRate;
	private int numChannels;
	private double sampleBitSize;

	private String errStr;

	private double duration, seconds;

	private AudioInputStream audioInputStream;
	private AudioFormat format = null;
	private int[] audioData = null;
	private byte[] audioBytes = null;
	private int nlengthInSamples;
	private double[] audioFTPower = null;
	private double[] pitchFreqSet;

	private int timeRange;
	private int pitchRange;

	private double transTime;
   
	private String fileName = "untitled";
	private File file;

	Wavelet wavelet = new Wavelet();

	private Clip clip;
	private boolean audioEOM;

	public AudioModel(ToneMapFrame toneMapFrame) {

		this.toneMapFrame = toneMapFrame;
		audioPanel = new AudioPanel(this);
		if (toneMapFrame.getJNIStatus()) audioPanel.jniB.setEnabled(true);

	}
	
	public void clear() {
		playStop();
		clip = null;
		audioBytes = null;
		audioData = null;
		audioFTPower = null;
		file = null;
		fileName = "";
		audioPanel.fileNameField.setText("");
		audioPanel.durationField.setText("");
		audioPanel.sampleRateField.setText("");
		audioPanel.bitSizeField.setText("");
		audioPanel.channelsField.setText("");
		audioPanel.timeControl.setTimeMax(INIT_TIME_MAX);
	}

	public boolean openFile() {
		return audioPanel.openFile();
	}
	public double[] getAudioFTPower() {
		return audioFTPower;
	}
	public double getDuration() {
		return duration;
	}
	public double getEndTime() {
		return timeEnd;
	}
	public File getFile() {
		return file;
	}
	public String getFileName() {
		return fileName;
	}
	public int getHighPitch() {
		return pitchHigh;
	}
	public int getLowPitch() {
		return pitchLow;
	}
	public int getNumChannels() {
		return numChannels;
	}
	public JPanel getPanel() {
		return audioPanel;
	}
	public double getSampleBitSize() {
		return sampleBitSize;
	}
	public double getSampleRate() {
		return sampleRate;
	}
	public double getSampleTimeSize() {
		return sampleTimeSize;
	}
	public double getStartTime() {
		return timeStart;
	}
	public boolean load(File file) {

		this.file = file;

		if (file != null && file.isFile()) {
			try {
				errStr = null;
				audioInputStream = AudioSystem.getAudioInputStream(file);

				fileName = file.getName();

				format = audioInputStream.getFormat();
				
			} catch (Exception ex) {
				reportStatus(ex.toString());
				return false;
			}
		} else {
			reportStatus("Audio file required.");
			return false;
		}

		numChannels = format.getChannels();
		sampleRate = (double) format.getSampleRate();
		sampleBitSize = format.getSampleSizeInBits();
		long frameLength = audioInputStream.getFrameLength();
		long milliseconds =
					(long) ((frameLength * 1000)/audioInputStream.getFormat().getFrameRate());
		double audioFileDuration = milliseconds / 1000.0;
		 
		if (audioFileDuration > MAX_AUDIO_DURATION) duration = MAX_AUDIO_DURATION;
		else duration = audioFileDuration;
		
		frameLength = (int)Math.floor((duration/audioFileDuration)*(double)frameLength); 

		try {
			audioBytes = new byte[(int)frameLength * format.getFrameSize()];
			audioInputStream.read(audioBytes);
		} catch (Exception ex) {
			reportStatus(ex.toString());
			return false;
		}

		getAudioData();
		
		return true;
	}

	private void getAudioData() {
		
		if (format.getSampleSizeInBits() == 16) {
			nlengthInSamples = audioBytes.length / 2;
			audioData = new int[nlengthInSamples];
			if (format.isBigEndian()) {
				for (int i = 0; i < nlengthInSamples; i++) {
					// First byte is MSB (high order) 
					int MSB = (int) audioBytes[2 * i];
					// Second byte is LSB (low order) 
					int LSB = (int) audioBytes[2 * i + 1];
					audioData[i] = MSB << 8 | (255 & LSB);
				}
			} else {
				for (int i = 0; i < nlengthInSamples; i++) {
					// First byte is LSB (low order) 
					int LSB = (int) audioBytes[2 * i];
					// Second byte is MSB (high order) 
					int MSB = (int) audioBytes[2 * i + 1];
					audioData[i] = MSB << 8 | (255 & LSB);
				}
			}
		} else {
			if (format.getSampleSizeInBits() == 8) {
				nlengthInSamples = audioBytes.length;
				audioData = new int[nlengthInSamples];
				if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
					for (int i = 0; i < audioBytes.length; i++) {
						audioData[i] = audioBytes[i];
					}
				} else {
					for (int i = 0; i < audioBytes.length; i++) {
						audioData[i] = audioBytes[i] - 128;
					}
				}
			}
		}
	}

	public boolean play() {

		try {
			if (playState != STOPPED) playStop();

			if (audioBytes == null) return false; 
			
			DataLine.Info info =
					new DataLine.Info(
						Clip.class,
						format);

			clip = (Clip) AudioSystem.getLine(info);
			clip.addLineListener(new ClipListener());

			long clipStart = (long)(audioBytes.length*getStartTime()/(getDuration()*1000.0));
			long clipEnd = (long)(audioBytes.length*getEndTime()/(getDuration()*1000.0));
			if ((clipEnd-clipStart)>MAX_CLIP_LENGTH) clipEnd = clipStart + MAX_CLIP_LENGTH;
	        byte[] clipBytes = new byte[(int)(clipEnd-clipStart)];
	        System.arraycopy(audioBytes, (int)clipStart, clipBytes, 0, clipBytes.length);	
   			clip.open(format, clipBytes, 0, clipBytes.length);

   			FloatControl panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
			
			panControl.setValue((float)panSetting / 100.0f);
		
			double value = (double)gainSetting;

			FloatControl gainControl =
				(FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			float dB =
				(float) (Math.log(value == 0.0 ? 0.0001 : value) / Math.log(10.0) * 20.0);
			gainControl.setValue(dB);
			double playStartTime = (player.getSeekTime()/100)*(playGetLength());
			clip.setMicrosecondPosition((long)playStartTime);
			
			clip.start();

			playState = PLAYING;

			return true;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			playState = STOPPED;
			clip = null;
			return false;
		}
	}

	public double playGetLength() {
		if (clip != null) {
			return clip.getMicrosecondLength()*1000000.0;
		}
		return 0.0;
	}

	public int playGetState() {

		return playState;

	}
	public double playGetTime() {

		if (clip != null) {
			return ((double)clip.getMicrosecondPosition())/1000.0;
		}
		return 0;
	}
	public void playLoop() {

		if (clip != null) {
			
			double playStartTime = (player.getSeekTime()/100)*(playGetLength());
			clip.setMicrosecondPosition((long)playStartTime);
			if (playState != PLAYING) {
				clip.start();
				playState = PLAYING;
			}

		}
	}
	public void playPause() {

		if (clip != null) {
			if (playState == PLAYING) {

				clip.stop();
				playState = PAUSED;

			} 
		}
	}
	public void playResume() {

		if (clip != null) {
			if (playState == PAUSED) {

				clip.start();
				playState = PLAYING;

			} 
		}
	}
	public void playSetPlayer(Player player) {
	
		this.player = player;
	}
	public void playSetSeek(double seekTime) {

		if (clip != null) {
			clip.setMicrosecondPosition((long)(seekTime*1000.0));
		}
	}
	public void playStop() {

		if (clip != null) {
			if (playState == PLAYING || playState == PAUSED) {

				clip.stop();
				clip.close();
				playState = STOPPED;
			}
		}

	}
	private void reportStatus(String msg) {
		if ((errStr = msg) != null) {
			System.out.println(errStr);
		
		}
	}

	public void setTime(TimeSet timeSet) {
		
		audioPanel.timeControl.setTimeMax((int)(timeSet.getEndTime()-timeSet.getStartTime()));
		
	}
	
	public void setPitch(PitchSet pitchSet) {
		audioPanel.pitchControl.setPitchRange((int)(pitchSet.getLowNote()), (int)(pitchSet.getHighNote()));
		
	}
	
	public boolean transform(ProgressListener progressListener) {

		toneMap = toneMapFrame.getToneMap();
		timeSet = toneMap.getTimeSet();
		pitchSet = toneMap.getPitchSet();
		timeRange = timeSet.getRange();
		pitchRange = pitchSet.getRange();
		
		pitchFreqSet = pitchSet.getFreqSet();
		audioFTPower = new double[timeRange * (pitchRange+1)];

		int startSample = timeSet.getStartSample();
		int endSample = timeSet.getEndSample();
		int sampleLength = (int)Math.floor((endSample - startSample)/((double)resolution));
		double[] audioSamples = new double[sampleLength];

		for (int i = 0; i < sampleLength; i++) {
			audioSamples[i] = (double) audioData[startSample + i*resolution];
		}

		int sampleIndexSize = (int)Math.floor((double)timeSet.getSampleIndexSize()/(double)resolution);
		
		double dt = (double)resolution/sampleRate;
		if (transformMode == TRANSFORM_MODE_JAVA) {
			wavelet.convert(
				audioFTPower,
				audioSamples,
				pitchFreqSet,
				dt,
				(double)pFactor,
				(double)tFactor,
				sampleIndexSize,
				sampleLength,
				pitchRange,
				progressListener);
		} else {
			
			WaveletJNI waveletJNI = new WaveletJNI();
			
			waveletJNI.waveletConvert( audioFTPower, 
						audioSamples, 
						pitchFreqSet, 
						dt, 
						(double)pFactor, 
						(double)tFactor, 
						sampleIndexSize, 
						sampleLength, 
						pitchRange,
						progressListener);	
		}

		return true;
	}
} // End AudioModel