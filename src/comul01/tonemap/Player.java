package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.geom.Line2D;
import javax.swing.event.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.font.*;
import java.text.*;

/**
 * Insert the type's description here.
 * Creation date: (27/06/2001 21:17:34)
 * @author: 
 */
public class Player implements ToneMapConstants {

	private int playState = STOPPED;

	private ToneMapFrame toneMapFrame;
	private AudioModel audioModel;
	private MidiModel midiModel;
	private PlayerPanel playerPanel;

	private String errStr;

	private double duration, seconds;

	private double timeStart = INIT_TIME_START;
	private double timeEnd = INIT_TIME_END;
	private int playMode = PLAY_MODE_AUDIO;

	private int seekSetting = 0;
	private int seekToSetting =100;
	private int panSetting = INIT_PAN_SETTING;

	private int volumeSetting = INIT_VOLUME_SETTING;

	private JButton playB, stopB, pauseB;
	private JRadioButton audioB, mapB, midiB;

	private JSlider seekSlider;
	private JSlider seekToSlider;

	public class PlayerPanel extends JPanel {
	
	    public PlayTimer playTimer;

		public PlayerPanel() {

			EmptyBorder eb = new EmptyBorder(0, 0, 0, 0);
			setBorder(eb);

			add(new PlayerButtons());
			add(new PlayMode());
			add(new SeekControl());
			add(new SeekToControl());
			add(playTimer = new PlayTimer());

		}
		
		private void play() {

			if (playMode == PLAY_MODE_AUDIO) {
				if (!audioModel.play()) return;
			} else if (playMode == PLAY_MODE_MIDI) {
				if (!midiModel.play()) return;
			}
			playTimer.start();
			playB.setEnabled(false);
			stopB.setEnabled(true);
			pauseB.setText("Pause ");
			pauseB.setEnabled(true);
	
		}

		private void loop() {
			
			playTimer.stop();
			
			if (playMode == PLAY_MODE_AUDIO) {
				audioModel.playLoop();
			} else if (playMode == PLAY_MODE_MIDI) {
				midiModel.playLoop();
			}
			
			playTimer.start();
			playB.setEnabled(false);
			stopB.setEnabled(true);
			pauseB.setText("Pause ");
			pauseB.setEnabled(true);
			

		}

		
		private void stop() {

			playTimer.stop();

			if (playMode == PLAY_MODE_AUDIO) {
				audioModel.playStop();
			} else
				if (playMode == PLAY_MODE_MIDI) {
				midiModel.playStop();
			}
		
			playB.setEnabled(true);
			stopB.setEnabled(false);
			pauseB.setText("Pause ");
			pauseB.setEnabled(false);

		}

		private void pause() {

			if (playMode == PLAY_MODE_AUDIO) {
				audioModel.playPause();
			} else
				if (playMode == PLAY_MODE_MIDI) {
				midiModel.playPause();
			}			
			
			playB.setEnabled(true);
			stopB.setEnabled(true);
			pauseB.setText("Resume");
			pauseB.setEnabled(true);


		}

		private void resume() {

			if (playMode == PLAY_MODE_AUDIO) {
				audioModel.playResume();
			} else
				if (playMode == PLAY_MODE_MIDI) {
				midiModel.playResume();
			}			
			
			playB.setEnabled(true);
			stopB.setEnabled(true);
			pauseB.setText("Pause");
			pauseB.setEnabled(true);


		}

		class SeekControl extends JPanel {

			public SeekControl() {
				
				JLabel seekLabel = new JLabel("From", JLabel.RIGHT);
				add(seekLabel);
				seekSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				seekSlider.setEnabled(true);
				seekSlider.addChangeListener(new SeekSliderListener());
				add(seekSlider);

			}

			class SeekSliderListener implements ChangeListener {

				public void stateChanged(ChangeEvent e) {

					JSlider slider = (JSlider) e.getSource();
					int value = slider.getValue();
					seekSetting = value;
					if (seekToSetting < seekSetting) {
						seekToSlider.setValue(seekSetting);
					}
					slider.repaint();
				}

			}

		}

		class SeekToControl extends JPanel {

			public SeekToControl() {
				
				seekToSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
				seekToSlider.setEnabled(true);
				seekToSlider.addChangeListener(new seekToSliderListener());
				add(seekToSlider);
				JLabel seekToLabel = new JLabel("To", JLabel.LEFT);
				add(seekToLabel);

			}

			class seekToSliderListener implements ChangeListener {

				public void stateChanged(ChangeEvent e) {

					JSlider slider = (JSlider) e.getSource();
					int value = slider.getValue();
					seekToSetting = value;
					if (seekToSetting < seekSetting) {
						seekSlider.setValue(seekToSetting);
					}
				
					slider.repaint();
				}

			}

		}

		class PlayerButtons extends JPanel {

			public PlayerButtons() {

				playB = new JButton("Play");
				playB.setEnabled(true);
				playB.addActionListener(new PlayBAction());
				add(playB);

				stopB = new JButton("Stop");
				stopB.setEnabled(false);
				stopB.addActionListener(new StopBAction());
				add(stopB);

				pauseB = new JButton("Pause ");
				pauseB.setEnabled(false);
				pauseB.addActionListener(new PauseBAction());
				add(pauseB);

			}

			class PlayBAction implements ActionListener {

				public void actionPerformed(ActionEvent evt) {

					System.out.println("Play");
					play();
					
				}

			}

