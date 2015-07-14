package comul01.tonemap;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.font.*;
import java.text.*;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (10/07/2001 08:16:45)
 * @author: 
 */
public class TimeControl extends JPanel implements ToneMapConstants {

		
	public TimeControl(ChangeListener listener) {

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.listener = listener;
		timeStartSlider = new TmSlider(JSlider.HORIZONTAL, timeMin, timeMax, timeStart, 
								"Start Time ms.", new TimeSliderListener());
		timeEndSlider = new TmSlider(JSlider.HORIZONTAL, timeMin, timeMax, timeEnd,
							"End Time ms.", new TimeSliderListener());
			
		add(timeStartSlider);
		add(timeEndSlider);
					
	}

	class TimeSliderListener implements ChangeListener {
		
		public void stateChanged(ChangeEvent e) {

			TmSlider slider = (TmSlider) e.getSource();
		 	int value = slider.getValue();
		 	String name = slider.getName();
			if (name.startsWith("Start")) {
			   timeStart = value;
			   if (timeStart > timeEnd) timeEndSlider.setValue(timeStart);
			} else if (name.startsWith("End")) {
			   timeEnd = value;
			   if (timeStart > timeEnd) timeStartSlider.setValue(timeEnd);
			}
			listener.stateChanged(e);
			
		}
	}

	public int getTimeStart(){
		return timeStart;
	}
	
	public int getTimeEnd() {
		return timeEnd;
	}

	public void setTimeMax(int max) {
		if (max > INIT_TIME_MAX) {
			timeMax = INIT_TIME_MAX;
		} else {
			timeMax = max;
		}
	
		timeStartSlider.setPaintLabels(false);
		timeStartSlider.setMaximum(timeMax);
		timeStartSlider.setMinimum(timeMin);
		timeStartSlider.setMajorTickSpacing(timeMax-timeMin);
		timeStartSlider.setPaintLabels(true);
		labelTable = timeStartSlider.createStandardLabels(timeMax-timeMin);
		timeStartSlider.setLabelTable(labelTable);
		timeStartSlider.setValue(timeMin);
		timeStartSlider.repaint();
	
		timeEndSlider.setPaintLabels(false);
		timeEndSlider.setMaximum(timeMax);
		timeEndSlider.setMinimum(timeMin);
		timeEndSlider.setMajorTickSpacing(timeMax-timeMin);
		timeEndSlider.setPaintLabels(true);
		labelTable = timeEndSlider.createStandardLabels(timeMax-timeMin);
		timeEndSlider.setLabelTable(labelTable);
		timeEndSlider.setValue(timeMax);
		timeEndSlider.repaint();
		
	}
		
	private int timeStart = INIT_TIME_START;
	private int timeEnd = INIT_TIME_END;
	private int	timeMin = INIT_TIME_MIN;
	private int timeMax = INIT_TIME_MAX;
	private int timeInc = INIT_TIME_INC;
	private ChangeListener listener;
	private Hashtable labelTable;
	private TmSlider timeStartSlider, timeEndSlider;
	
}