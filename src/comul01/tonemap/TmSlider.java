package comul01.tonemap;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.font.*;
import java.text.*;

/**
 * Insert the type's description here.
 * Creation date: (12/06/2001 07:13:44)
 * @author: 
 */
class TmSlider extends JSlider implements ChangeListener {

		
	public TmSlider(int orient, int min, int max, 
						int value, String name, 
						ChangeListener listener) {
		super(orient, min, max, value);
		TitledBorder tb = new TitledBorder(new EtchedBorder());
		tb.setTitle(name + "=" + value);
		setBorder(tb);
		setPreferredSize(new Dimension(200,60));
		setMaximumSize(new Dimension(200,60));
		setMajorTickSpacing(max-min);
		setPaintLabels(true);
		this.name = name;
		this.value = value;
		this.listener = listener;
		addChangeListener(this);
							
	}

	public void showValue(int value) {
		
		TitledBorder tb = (TitledBorder)getBorder();
		String s = tb.getTitle();
		tb.setTitle(s.substring(0, s.indexOf('=')+1) + s.valueOf(value));
	}

	public void stateChanged(ChangeEvent e) {
   	
	 	value = getValue();
		TitledBorder tb = (TitledBorder)getBorder();
		String s = tb.getTitle();
		tb.setTitle(s.substring(0, s.indexOf('=')+1) + s.valueOf(value));
		listener.stateChanged(e);				
		repaint();
		 
	}

	public String getName() {
   	
		return name;
	
	}
	

	private ChangeListener listener;
	private String name;
	private int value;

}