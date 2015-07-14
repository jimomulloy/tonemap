
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

import java.awt.font.*;
import java.text.*;


/**
  * This is the Control Panel for the Tone Mapping Functions of ToneMap
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class ToneMapPanel extends JPanel implements ProgressListener,
						ToneMapConstants {

	class ScaleControl extends JPanel{

		ScaleControl(){
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			timeScaleSlider = new TmSlider(JSlider.HORIZONTAL, 1, 100, INIT_TIME_SCALE, 
									"Time Scale", new ScaleSliderListener());
			pitchScaleSlider = new TmSlider(JSlider.HORIZONTAL, 1, 100, INIT_PITCH_SCALE,
									"Pitch Scale", new ScaleSliderListener());
			add(timeScaleSlider);
			add(pitchScaleSlider);
		}

		class ScaleSliderListener implements ChangeListener {

			public void stateChanged(ChangeEvent e) {
			
   			    TmSlider slider = (TmSlider) e.getSource();
  				int value = slider.getValue();
  				String name = slider.getName();
				if (name.startsWith("Time")) {
		 		   timeScale = value;
		 		} else if (name.startsWith("Pitch")) {
		 		   pitchScale = value;
				}
				toneMapView.coordinate();
			}
			
		}
	}

	class ThreshholdControl extends JPanel{

		ThreshholdControl(){
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			lowThreshholdSlider = new TmSlider(JSlider.HORIZONTAL, 0, 100, INIT_LOW_THRESHHOLD, 
									"Low", new ThreshholdSliderListener());
			highThreshholdSlider = new TmSlider(JSlider.HORIZONTAL, 0, 100, INIT_HIGH_THRESHHOLD,
									"High", new ThreshholdSliderListener());
			add(lowThreshholdSlider);
			add(highThreshholdSlider);
		}

		class ThreshholdSliderListener implements ChangeListener {

			public void stateChanged(ChangeEvent e) {
				
   	
   			    JSlider slider = (JSlider) e.getSource();
   			    if (true){
	   			    
	  				int value = slider.getValue();
					TitledBorder tb = (TitledBorder) slider.getBorder();
					String s = tb.getTitle();
					tb.setTitle(s.substring(0, s.indexOf('=')+1) + s.valueOf(value));
					if (s.startsWith("Low")) {
			 		   lowThreshhold = value;
					} else if (s.startsWith("High")) {
			 		   highThreshhold = value;
					} 
					toneMapView.coordinate();
			 	    slider.repaint();
					
   			    }
			}
			
		}
	}

	
		
	class ActionControl extends JPanel{

		ActionControl(){
			
			clearB = new JButton("Clear");
			clearB.setEnabled(true);
			add(clearB);
			
			loadB = new JButton("Load");
			loadB.setEnabled(true);
			add(loadB);
		
			processB = new JButton("Process");
			processB.setEnabled(false);
			add(processB);

			clearB.addActionListener(new ClearBAction());
			loadB.addActionListener(new LoadBAction());
			processB.addActionListener(new ProcessBAction());

					
		}

		class LoadBAction implements ActionListener {
		
			public void actionPerformed(ActionEvent evt) {
				
				if (timer.isRunning()) timer.restart();
				else timer.start();	
				timer.start();
				loadB.setEnabled(false);
				processB.setEnabled(false);
				final SwingWorker worker = new SwingWorker() {
					public Object construct() {
						toneMap.loadAudio();
						return this;
					}

					public void finished() {
						if (timer.isRunning()) timer.stop();
						Toolkit.getDefaultToolkit().beep();
						setProgress(0);
						progressBar.setValue(progressBar.getMinimum());
						loadB.setEnabled(true);
						processB.setEnabled(true);
					}
					
				};
			
				worker.start();
			}

		}
		
	
	class ProcessBAction implements ActionListener {
		
			public void actionPerformed(ActionEvent evt) {
				
				if (timer.isRunning()) timer.restart();
				else timer.start();	
				timer.start();
				loadB.setEnabled(false);
				processB.setEnabled(false);
				final SwingWorker worker = new SwingWorker() {
					public Object construct() {
						toneMap.process();
						return this;
					}

					public void finished() {
						if (timer.isRunning()) timer.stop();
						Toolkit.getDefaultToolkit().beep();
						setProgress(0);
						progressBar.setValue(progressBar.getMinimum());
						loadB.setEnabled(true);
						processB.setEnabled(true);
					}
					
				};
			
				worker.start();
	   
			}

		}
	

	class ClearBAction implements ActionListener {
		
			public void actionPerformed(ActionEvent evt) {
				
				if (timer.isRunning()) timer.restart();
				else timer.start();	
				timer.start();
				clearB.setEnabled(false);
				loadB.setEnabled(false);
				processB.setEnabled(false);
				final SwingWorker worker = new SwingWorker() {
					public Object construct() {
						toneMap.clear();
						return this;
					}

					public void finished() {
						if (timer.isRunning()) timer.stop();
						Toolkit.getDefaultToolkit().beep();
						progressBar.setValue(progressBar.getMinimum());
						clearB.setEnabled(true);
					}
					
				};
			
				worker.start();
	   
			}

		}
	}

		
	class ViewControl extends JPanel{

		ViewControl(){
			
			
		    audioViewB = new JRadioButton("Power");
			audioViewB.setActionCommand("Power");
			audioViewB.setSelected(false);

		    preViewB = new JRadioButton("PreAmp");
			preViewB.setActionCommand("Pre");
			preViewB.setSelected(true);
		   
			postViewB = new JRadioButton("PostAmp");
			postViewB.setActionCommand("Post");
			postViewB.setSelected(false);
		    
			midiViewB = new JRadioButton("Note");
			midiViewB.setActionCommand("Note");
			midiViewB.setSelected(false);

			ButtonGroup group = new ButtonGroup();
			
			group.add(preViewB);
			group.add(postViewB);
			group.add(midiViewB);
			group.add(audioViewB);
	
			ViewControlListener viewControlListener = new ViewControlListener();
   			audioViewB.addActionListener(viewControlListener);
   			preViewB.addActionListener(viewControlListener);
   			postViewB.addActionListener(viewControlListener);
   			midiViewB.addActionListener(viewControlListener);

		 	JPanel radioPanel = new JPanel();
	        radioPanel.setLayout(new GridLayout(1, 0));
		
	   		radioPanel.add(preViewB);
			radioPanel.add(postViewB);
			radioPanel.add(midiViewB);
			radioPanel.add(audioViewB);
		
			add(radioPanel);
   
		}
			 
		class ViewControlListener implements ActionListener{
   			
			public void actionPerformed(ActionEvent e) {
  			   
  				String s = e.getActionCommand();
  			    if (s.startsWith("Power")) {
			 	  	viewMode = VIEW_MODE_AUDIO;
				} else if (s.startsWith("Pre")) {
			 	   	viewMode = VIEW_MODE_PRE;
				} else if (s.startsWith("Post")) {
			 	   	viewMode = VIEW_MODE_POST;
				} else if (s.startsWith("Note")) {
			 	   	viewMode = VIEW_MODE_NOTE;
				}
				toneMapView.coordinate();
			}
		}
					
	}


	class ToneMapViewer extends JPanel{

		public ToneMapViewer(){
	    	
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			
			toneMapView = new ToneMapView();
			
			viewScrollPane = new JScrollPane(toneMapView,
								ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
								ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			viewWidth = screenSize.width-150;
			viewHeight = screenSize.height-350;
								
			viewScrollPane.setPreferredSize(new Dimension(viewWidth, viewHeight));
			
			viewScrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));			

			viewScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, 
									new Corner());
   		    viewScrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER,
	 	                               new Corner());
	  		viewScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
									new Corner());
	  			
			timeRule = new TimeRuler(toneMapView.getPreferredSize().width, 25);
			
			viewScrollPane.setColumnHeaderView(timeRule);
	
			pitchRule = new PitchRuler(25, toneMapView.getPreferredSize().height);
			
			viewScrollPane.setRowHeaderView(pitchRule);
		
			add(viewScrollPane);
			
			toneMapView.coordinate();
			
		}

		class UnitsListener implements ItemListener {
  	    	public void itemStateChanged(ItemEvent e) {
  	    	}
		  	  
		}

		
		class Corner extends JComponent {
  			public void paintComponent(Graphics g) {
	 		   	g.setColor(new Color(155,155,155));
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		}
   
	}

	public JButton loadB, processB, clearB;
	
	private ToneMapView toneMapView;
	private ToneMap toneMap;
	private ToneMapMatrix toneMapMatrix;
	private ToneMapMatrix.Iterator mapIterator;

	private ScaleControl scaleControl;
	private ThreshholdControl threshholdControl;
	private ActionControl actionControl;
	
	private	TmSlider timeScaleSlider;
	private	TmSlider pitchScaleSlider;
	private	TmSlider lowThreshholdSlider;
	private	TmSlider highThreshholdSlider;
	
	private JScrollPane viewScrollPane;
	private JPanel viewPanel;
	private TimeRuler timeRule;
	private PitchRuler pitchRule;
	private JToggleButton viewToggle;

	private JProgressBar progressBar;

	
	private JRadioButton audioViewB, preViewB, postViewB, midiViewB;

	private int timeScale=INIT_TIME_SCALE;
	private int pitchScale=INIT_PITCH_SCALE;
	private int lowThreshhold=INIT_LOW_THRESHHOLD;
	private int highThreshhold=INIT_HIGH_THRESHHOLD;

	private int[] timeCoords;
	private int[] pitchCoords;

	private int timeRange;
	private int pitchRange;
	private int progressValue;

	private int viewMode = VIEW_MODE_PRE;

	private int viewWidth, viewHeight;

	private Timer timer;

	private NoteListElement noteListElement;
	
	class ToneMapView extends JPanel implements Scrollable {
		
		ToneMapView () {
			
		  	setPreferredSize(new Dimension(viewWidth, viewHeight));
		
			addMouseListener( new viewMouseListener() );
			addMouseMotionListener ( new viewMouseMotionListener());
			
			setBackground(Color.black);

		}
		
		public void coordinate() {
		
			Dimension size = new Dimension(viewWidth, viewHeight);
		  	maxWidth = (size.width * 100/timeScale)-45;
			maxHeight = (size.height *100/pitchScale)-45;
			
			size = new Dimension(maxWidth, maxHeight);
			setBackground(Color.black);	
			setPreferredSize(new Dimension(maxWidth, maxHeight));
		
			timeRule.setPreferredSize(new Dimension(maxWidth, 25));
			pitchRule.setPreferredSize(new Dimension(25, maxHeight));
			
			
			if (toneMapMatrix != null) {
				
				timeStep = (int)Math.ceil((double)maxWidth/(double)timeRange);
				pitchStep = (int)Math.ceil((double)maxHeight/(double)pitchRange);
					
				for (int i=0; i < timeCoords.length; i++){
					timeCoords[i] = (int)Math.ceil(maxWidth*i/timeRange);
				
				
				}
			
				for (int i=0; i < pitchCoords.length; i++){
					pitchCoords[i] = maxHeight - (int)(Math.ceil(maxHeight*i/pitchRange));
						
				}
				
				timeRule.setLimits(0,
						toneMap.getTimeSet().getEndTime() - toneMap.getTimeSet().getStartTime()); 
		
				pitchRule.setLimits(toneMap.getPitchSet().getLowNote(), 
						toneMap.getPitchSet().getHighNote());
								
			}

			timeRule.revalidate();
			timeRule.repaint();

			pitchRule.revalidate();
			pitchRule.repaint();
			
			revalidate();
			repaint();
		}
		
		public void paintComponent(Graphics g) { 
			super.paintComponent(g);
			if (toneMapMatrix != null) {

				mapIterator = toneMapMatrix.newIterator();
			
				int x, y, xa1, xa2, ya1, ya2;
				double lowT, highT, ampT;
				lowT = (double)lowThreshhold/100.0;
				highT = (double)highThreshhold/100.0;
				mapIterator.firstPitch();
		
				do {
					
					mapIterator.firstTime();
			
					do {
						
						toneMapElement = mapIterator.getElement();
						if (toneMapElement != null) {
							if (viewMode == VIEW_MODE_POST) {
								amplitude = toneMapElement.postAmplitude;
							} else { 
								if (viewMode == VIEW_MODE_PRE) {
									amplitude = toneMapElement.preAmplitude;
								} else {
									amplitude = 100*toneMapElement.preFTPower/toneMapMatrix.getMaxFTPower();
								}
							}
							x = mapIterator.getTimeIndex();
							y = mapIterator.getPitchIndex();
							
							noteListElement = toneMapElement.noteListElement;
							
							if (amplitude == -1 ) {
								g.setColor(new Color(155,155,155));
							} else if (amplitude < lowT ) {
								g.setColor(Color.black);		
							} else if (viewMode == VIEW_MODE_NOTE) {
								if (noteListElement != null) {
									if(noteListElement.underTone) {
										g.setColor(Color.green);
									} else {
										g.setColor(Color.white);
									}
								} else {
									g.setColor(Color.black);
								}
							} else if (amplitude > highT ) {
								g.setColor(Color.red);
							} else {
								ampT = (amplitude - lowT)/(highT - lowT);	
								g.setColor(new Color((int)(255*ampT),0,(int)(255*(1-ampT))));
							}
				
						 	g.fillRect(timeCoords[x], pitchCoords[y]-pitchStep,
						 				timeStep,pitchStep);
						 	g.drawRect(timeCoords[x], pitchCoords[y]-pitchStep,
						 				timeStep,pitchStep);
						}
							
					} while ( mapIterator.nextTime());

				} while ( mapIterator.nextPitch());

			}
		}
		
		class viewMouseListener extends MouseAdapter {
			public void mousePressed(MouseEvent evt) {
				int x = evt.getX();
				int y = evt.getY();
			}
			
			public void mouseClicked(MouseEvent evt) {
				int x = evt.getX();
				int y = evt.getY();
			}
		} // End viewMouseListener
		
		class viewMouseMotionListener implements MouseMotionListener{
			public void mouseMoved(MouseEvent evt) {
				int x = evt.getX();
				int y = evt.getY();
			}
			
			public void mouseDragged(MouseEvent evt) {
				int x = evt.getX();
				int y = evt.getY();
			}
			
		} // End viewMouseMotionListener
		
		
	    private int maxUnitIncrement = 1;
		
		
	    public Dimension getPreferredScrollableViewportSize() {
	        return getPreferredSize();
	    }
		
	    public int getScrollableUnitIncrement(Rectangle visibleRect,
	                                          int orientation,
	                                          int direction) {
	        //Get the current position.
	        int currentPosition = 0;
	        if (orientation == SwingConstants.HORIZONTAL)
	            currentPosition = visibleRect.x;
	        else
	            currentPosition = visibleRect.y;
		
	        //Return the number of pixels between currentPosition
	        //and the nearest tick mark in the indicated direction.
	        if (direction < 0) {
	            int newPosition = currentPosition - 
	                             (currentPosition / maxUnitIncrement) *
	                              maxUnitIncrement;
	            return (newPosition == 0) ? maxUnitIncrement : newPosition;
	        } else {
	            return ((currentPosition / maxUnitIncrement) + 1) *
	                   maxUnitIncrement - currentPosition;
	        }
	    }
		
	    public int getScrollableBlockIncrement(Rectangle visibleRect,
	                                           int orientation,
	                                           int direction) {
	        if (orientation == SwingConstants.HORIZONTAL)
	            return visibleRect.width - maxUnitIncrement;
	        else
	            return visibleRect.height - maxUnitIncrement;
	    }
	
	    public boolean getScrollableTracksViewportWidth() {
	        return false;
	    }
	
	    public boolean getScrollableTracksViewportHeight() {
	        return false;
	    }
	
	    public void setMaxUnitIncrement(int pixels) {
	        maxUnitIncrement = pixels;
	    }
	
		
		private int timeStep;
		private int pitchStep;
		
		private int maxWidth;
		private int maxHeight;
		private double amplitude;
		private ToneMapElement toneMapElement;
		
			
	} // End ToneMapView
		
	public ToneMapPanel(ToneMap toneMap) {
		
		this.toneMap = toneMap;

		EmptyBorder eb = new EmptyBorder(5,5,5,5);
		BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
		CompoundBorder cb = new CompoundBorder(eb,bb);
		setBorder(new CompoundBorder(cb,eb));

		JPanel p0 = new JPanel();
		p0.setLayout(new BoxLayout(p0, BoxLayout.Y_AXIS));
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(1,2));
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		timer = new Timer(ONE_SECOND, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
			
				progressBar.setValue(progressValue);
			}
		});

		p2.add(new ScaleControl());
		p2.add(new ThreshholdControl());
		p3.add(new ActionControl());
		p3.add(progressBar);
		p3.add(new ViewControl());
		p1.add(new ToneMapViewer());

		p0.add(p1);
		p0.add(p2);
		p0.add(p3);
		
		add(p0); 

		toneMapView.coordinate();

	}
	public void init(){
		
		toneMapMatrix = toneMap.getMatrix();
			
			if (toneMapMatrix != null) {
				
				timeRange = toneMap.getTimeSet().getRange();
				pitchRange = toneMap.getPitchSet().getRange();
				timeCoords = new int[timeRange];
				pitchCoords = new int[pitchRange];
			
			}
		
		toneMapView.coordinate();
		clearB.setEnabled(true);
		loadB.setEnabled(true);
		processB.setEnabled(false);
	}
	public void setProgress(int progressValue) {
		this.progressValue = progressValue;
		
	}
} // End ToneMapPanel