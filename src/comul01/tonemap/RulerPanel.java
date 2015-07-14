package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;

class RulerPanel extends JPanel implements SwingConstants {
	public RulerPanel(int dir, int w, int h,
	  	int lbldist, int lbl, int subs){
		direction = dir;
	  	labelDistance = lbldist;
	  	label = lbl;
	  	subdivisions = subs;
	  	setPreferredSize(new Dimension(w, h));
   	}                                 
	
   	public void paintComponent(Graphics g){
		super.paintComponent(g);
	  	Dimension d = getPreferredSize();
	  	if (direction == HORIZONTAL)
	  	{  	int i = 0;
		 	int x = 0;
		 	if (subdivisions > 0)
		 	{	while (x < d.width)
				{  g.drawLine(x, 0, x, (d.height * 4) / 10);
			   		i++;
			   	x = (i * labelDistance) / subdivisions;
			}
		 }
		 i = 0;
		 x = 0;
		 while (x <= d.width)
		 {  g.drawLine(x, 0, x, (d.height * 8) / 10);
			g.drawString("" + i * label, x + 2,
			   (d.height * 8) / 10);
			i++;
			x = i * labelDistance;
		 }
	  }
	  else
	  {  int i = 0;
		 int y = 0;
		 if (subdivisions > 0)
		 {  while (y <= d.height)
			{  g.drawLine(0, y, (d.width * 4) / 10, y);
			   i++;
			   y = (i * labelDistance) / subdivisions;
			}
		 }
		 i = 0;
		 y = 0;
		 while (y <= d.height)
		 {  g.drawLine(0, y, (d.width * 8) / 10, y);
			g.drawString("" + i * label, 2, y);
			i++;
			y = i * labelDistance;
		 }
	  }
   }                 
   	
	public void setIsMetric(boolean isMetric) {
		this.isMetric = isMetric;
		setIncrementAndUnits();
		repaint();
	}

	private void setIncrementAndUnits() {
		if (isMetric) {
			units = (int)((double)INCH / (double)2.54); // dots per centimeter
			increment = units;
		} else {
			units = INCH;
			increment = units / 2;
		}
	}

	public boolean isMetric() {
		return this.isMetric;
	}

	public int getIncrement() {
		return increment;
	}

	public void setPreferredHeight(int ph) {
		setPreferredSize(new Dimension(SIZE, ph));
	}

	public void setPreferredWidth(int pw) {
		setPreferredSize(new Dimension(pw, SIZE));
	}
 
	public static final int INCH = Toolkit.getDefaultToolkit().
			getScreenResolution();
	public static final int SIZE = 25;

   	private int direction;
   	private int labelDistance;
   	private int subdivisions;
	private int label;

	public int orientation;
	public boolean isMetric;
	private int increment;
	private int units;
	
	
} // End RulerPanel