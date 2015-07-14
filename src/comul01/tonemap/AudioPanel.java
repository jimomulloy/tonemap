package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.geom.Line2D;
import javax.swing.event.*;
import java.awt.font.*;
import java.text.*;


/**
  * This is a class that encapsulates the Audio properties associated with the ToneMap 
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class AudioPanel extends JPanel implements ToneMapConstants{

	

	class TimeControlListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
				
   		   audioModel.timeStart = (double)timeControl.getTimeStart();
	 	   audioModel.timeEnd = (double)timeControl.getTimeEnd();
	 	  			
		}

	}

	
	class PitchControlListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
				
   		   audioModel.pitchLow = pitchControl.getPitchLow();
	 	   audioModel.pitchHigh = pitchControl.getPitchHigh();
			
		}

	}



	class SampleSizeControl extends JPanel {
		
		public SampleSizeControl() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
			sampleSizeSlider = new TmSlider(JSlider.HORIZONTAL, MIN_SAMPLE_SIZE, MAX_SAMPLE_SIZE, INIT_SAMPLE_SIZE,
									"SampleSize ms.", new SampleSizeListener());
			add(sampleSizeSlider);
			
		}
		
		class SampleSizeListener implements ChangeListener {

			public void stateChanged(ChangeEvent e) {

				JSlider slider = (JSlider) e.getSource();
	  			int value = slider.getValue();
				audioModel.sampleTimeSize = (double)value;
			}
			
		}

	}

		

	class ResolutionControl extends JPanel {
		
		public ResolutionControl() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
			resolutionSlider = new TmSlider(JSlider.HORIZONTAL, 1, 100, 1,
									"Sample Resolution", new ResolutionListener());
			add(resolutionSlider);
			
		}
		
		class ResolutionListener implements ChangeListener {

			public void stateChanged(ChangeEvent e) {

				JSlider slider = (JSlider) e.getSource();
	  			int value = slider.getValue();
				audioModel.resolution = value;
			}
			
		}

	}

	
	class PFactorControl extends JPanel {
		
		public PFactorControl() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
			pFactorSlider = new TmSlider(JSlider.HORIZONTAL, 1, 100, 60,
									"Pitch Factor", new PFactorListener());
			add(pFactorSlider);
			
		}
		
		class PFactorListener implements ChangeListener {

			public void stateChanged(ChangeEvent e) {

				JSlider slider = (JSlider) e.getSource();
	  			int value = slider.getValue();
				audioModel.pFactor = value;
			}
			
		}

	}

	class TFactorControl extends JPanel {
		
		public TFactorControl() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
			tFactorSlider = new TmSlider(JSlider.HORIZONTAL, 1, 100, 10,
									"Time Factor", new TFactorListener());
			add(tFactorSlider);
			
		}
		
		class TFactorListener implements ChangeListener {

			public void stateChanged(ChangeEvent e) {

				JSlider slider = (JSlider) e.getSource();
	  			int value = slider.getValue();
				audioModel.tFactor = value;
			}
			
		}

	}

	class GainControl extends JPanel {

		
		public GainControl() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
			gainSlider = new TmSlider(JSlider.HORIZONTAL, 0, 100, INIT_VOLUME_SETTING, 
									"Gain",new GainSliderListener());
			add(gainSlider);
			
		}

		class GainSliderListener implements ChangeListener {

			public void stateChanged(ChangeEvent e) {
   	
   			    TmSlider slider = (TmSlider) e.getSource();
	  			int value = slider.getValue();
				audioModel.gainSetting = value;
			}
		}

	}


	class PanControl extends JPanel {

		
		public PanControl() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			panSlider = new TmSlider(JSlider.HORIZONTAL, 0, 100, INIT_PAN_SETTING, 
									"Pan",new PanSliderListener());
			add(panSlider);			
		}

		class PanSliderListener implements ChangeListener {

			public void stateChanged(ChangeEvent e) {
   					
   			    TmSlider slider = (TmSlider)e.getSource();
	  			int value = slider.getValue();
				String name = slider.getName();
			 	audioModel.panSetting = value;
			}

		}

	}

	public boolean openFile() {
				
		try {
	       	File file = new File(System.getProperty("user.dir"));
	       	JFileChooser fc = new JFileChooser(file);
	   		fc.setFileFilter(new javax.swing.filechooser.FileFilter () {
	 	        	public boolean accept(File f) {
	   	            	if (f.isDirectory()) {
	   	                	return true;
	   	             	}
	       	   		    String name = f.getName();
	       	  			if (name.endsWith(".au") || name.endsWith(".wav") || name.endsWith(".aiff") || name.endsWith(".aif")) {
	       	 		        return true;
	       				}
	       			        return false;
	       				}
	          	 	    public String getDescription() {
	              	        	return ".au, .wav, .aif";
	           	    	}
	       			});
	
	            	if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	                	file = fc.getSelectedFile();
	                	if (!audioModel.load(file)) return false;
	 	                			
						fileNameField.setText(audioModel.getFileName());
						durationField.setText(String.valueOf(audioModel.getDuration()));
						sampleRateField.setText(String.valueOf(audioModel.getSampleRate()));
						bitSizeField.setText(String.valueOf(audioModel.getSampleBitSize()));
						channelsField.setText(String.valueOf(audioModel.getNumChannels()));
						timeControl.setTimeMax((int)(audioModel.getDuration()*1000));
						return true;
							
					} else {
						return false;
					}
	  	        } catch (SecurityException ex) { 
	    	        ex.printStackTrace();
	    	        return false;
	  	  	  	} catch (Exception ex) { 
	       	    	ex.printStackTrace();
	       	    	return false;
	        	}
			}	
	
	class FileControl extends JPanel {

		
		public FileControl() {

			TitledBorder tb = new TitledBorder(new EtchedBorder());
			tb.setTitle("File"); 
			setBorder(tb);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setPreferredSize(new Dimension(200, 150));
			JPanel bp = new JPanel();
			JPanel ip = new JPanel();
			ip.setLayout(new BoxLayout(ip, BoxLayout.X_AXIS));
			JPanel ipHeadings = new JPanel();
			ipHeadings.setLayout(new GridLayout(5,1));
			JPanel ipFields = new JPanel();
			ipFields.setLayout(new GridLayout(5,1));  
			
			openB = new JButton("Open");
			openB.setEnabled(true);
			bp.add(openB);
		
			saveB = new JButton("Save");
			saveB.setEnabled(false);
			bp.add(saveB);
					
			openB.addActionListener(new OpenBAction());
			saveB.addActionListener(new SaveBAction());

			JLabel fileNameHeading = new JLabel("Name:", JLabel.LEFT);
			JLabel durationHeading = new JLabel("Duration:", JLabel.LEFT);
			JLabel sampleRateHeading = new JLabel("SampleRate:", JLabel.LEFT);
			JLabel bitSizeHeading = new JLabel("Sample Bit Size:", JLabel.LEFT);
			JLabel channelsHeading = new JLabel("Channels:", JLabel.LEFT);
			
			fileNameField = new JLabel();
			fileNameField.setHorizontalAlignment(JLabel.RIGHT);
			durationField = new JLabel();
			durationField.setHorizontalAlignment(JLabel.RIGHT);
			sampleRateField = new JLabel();
			sampleRateField.setHorizontalAlignment(JLabel.RIGHT);
			bitSizeField = new JLabel();
			bitSizeField.setHorizontalAlignment(JLabel.RIGHT);
			channelsField = new JLabel();
			channelsField.setHorizontalAlignment(JLabel.RIGHT);

			ipHeadings.add(fileNameHeading);
			ipHeadings.add(durationHeading);
			ipHeadings.add(sampleRateHeading);
			ipHeadings.add(bitSizeHeading);
			ipHeadings.add(channelsHeading);
		
			ipFields.add(fileNameField);
			ipFields.add(durationField);
			ipFields.add(sampleRateField);
			ipFields.add(bitSizeField);
			ipFields.add(channelsField);
			
			ip.add(ipHeadings);
			ip.add(ipFields);

			add(bp, BorderLayout.NORTH);
			add(ip, BorderLayout.SOUTH);
					
		}

		class OpenBAction implements ActionListener {
		
			public void actionPerformed(ActionEvent evt) {
				
				try {
	 	        	File file = new File(System.getProperty("user.dir"));
	  	        	JFileChooser fc = new JFileChooser(file);
	   	       		fc.setFileFilter(new javax.swing.filechooser.FileFilter () {
	    	        	public boolean accept(File f) {
	     	            	if (f.isDirectory()) {
	      	                	return true;
	       	             	}
	        	   		    String name = f.getName();
	         	  			if (name.endsWith(".au") || name.endsWith(".wav") || name.endsWith(".aiff") || name.endsWith(".aif")) {
	          	 		        return true;
	           				}
	           			        return false;
	           				}
	            	 	    public String getDescription() {
	               	        	return ".au, .wav, .aif";
	               	    	}
	           			});
	
		            	if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	 	                	file = fc.getSelectedFile();
	 	                	audioModel.load(file);
	 	                			
							fileNameField.setText(audioModel.getFileName());
							durationField.setText(String.valueOf(audioModel.getDuration()));
							sampleRateField.setText(String.valueOf(audioModel.getSampleRate()));
							bitSizeField.setText(String.valueOf(audioModel.getSampleBitSize()));
							channelsField.setText(String.valueOf(audioModel.getNumChannels()));
							timeControl.setTimeMax((int)(audioModel.getDuration()*1000));
							
						}
	   	        } catch (SecurityException ex) { 
	    	        ex.printStackTrace();
	  	  	  	} catch (Exception ex) { 
	       	    	ex.printStackTrace();
	        	}
			}	
	    
		}

		
		class SaveBAction implements ActionListener {
		
			public void actionPerformed(ActionEvent evt) {
				System.out.println("File Save");
			} 
		}
	}

	
	class TransControl extends JPanel {

		
		public TransControl() {

			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			TitledBorder tb = new TitledBorder(new EtchedBorder());
			tb.setTitle("Transform"); 
			setBorder(tb);
			
			setPreferredSize(new Dimension(200, 150));
			
			JPanel cp = new JPanel();
			cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
			
			javaB = new JRadioButton("Java");
			javaB.setActionCommand("Java");
			javaB.setSelected(true);

		    jniB = new JRadioButton("JNI");
			jniB.setActionCommand("JNI");
			jniB.setEnabled(false);
			jniB.setSelected(false);

			ButtonGroup group = new ButtonGroup();
			group.add(javaB);
			group.add(jniB);

			TransformBListener transformBListener = new TransformBListener();
   			javaB.addActionListener(transformBListener);
   			jniB.addActionListener(transformBListener);
   			
		 	JPanel radioPanel = new JPanel();
	        radioPanel.setLayout(new GridLayout(1, 0));
			radioPanel.add(javaB);
	   		radioPanel.add(jniB);

	   		add(cp, BorderLayout.NORTH);
			add(radioPanel, BorderLayout.SOUTH);
			
		}

			 
		class TransformBListener implements ActionListener{
   			
			public void actionPerformed(ActionEvent e) {
  			   
  				String s = e.getActionCommand();
  			    if (s.startsWith("Java")) {
			 	  	audioModel.transformMode = TRANSFORM_MODE_JAVA;
				} else if (s.startsWith("JNI")) {
					audioModel.transformMode = TRANSFORM_MODE_JNI;
				}
			}
		}
			
	}

	
	private AudioModel audioModel;
	
	public TimeControl timeControl;
	public PitchControl pitchControl;
	public SampleSizeControl sampleSizeControl;
	public GainControl gainControl;
	public PanControl panControl;
	public FileControl fileControl;
	public TransControl transControl;
	public ResolutionControl resolutionControl;
	public TFactorControl tFactorControl;
	public PFactorControl pFactorControl;
	public JButton openB, saveB;
	public TmSlider sampleSizeSlider;
	public TmSlider gainSlider;
	public TmSlider panSlider;
	public TmSlider resolutionSlider;
	public TmSlider tFactorSlider;
	public TmSlider pFactorSlider;
	public JRadioButton javaB, jniB;
	public JLabel fileNameField;
	public JLabel durationField;
	public JLabel sampleRateField;
	public JLabel bitSizeField;
	public JLabel channelsField;

	public AudioPanel(AudioModel audioModel) {
	
		this.audioModel = audioModel;
			
		setLayout(new BorderLayout());
		
		EmptyBorder eb = new EmptyBorder(5,5,5,5);
		BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
		CompoundBorder cb = new CompoundBorder(eb,bb);
		setBorder(new CompoundBorder(cb,eb));

		JPanel p0 = new JPanel();
		p0.setLayout(new BoxLayout(p0, BoxLayout.Y_AXIS));
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
		JPanel p3 = new JPanel();
		p3.setLayout(new GridLayout(1, 2));
				
		p1.add(timeControl = new TimeControl(new TimeControlListener()));
		p1.add(pitchControl = new PitchControl(new PitchControlListener()));
		p2.add(gainControl = new GainControl());
		p2.add(panControl = new PanControl());
		p2.add(sampleSizeControl = new SampleSizeControl());
		p4.add(resolutionControl = new ResolutionControl());
		p4.add(tFactorControl = new TFactorControl());
		p4.add(pFactorControl = new PFactorControl());
		p3.add(fileControl = new FileControl());
		p3.add(transControl = new TransControl());

		p0.add(p4);
		p0.add(p2);
		p0.add(p1);

		add(p3, BorderLayout.NORTH);
		add(p0, BorderLayout.CENTER);	
	
	}
}// End AudioPanel