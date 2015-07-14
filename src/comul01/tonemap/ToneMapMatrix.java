package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;

/**
  * This is the ToneMapMatrix class encapsulates Matrix of ToneMap data 
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class ToneMapMatrix implements Serializable {

	public ToneMapMatrix(int matrixSize, 
						TimeSet timeSet, PitchSet pitchSet ) {
	
		this.matrixSize = matrixSize;
		this.timeSet = timeSet;
		this.pitchSet = pitchSet;
		timeRange = timeSet.getRange();
		pitchRange = pitchSet.getRange();
		matrix = new ToneMapElement[matrixSize];
		
	}
	
	public void reset(){
		
		maxAmplitude = 0;
		minAmplitude = 0;
		maxFTPower = 0;
		minFTPower = 0;
		avgAmplitude = 0;
		avgFTPower = 0;
		long count = 0;
		for (int i = 0; i < (matrixSize-1); i++ ){
			if (matrix[i] != null) {
				if (matrix[i].preAmplitude != -1){
					
					count++;
					avgAmplitude += matrix[i].preAmplitude;
					avgFTPower += matrix[i].preFTPower;
				
					if (maxAmplitude < matrix[i].preAmplitude) 
						maxAmplitude = matrix[i].preAmplitude;
					if ((minAmplitude == 0) || (minAmplitude > matrix[i].preAmplitude))
						minAmplitude = matrix[i].preAmplitude;
					if (maxFTPower < matrix[i].preFTPower) 
						maxFTPower = matrix[i].preFTPower;
					if ((minFTPower == 0) || (minFTPower > matrix[i].preFTPower))
						minFTPower = matrix[i].preFTPower;
				}
				
			}
		}
		
		avgAmplitude = avgAmplitude/count;
		avgFTPower = avgFTPower/count;
		
		for (int i = 0; i < (matrixSize-1); i++ ){
			if (matrix[i] != null) {
				if (matrix[i].preAmplitude != -1){ 
					matrix[i].postAmplitude = matrix[i].preAmplitude;
					matrix[i].postFTPower = matrix[i].preFTPower;
					matrix[i].noteState = 0;
					matrix[i].noteListElement = null;
				}
				
			}
		}
	}

	public double FTPowerToAmp(double FTPower) {
		double amplitude = 0.0;
		if ( FTPower <= 0.0 ) return 0.0;
		double logMinFTPower = Math.abs(Math.log(minFTPower/maxFTPower));
		amplitude = (logMinFTPower - Math.abs(Math.log(FTPower/maxFTPower)))/logMinFTPower;
		return amplitude;
	}
	
	
	public double getMaxAmplitude(){
		return maxAmplitude;
	}
	
	public double getMinAmplitude(){
		return minAmplitude;
	}
	
	public double getAvgAmplitude(){
		return avgAmplitude;
	}

	public double getMaxFTPower(){
		return maxFTPower;
	}
	
	public double getMinFTPower(){
		return minFTPower;
	}

	public double getAvgFTPower(){
		return avgFTPower;
	}


	public TimeSet getTimeSet(){
		return timeSet;
	}

	public PitchSet getPitchSet(){
		return pitchSet;
	}
	
	public int getTimeRange(){
		return timeRange;
	}
	
	public int getPitchRange(){
		return pitchRange;
	}
	public int getMatrixSize(){
		return matrixSize;
	}
	public Iterator newIterator() {
		return new Iterator();
	}
	
	public class Iterator {
	
		public Iterator() {
			index = 0;
			timeIndex = 0;
			pitchIndex = 0;
		}
		
		public ToneMapElement getElement() {
			if (matrix[index]==null) System.out.println("null index in matrix");
			//System.out.println("get matrix element i, t, p : " + index + ", " + 
			//			timeIndex + ", " + pitchIndex); 
						
			return matrix[index];
		}
	
		public void setElement(ToneMapElement toneMapElement) {
			matrix[index] = toneMapElement;
		}
	
		public void newElement(double amplitude, double FTPower) {
			toneMapElement = new ToneMapElement(amplitude, FTPower,
												index, timeIndex, pitchIndex );
			
			matrix[index] = toneMapElement;
		}
	
	
		public boolean nextTime() {
			if (timeIndex < (timeRange-1)){
				timeIndex++;
				index++;
				return true;
			}
			else
				return false;
		}
	
		public boolean nextPitch() {
		    if (pitchIndex < (pitchRange-1)){
				pitchIndex++;
				index = index + timeRange;
				return true;
			}
			else
				return false;
		}
	
		public boolean next() {
			if (index < (matrixSize-1)){
				index++;
				if (timeIndex < (timeRange)){
					timeIndex++;
				}
				else {
					timeIndex = 0;
					pitchIndex++;
				}				
				return true;
			}
			else
				return false;
					
		}
	
		public boolean isNextTime() {
		    return (timeIndex < (timeRange-1));	
		}
			
		public boolean isNextPitch() {
			return (pitchIndex < (pitchRange-1));		
		}
			
		public boolean isNext() {
			return (index < (matrixSize-1));		
		}
	
		public boolean isLastTime() {
		    return (timeIndex == (timeRange-1));	
		}
		
		public boolean isLastPitch() {
			return (pitchIndex == (pitchRange-1));		
		}
		
		public boolean isLast() {
			return (index == (matrixSize-1));		
		}
	
		public ToneMapElement readElementAt(double time, double pitch) {
			return element;
		}
	
		public boolean prevTime() {
			if (timeIndex > 0){
				timeIndex--;
				index--;
				return true;
			}
			else
				return false;
				
		}
	
		public boolean prevPitch() {
			if (pitchIndex > 0){
				pitchIndex--;
				index = index - timeRange;
				return true;
			}
			else
				return false;
				
		}
	
		public boolean prev() {
			if (index > 0){
				index--;
				if (timeIndex > 0){
					timeIndex--;
				}
				else {
					timeIndex = timeRange-1;
					pitchIndex--;
				}
				return true;
			}
			else
				return false;
		}
							
		public void lastTime() {
		    index = timeRange - timeIndex + index;
			timeIndex = timeRange - 1;
		}
		
		public void lastPitch() {
			index = timeRange*(pitchRange-pitchIndex-2) + timeIndex;
			pitchIndex = pitchRange - 1; // ??
		}
		
		public void last() {
			index = (timeRange*pitchRange) - 1;
			timeIndex = timeRange - 1;
			pitchIndex = pitchRange - 1;
		}
	
		public void firstTime() {
			index = index - timeIndex;
			timeIndex = 0;
		}
		
		public void firstPitch() {
			index = timeIndex;
			pitchIndex = 0;
		}
		
		public void first() {
			timeIndex = 0;
			pitchIndex = 0;
			index = 0;			
		}
		
		
		public int getTimeIndex() {
			return timeIndex;			
		}
	
		public int getPitchIndex() {
			return pitchIndex;
		}
	
		public int getIndex() {
			return index;			
		}
	
		public void setTimeIndex(int index) {
		    this.index = this.index + (index - timeIndex);
			timeIndex = index; 
		}
		
		public void setPitchIndex(int index) {
		    this.index = this.index + (timeRange * (index - pitchIndex));
			pitchIndex = index; 
		}
		
		public void setIndex(int index) {
			this.index = index;
			pitchIndex = index/timeRange;
			timeIndex = index%timeRange;
			
		}
		
		private int timeIndex;
		private int pitchIndex;
		private int index;
		
	} // End Iterator
	
	private ToneMapElement[] matrix;
	private ToneMapElement toneMapElement;
	private TimeSet timeSet;
	private PitchSet pitchSet;
	private int timeRange;
	private int pitchRange;
	private double minAmplitude;
	private double maxAmplitude;
	private double minFTPower;
	private double maxFTPower;
	private double avgAmplitude;
	private double avgFTPower;
	private int matrixSize;
	private ToneMapElement element;
	
} // End ToneMapMatrix