			class StopBAction implements ActionListener {

				public void actionPerformed(ActionEvent evt) {

					System.out.println("Stop");
					stop();
				}

			}

			class PauseBAction implements ActionListener {

				public void actionPerformed(ActionEvent evt) {

					String s = evt.getActionCommand();
 	 			    if (s.startsWith("Pause")) {
						pause();
					} else if (s.startsWith("Resume")) {
						resume();
					}
				}
			}
		}

		class PlayMode extends JPanel {

			PlayMode() {

				audioB = new JRadioButton("Audio");
				audioB.setActionCommand("Audio");
				audioB.setSelected(true);

				midiB = new JRadioButton("Midi");
				midiB.setActionCommand("Midi");
				midiB.setSelected(false);

				ButtonGroup group = new ButtonGroup();
				group.add(audioB);
				group.add(midiB);

				PlayModeListener playModeListener = new PlayModeListener();
				audioB.addActionListener(playModeListener);
				midiB.addActionListener(playModeListener);

				JPanel radioPanel = new JPanel();
				radioPanel.setLayout(new GridLayout(1, 0));
				radioPanel.add(audioB);
				radioPanel.add(midiB);

				add(radioPanel);

			}

			class PlayModeListener implements ActionListener {

				public void actionPerformed(ActionEvent e) {

					String s = e.getActionCommand();
					if (s.startsWith("Audio")) {
						if (playMode == PLAY_MODE_MIDI) {
							stop();
						}
						playMode = PLAY_MODE_AUDIO;
					} else if (s.startsWith("Midi")) {
						if (playMode == PLAY_MODE_AUDIO) {
							stop();
						}
						playMode = PLAY_MODE_MIDI;
					}
				}
			}

		}

		class PlayTimer extends JComponent implements Runnable {

			private boolean cbStop = true;
			private BufferedImage bimg;
			private Thread thread;
			private double playTime;
			private int w, h;
			private Font font = new Font("Dialog", Font.BOLD, 12);
			private Color color;
			private NumberFormat nf;

			public PlayTimer() {

				setBackground(Color.black);
				setPreferredSize(new Dimension(35, 20));
				nf = NumberFormat.getNumberInstance();
				nf.setMaximumFractionDigits(1);
				nf.setMinimumFractionDigits(1);
				nf.setMaximumIntegerDigits(2);
				nf.setMinimumIntegerDigits(1);  
			
			}

			public void start() {

				thread = new Thread(this);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.setName("Player.PlayTime");
				thread.start();
				playTime = 0.0;
			}

			public synchronized void stop() {
				thread = null;
				notifyAll();
			}

			public void run() {
				playState = getState();
				double playToTime = getToTime();
				while (playState != EOM && playTime < playToTime && thread != null) {
					
					repaint();
					try {
						Thread.sleep(100);
						playTime = getTime();
						
					} catch (InterruptedException e) {
						break;
					}
					playState = getState();
					playToTime = getToTime();
				}
				
				if (thread != null) {
					playTime = 0.0;
					repaint();
					loop();
				}
			}

			public void paint(Graphics g) {
				if (bimg == null) {
					bimg = (BufferedImage) createImage(35, 25);
				}
				int w = bimg.getWidth();
				int h = bimg.getHeight();
				Graphics2D big = bimg.createGraphics();
				big.setBackground(Color.black);
				big.clearRect(0, 0, w, h);
				big.setFont(font);
				big.setColor(color.white);
				big.drawString(nf.format(playTime/1000.0), 10, 15);
				big.setColor(Color.gray);
				big.drawLine(0, 0, 0, h - 1);
				big.drawLine(0, 0, w - 1, 0);
				big.setColor(Color.white);
				big.drawLine(w - 1, 0, w - 1, h - 1);
				big.drawLine(0, h - 1, w - 1, h - 1);
				g.drawImage(bimg, 0, 0, this);
				big.dispose();
			}
		}

	}

	public Player(ToneMapFrame toneMapFrame) {

		this.toneMapFrame = toneMapFrame;
		playerPanel = new PlayerPanel();
  	  	audioModel = toneMapFrame.getAudioModel();
   	 	midiModel = toneMapFrame.getMidiModel();
		audioModel.playSetPlayer(this);
		midiModel.playSetPlayer(this);

	}

	
	public void clear() {
		playerPanel.stop();
	}
		
	public JPanel getPanel() {
		return playerPanel;
	}
	public double getSeekTime() {
 	   return (double) seekSetting;
	}
	public double getSeekToTime() {
 	   return (double) seekToSetting;
	}
	public int getState() {

		if (playMode == PLAY_MODE_AUDIO) {
			return audioModel.playGetState();
		} else if (playMode == PLAY_MODE_MIDI) {
			return midiModel.playGetState();
		}

		return 0;

	}
	public double getTime() {

		if (playMode == PLAY_MODE_AUDIO) {
			return audioModel.playGetTime();
		} else if (playMode == PLAY_MODE_MIDI) {
			return midiModel.playGetTime();
		}

		return 0;

	}
	public double getToTime() {

		if (playMode == PLAY_MODE_AUDIO) {
			return (getSeekToTime()/100)*(audioModel.playGetLength())/1000.0;
		} else if (playMode == PLAY_MODE_MIDI) {
			return (getSeekToTime()/100)*(midiModel.playGetLength())/1000.0;
		}

		return 0;
	}
	public void reset() {

	}
} // End Player