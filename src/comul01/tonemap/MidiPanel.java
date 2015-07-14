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
  * This is a class that encapsulates the MIDI properties associated with the ToneMap 
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
	
public class MidiPanel extends JPanel implements ToneMapConstants {

	class TimeControlListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
				
   		   midiModel.timeStart = (double)timeControl.getTimeStart();
	 	   midiModel.timeEnd = (double)timeControl.getTimeEnd();
			
		}

	}

	
	class PitchControlListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
				
   		   midiModel.pitchLow = pitchControl.getPitchLow();
	 	   midiModel.pitchHigh = pitchControl.getPitchHigh();
			
		}

	}

	class QuantizeControl extends JPanel {

		public QuantizeControl() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			TitledBorder tb = new TitledBorder(new EtchedBorder());
			tb.setTitle("Quantize Beat"); 
			p.setBorder(tb);
			
			String s = null;
			JComboBox combo = new JComboBox();
			combo.setPreferredSize(new Dimension(120,25));
			combo.setMaximumSize(new Dimension(120,25));
			combo.addItem("0");
			for (int i = 16; i >= 1; i--) {
				s = "1/"+i;
				combo.addItem(s);
			} 
			combo.addItemListener(new QuantizeBCBListener());
			p.add(combo);
			add(p);
			
			p = new JPanel();
			
			tb = new TitledBorder(new EtchedBorder());
			tb.setTitle("Quantize Duration"); 
			p.setBorder(tb);
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			
			combo = new JComboBox();
			combo.setPreferredSize(new Dimension(120,25));
			combo.setMaximumSize(new Dimension(120,25));
			combo.addItem("0");
			for (int i = 16; i >= 1; i--) {
				s = "1/"+i;
				combo.addItem(s);
			} 
			combo.addItemListener(new QuantizeDCBListener());
			p.add(combo);
			add(p);
				
		}

		class QuantizeBCBListener implements ItemListener {

			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() instanceof JComboBox) {
					JComboBox combo = (JComboBox) e.getSource();
					if (combo.getSelectedIndex() == 0) {
						midiModel.quantizeBeatSetting = 0;
					} else {	
						midiModel.quantizeBeatSetting = 1.0/(double)(combo.getSelectedIndex());
					}
					System.out.println("panel qbs: "+midiModel.quantizeBeatSetting); 
				}
			}

		}

		class QuantizeDCBListener implements ItemListener {


			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() instanceof JComboBox) {
					JComboBox combo = (JComboBox) e.getSource();
					if (combo.getSelectedIndex() == 0) {
						midiModel.quantizeDurationSetting = 0;
					} else {	
						midiModel.quantizeDurationSetting = 1.0/(double)(combo.getSelectedIndex());
					}
					System.out.println("panel qds: "+midiModel.quantizeDurationSetting); 
				}
			}

		}

	}

	
	class BPMControl extends JPanel {

		
		public BPMControl() {
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			bpmSlider = new TmSlider(JSlider.HORIZONTAL, 0, MAX_BPM_SETTING,
									INIT_BPM_SETTING, "BPM",new BPMSliderListener());
			add(bpmSlider);			
		}

		class BPMSliderListener implements ChangeListener {

			public void stateChanged(ChangeEvent e) {

   			    TmSlider slider = (TmSlider)e.getSource();
	  			int value = slider.getValue();
				midiModel.bpmSetting = value;
				
			}

		}

	}




	class ChannelControl extends JPanel {

		
		public ChannelControl() {
		
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
	

			veloS = new TmSlider(JSlider.HORIZONTAL, 0, 128,
	            		INIT_VELOCITY_SETTING, 
						"Velocity",new ControlSliderListener());
			presS = new TmSlider(JSlider.HORIZONTAL, 0, 128,
						INIT_PRESSURE_SETTING, 
						"Pressure",new ControlSliderListener());
			revbS = new TmSlider(JSlider.HORIZONTAL, 0, 128,
						INIT_REVERB_SETTING, 
						"Reverb",new ControlSliderListener());
			bendS = new TmSlider(JSlider.HORIZONTAL, 0, 16384,
						INIT_BEND_SETTING, 
						"Bend",new ControlSliderListener());
			p.add(veloS);
			p.add(presS);
		 	p.add(revbS);
		 	p.add(bendS);
			add(p);

			p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));

			JComboBox combo = new JComboBox();
			combo.setPreferredSize(new Dimension(120,25));
			combo.setMaximumSize(new Dimension(120,25));
			for (int i = 1; i <= 16; i++) {
				combo.addItem("Channel " + String.valueOf(i));
			} 
			combo.addItemListener(new ControlBoxItemListener());
			p.add(combo);
			muteCB = createCheckBox("Mute", p);
			soloCB = createCheckBox("Solo", p);
			monoCB = createCheckBox("Mono", p);
			sustCB = createCheckBox("Sustain", p);

			createButton("Off", p);

			String[] instrumentNames = new String[midiModel.instruments.length];
			for (int i = 0; i < midiModel.instruments.length; i++){
				instrumentNames[i] = midiModel.instruments[i].getName();
			}
			instrumentCB = new JComboBox(instrumentNames);
			instrumentCB.setPreferredSize(new Dimension(120,25));
			instrumentCB.setMaximumSize(new Dimension(120,25));
			instrumentCB.setSelectedIndex(0);
			instrumentCB.addActionListener(new InstrumentCBListener());
			p.add(instrumentCB);		
			
		    add(p);
		}

		public JButton createButton(String name, JPanel p) {
			JButton b = new JButton(name);
			b.addActionListener(new ControlButtonListener());
			p.add(b);
			return b;
		}

		private JCheckBox createCheckBox(String name, JPanel p) {
			JCheckBox cb = new JCheckBox(name);
			cb.addItemListener(new ControlBoxItemListener());
			p.add(cb);
			return cb;
		}

		class ControlSliderListener implements ChangeListener {

			public void stateChanged(ChangeEvent e) {
	        	TmSlider slider = (TmSlider) e.getSource();
 	        	int value = slider.getValue();
   	         	String name = slider.getName();
				if (name.startsWith("Velocity")) {
			 		midiModel.cc.velocity = value;
				} else if (name.startsWith("Pressure")) {
					midiModel.cc.channel.setChannelPressure(midiModel.cc.pressure = value);
				} else if (name.startsWith("Bend")) {
					midiModel.cc.channel.setPitchBend(midiModel.cc.bend = value);
				} else if (name.startsWith("Reverb")) {
			  		midiModel.cc.channel.controlChange(midiModel.REVERB, midiModel.cc.reverb = value);
				}
				slider.repaint();
			}
		}

		class ControlBoxItemListener implements ItemListener {


			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() instanceof JComboBox) {
					JComboBox combo = (JComboBox) e.getSource();
					midiModel.cc = midiModel.channels[combo.getSelectedIndex()];
					midiModel.cc.setComponentStates();            
				} else {
					JCheckBox cb = (JCheckBox) e.getSource();
					String name = cb.getText();
					if (name.startsWith("Mute")) {
						midiModel.cc.channel.setMute(midiModel.cc.mute = cb.isSelected());
					} else if (name.startsWith("Solo")) {
						midiModel.cc.channel.setSolo(midiModel.cc.solo = cb.isSelected());
					} else if (name.startsWith("Mono")) {
						midiModel.cc.channel.setMono(midiModel.cc.mono = cb.isSelected());
					} else if (name.startsWith("Sustain")) {
						midiModel.cc.sustain = cb.isSelected();
						midiModel.cc.channel.controlChange(midiModel.SUSTAIN, midiModel.cc.sustain ? 127 : 0);
					}
				}
			}

		}

		
		class ControlButtonListener implements ActionListener {

	
			public void actionPerformed(ActionEvent e) {
				JButton button = (JButton) e.getSource();
				if (button.getText().startsWith("Off")) {
					for (int i = 0; i < midiModel.channels.length; i++) {
						midiModel.channels[i].channel.allNotesOff();
					}
					
				}
			} 
		}
		
		class InstrumentCBListener implements ActionListener {

			public void actionPerformed(ActionEvent e) {
   			    int value = instrumentCB.getSelectedIndex();
				midiModel.programChange(value);				
			}

		}

	}

	class FileControl extends JPanel {

		
		public FileControl() {

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setPreferredSize(new Dimension(200,150));			
			TitledBorder tb = new TitledBorder(new EtchedBorder());
			tb.setTitle("File"); 
			setBorder(tb);
			
			JPanel bp = new JPanel();
			JPanel ip = new JPanel();
			ip.setLayout(new BoxLayout(ip, BoxLayout.X_AXIS));
			JPanel ipHeadings = new JPanel();
			ipHeadings.setLayout(new GridLayout(5,1));
			JPanel ipFields = new JPanel();
			ipFields.setLayout(new GridLayout(5,1));  
			
			openB = new JButton("Open");
			openB.setEnabled(false);
			bp.add(openB);
		
			saveB = new JButton("Save");
			saveB.setEnabled(true);
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

			add(bp);
			//add(ip);
					
		}

		class OpenBAction implements ActionListener {
		
			public void actionPerformed(ActionEvent evt) {
				System.out.println("File load");

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
	 	                	//load();
	  	         		}
	   	        } catch (SecurityException ex) { 
	    	        //JavaSound.showInfoDialog();
	     	    	 ex.printStackTrace();
	  	  	  	} catch (Exception ex) { 
	       	    	ex.printStackTrace();
	        	}
			}	
	    
		}
	
		class SaveBAction implements ActionListener {
		
			public void actionPerformed(ActionEvent evt) {
				if (!save()) System.out.println("report error");
			} 
		}
	}

	
	class TransControl extends JPanel {

		
		public TransControl() {
		
			TitledBorder tb = new TitledBorder(new EtchedBorder());
			tb.setTitle("Transform"); 
			setBorder(tb);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setPreferredSize(new Dimension(200,150));			
			
		}
			
	}

	
  		
	private MidiModel midiModel;
		
	public TimeControl timeControl;
	public PitchControl pitchControl;
	public FileControl fileControl;
	public TransControl transControl;
	public ChannelControl channelControl;
	public BPMControl bpmControl;
	public QuantizeControl quantizeControl;
	public JButton openB, saveB;
	public TmSlider timeStartSlider;
	public TmSlider timeEndSlider;
	public TmSlider timeIncSlider;
	public TmSlider pitchLowSlider;
	public TmSlider pitchHighSlider;
	public TmSlider volumeSlider;
	public TmSlider panSlider;
	public TmSlider bpmSlider;
	public TmSlider quantizeSlider;
	public JComboBox instrumentCB;
	public TmSlider veloS, presS, bendS, revbS;
	public JCheckBox soloCB, monoCB, muteCB, sustCB; 
	public JLabel fileNameField;
	public JLabel durationField;
	public JLabel sampleRateField;
	public JLabel bitSizeField;
	public JLabel channelsField;


	public MidiPanel(MidiModel midiModel) {

		this.midiModel = midiModel;
		
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
	
		JPanel p3 = new JPanel();
		p3.setLayout(new GridLayout(1, 2));
		
		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));

		JPanel p5 = new JPanel();
		p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));

		p1.add(timeControl = new TimeControl(new TimeControlListener()));
		p1.add(pitchControl = new PitchControl(new PitchControlListener()));
		p5.add(bpmControl = new BPMControl());
		p3.add(fileControl = new FileControl());
		p3.add(transControl = new TransControl());
		p4.add(channelControl = new ChannelControl());
		p5.add(quantizeControl = new QuantizeControl());


		p0.add(p3);
		p0.add(p4);
		p0.add(p5);
  		p0.add(p1);

		add(p3, BorderLayout.NORTH);
		add(p0, BorderLayout.CENTER);	

	}
	public boolean save() {
		
		try {
	   	    File file = new File(System.getProperty("user.dir"));
	        JFileChooser fc = new JFileChooser(file);
	        fc.setFileFilter(new 
			javax.swing.filechooser.FileFilter() {
	            public boolean accept(File f) {
		      	    if (f.isDirectory()) {
	  	    	       return true;
	           	    }
	           	    return false;
	           	}
	           	public String getDescription() {
	           	   return "Save as .mid file.";
	            }
			});
	        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
		 		if (!midiModel.saveMidiFile(fc.getSelectedFile())) return false;
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
} // End MidiPanel