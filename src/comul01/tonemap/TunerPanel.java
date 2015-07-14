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
  * This is a class that encapsulates the Tuner properties associated with the ToneMap 
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class TunerPanel extends JPanel implements ToneMapConstants {

	class TimeControlListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
				
   		   tunerModel.timeStart = (double)timeControl.getTimeStart();
	 	   tunerModel.timeEnd = (double)timeControl.getTimeEnd();
			
		}

	}

	
	class PitchControlListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
				
   		   tunerModel.pitchLow = pitchControl.getPitchLow();
	 	   tunerModel.pitchHigh = pitchControl.getPitchHigh();
			
		}

	}

			
	class NoteControl extends JPanel {

		
		public NoteControl() {
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			TitledBorder tb = new TitledBorder(new EtchedBorder());
			tb.setTitle("Note Scan"); 
			setBorder(tb);
			
			noteLowSlider = new TmSlider(JSlider.HORIZONTAL, 0, 100, INIT_NOTE_LOW, 
									"Low Threshhold",new NoteSliderListener());
			add(noteLowSlider);

			noteHighSlider = new TmSlider(JSlider.HORIZONTAL, 0, 100, INIT_NOTE_HIGH,
									"High Threshhold", new NoteSliderListener());
			add(noteHighSlider);
			noteSustainSlider = new TmSlider(JSlider.HORIZONTAL, 0, 100, 
									INIT_NOTE_SUSTAIN,
									"Sustain Time", new NoteSliderListener());
			add(noteSustainSlider);
			noteMinDurationSlider = new TmSlider(JSlider.HORIZONTAL, 0, 1000, 
									INIT_NOTE_MIN_DURATION,
									"Min Duration", new NoteSliderListener());
			add(noteMinDurationSlider);
			noteMaxDurationSlider = new TmSlider(JSlider.HORIZONTAL, 0, 100, 
									INIT_NOTE_MAX_DURATION,
									"Max Duration", new NoteSliderListener());
			add(noteMaxDurationSlider);

		}

		class NoteSliderListener implements ChangeListener {

			public void stateChanged(ChangeEvent e) {
   	
   			    TmSlider slider = (TmSlider) e.getSource();
	  			int value = slider.getValue();
				String s = slider.getName();
				if (s.startsWith("Low")) {
			 	   tunerModel.noteLow = value;
				} else if (s.startsWith("High")) {
			 	   tunerModel.noteHigh = value;
				}else if (s.startsWith("Sus")) {
			 	   tunerModel.noteSustain = value;
				}else if (s.startsWith("Min")) {
			 	   tunerModel.noteMinDuration = value;
				}else if (s.startsWith("Max")) {
			 	   tunerModel.noteMaxDuration = value;
				}
			}
		}

	}

	

	class HarmonicControl extends JPanel {

		
		public HarmonicControl() {

			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

			harmonic1S = new TmSlider(JSlider.HORIZONTAL, 0, 100,
	            		100, "Harmonic 1",new FilterSliderListener());
			harmonic2S = new TmSlider(JSlider.HORIZONTAL, 0, 100,
	            		100, "Harmonic 2",new FilterSliderListener());
			harmonic3S = new TmSlider(JSlider.HORIZONTAL, 0, 100,
	            		100, "Harmonic 3",new FilterSliderListener());
			harmonic4S = new TmSlider(JSlider.HORIZONTAL, 0, 100,
	            		100, "Harmonic 4",new FilterSliderListener());
			
			add(harmonic1S);
			add(harmonic2S);
			add(harmonic3S);
			add(harmonic4S);

		}

	}		

	class FormantControl extends JPanel {

		
		public FormantControl() {


			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

			formantLowS = new TmSlider(JSlider.HORIZONTAL, 0, 100,
						100, "Formant low",new FilterSliderListener());
			formantMiddleS = new TmSlider(JSlider.HORIZONTAL, 0, 100,
						100, "Formant middle",new FilterSliderListener());
			formantHighS = new TmSlider(JSlider.HORIZONTAL, 0, 100,
						100, "Formant high",new FilterSliderListener());
			formantFreqS = new TmSlider(JSlider.HORIZONTAL, 0, 5000,
						1000, "Formant Frequency",new FilterSliderListener());

			add(formantLowS);
			add(formantMiddleS);
			add(formantHighS);
			add(formantFreqS);

		}

	}

	class FilterControl extends JPanel {

		
		public FilterControl() {

			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

			undertoneS = new TmSlider(JSlider.HORIZONTAL, 0, 100,
						100, "Undertone",new FilterSliderListener());
			droneS = new TmSlider(JSlider.HORIZONTAL, 0, 100,
						100, "Drone",new FilterSliderListener());
			spikeS = new TmSlider(JSlider.HORIZONTAL, 0, 100,
						100, "Spike",new FilterSliderListener());
			droneS.setEnabled(false);
			spikeS.setEnabled(false);
			
		 	add(undertoneS);
		 	add(droneS);
		 	add(spikeS);
		}
	}
				
	class FilterSliderListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
	
			TmSlider slider = (TmSlider) e.getSource();
			int value = slider.getValue();
		  	String s = slider.getName();
			if (s.startsWith("Harmonic 1")) {
		 	   	tunerModel.harmonic1Setting = value;
			} else if (s.startsWith("Harmonic 2")) {
		 	   	tunerModel.harmonic2Setting = value;
			} else if (s.startsWith("Harmonic 3")) {
		 	   	tunerModel.harmonic3Setting= value;
			} else if (s.startsWith("Harmonic 4")) {
		 	   	tunerModel.harmonic4Setting = value;
			} else if (s.startsWith("Formant low")) {
				tunerModel.formantLowSetting = value;
			} else if (s.startsWith("Formant middle")) {
				tunerModel.formantMiddleSetting = value;
			} else if (s.startsWith("Formant high")) {
				tunerModel.formantHighSetting = value;
			} else if (s.startsWith("Formant Frequency")) {
				tunerModel.formantFreqSetting = value;
			} else if (s.startsWith("Drone")) {
				tunerModel.droneSetting = value;
			} else if (s.startsWith("Undertone")) {
				tunerModel.undertoneSetting = value;
			} else if (s.startsWith("Spike")) {
				tunerModel.spikeSetting = value;
			}
		}
	}

	class ModeControl extends JPanel {

		
		public ModeControl() {
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			TitledBorder tb = new TitledBorder(new EtchedBorder());
			tb.setTitle("Mode"); 
			setBorder(tb);

		    noteModeB = new JRadioButton("Note");
			noteModeB.setActionCommand("Note");
			noteModeB.setSelected(true);

		    beatModeB = new JRadioButton("Beat");
			beatModeB.setActionCommand("Beat");
			beatModeB.setSelected(false);
			beatModeB.setEnabled(true);
		   
			chirpModeB = new JRadioButton("Chirp");
			chirpModeB.setActionCommand("Chirp");
			chirpModeB.setSelected(false);
			chirpModeB.setEnabled(false);
		    
			chordModeB = new JRadioButton("Chord");
			chordModeB.setActionCommand("Chord");
			chordModeB.setSelected(false);
			chordModeB.setEnabled(false);

			ButtonGroup group = new ButtonGroup();
			group.add(noteModeB);
			group.add(beatModeB);
			group.add(chirpModeB);
			group.add(chordModeB);
	
			ModeControlListener modeControlListener = new ModeControlListener();
   			noteModeB.addActionListener(modeControlListener);
   			beatModeB.addActionListener(modeControlListener);
   			chirpModeB.addActionListener(modeControlListener);
   			chordModeB.addActionListener(modeControlListener);

		 	JPanel radioPanel = new JPanel();
	        radioPanel.setLayout(new GridLayout(1, 0));
			radioPanel.add(noteModeB);
	   		radioPanel.add(beatModeB);
			radioPanel.add(chirpModeB);
			radioPanel.add(chordModeB);
		
			add(radioPanel);

			harmonicCB = new JCheckBox("Harmonic");
		    harmonicCB.addItemListener(new FilterBoxItemListener());
			add(harmonicCB);
			
			formantCB = new JCheckBox("Formant");
		    formantCB.addItemListener(new FilterBoxItemListener());
			add(formantCB);

			undertoneCB = new JCheckBox("Undertone");
		    undertoneCB.addItemListener(new FilterBoxItemListener());
			add(undertoneCB);

			peakCB = new JCheckBox("Peak");
		    peakCB.addItemListener(new FilterBoxItemListener());
			add(peakCB);

			droneCB = new JCheckBox("Drone");
		    droneCB.addItemListener(new FilterBoxItemListener());
		    droneCB.setEnabled(false);
			add(droneCB);

			spikeCB = new JCheckBox("Spike");
		    spikeCB.addItemListener(new FilterBoxItemListener());
	      	spikeCB.setEnabled(false);
			add(spikeCB);
 
		}
			 
		class ModeControlListener implements ActionListener{
   			
			public void actionPerformed(ActionEvent e) {
  			   
  				String s = e.getActionCommand();
  			    if (s.startsWith("Note")) {
					tunerModel.processMode = NOTE_MODE;
				} else if (s.startsWith("Beat")) {
					tunerModel.processMode = BEAT_MODE;
				} else if (s.startsWith("Chirp")) {
					tunerModel.processMode = CHIRP_MODE;
			 	} else if (s.startsWith("Chord")) {
					tunerModel.processMode = CHORD_MODE;
			 	} 
			}
		}
	
		class FilterBoxItemListener implements ItemListener {

			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() instanceof JComboBox) {
					JComboBox combo = (JComboBox) e.getSource();
				} else {
					JCheckBox cb = (JCheckBox) e.getSource();
					String name = cb.getText();
					if (name.startsWith("Harmonic")) {
						tunerModel.harmonicSwitch = cb.isSelected();
					} else if(name.startsWith("Formant")) {
						tunerModel.formantSwitch = cb.isSelected();
					} else if(name.startsWith("Undertone")) {
						tunerModel.undertoneSwitch = cb.isSelected();
					} else if(name.startsWith("Peak")) {
						tunerModel.peakSwitch = cb.isSelected();
					} else if(name.startsWith("Drone")) {
						tunerModel.droneSwitch = cb.isSelected();
					} else if(name.startsWith("Spike")) {
						tunerModel.spikeSwitch = cb.isSelected();
					}
				}
			}

		}

					
	}


	private TunerModel tunerModel;
	
	public TimeControl timeControl;
	public PitchControl pitchControl;
	public ModeControl modeControl;
	public NoteControl noteControl;
	public HarmonicControl harmonicControl;
	public FormantControl formantControl;
	public FilterControl filterControl;
	public TmSlider noteLowSlider;
	public TmSlider noteHighSlider;
	public TmSlider noteSustainSlider;
	public TmSlider noteMinDurationSlider;
	public TmSlider noteMaxDurationSlider;
	public	TmSlider harmonic1S;
	public	TmSlider harmonic2S;
	public	TmSlider harmonic3S;
	public	TmSlider harmonic4S;
	public	TmSlider formantLowS;
	public	TmSlider formantMiddleS;
	public	TmSlider formantHighS;
	public	TmSlider formantFreqS;
	public	TmSlider droneS;
	public	TmSlider undertoneS;
	public	TmSlider spikeS;
	public JCheckBox harmonicCB, formantCB, droneCB, undertoneCB, peakCB, spikeCB; 
	public JRadioButton noteModeB, beatModeB, chirpModeB, chordModeB;

	public TunerPanel(TunerModel tunerModel) {

		this.tunerModel = tunerModel;
		
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
		//p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
	
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
		
		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
		
		JPanel p5 = new JPanel();
		p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
	
		JPanel p6 = new JPanel();
		p6.setLayout(new BoxLayout(p6, BoxLayout.X_AXIS));
			
		p1.add(timeControl = new TimeControl(new TimeControlListener()));
		p1.add(pitchControl = new PitchControl(new PitchControlListener()));
		p2.add(modeControl = new ModeControl());
		p3.add(noteControl = new NoteControl());
		p4.add(harmonicControl = new HarmonicControl());
		p5.add(formantControl = new FormantControl());
		p6.add(filterControl = new FilterControl());
				
		p0.add(p3);
		p0.add(p4);
		p0.add(p5);
		p0.add(p6);
		p0.add(p1);

		add(p0,BorderLayout.CENTER);
		add(p2,BorderLayout.NORTH);	

	
	}
}