package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;


/**
  * This is the main container frame for ToneMap App
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class ToneMapFrame implements ToneMapConstants {

	static boolean JNIStatus = false;

	static final String ABOUTMSG = "ToneMap \n \nAn Cham Cham. \n \nWritten by Jim O'Mulloy \n";
	
	static {
		try {
			System.loadLibrary("WavletJNI");
			JNIStatus = true;
		} catch (UnsatisfiedLinkError ex) {
			JNIStatus = false;
		} 
	}

	public ToneMapFrame() {
		
		mainFrame = new JFrame("ToneMap");

		mainFrame.addWindowListener(new WindowAdapter() {
				                       public void windowClosing(WindowEvent e) {
									   quit();
							       }});
		
		buildMenus();
		
		buildContent();
		
		final int inset = 10;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainFrame.setBounds(inset, inset, screenSize.width - inset*2, screenSize.height - inset*2 );
	
		mainFrame.pack();
		
		mainFrame.setVisible(true);

		
				
	}

	protected void buildContent() {
	
		JPanel contentPane = new JPanel();
		JPanel upperPane = new JPanel();
		JPanel actionPane = new JPanel();
		JPanel statusPane = new JPanel();
		statusLabel = new JLabel("Status Reset");
		JPanel lowerPane = new JPanel();
		JToolBar toolBar = new JToolBar();
		JTabbedPane tabPane = new JTabbedPane(JTabbedPane.BOTTOM);
		
		contentPane.setLayout(new BorderLayout());
		
		final int inset1 = 45;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		contentPane.setPreferredSize(new Dimension(screenSize.width - inset1*2, screenSize.height - inset1*2));
		
		EmptyBorder eb = new EmptyBorder(5,5,5,5);
		BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
		CompoundBorder cb = new CompoundBorder(eb,bb);
		contentPane.setBorder(cb);

		upperPane.setLayout(new BorderLayout());
		lowerPane.setLayout(new BorderLayout());
		 
		actionPane.setLayout(new BorderLayout());
		statusPane.setLayout(new BorderLayout());

		audioModel = new AudioModel(this);
		audioPanel = audioModel.getPanel();
		
		midiModel = new MidiModel(this);
		midiPanel = midiModel.getPanel();

		tunerModel = new TunerModel(this);
		tunerPanel = tunerModel.getPanel();
		
		toneMap = new ToneMap(this);
		toneMapPanel = toneMap.getPanel();
		
		tabPane.addTab("Map", toneMapPanel);
	 	tabPane.addTab("Audio", audioPanel);
		tabPane.addTab("MIDI", midiPanel);
		tabPane.addTab("Tuner", tunerPanel);

		buildToolBar(toolBar);	
					
		actionPane.add(toolBar, BorderLayout.NORTH);
		actionPane.add(tabPane, BorderLayout.CENTER);

		statusPane.add(statusLabel, BorderLayout.CENTER);
		EmptyBorder eb1 = new EmptyBorder(2,2,2,2);
		BevelBorder bb1 = new BevelBorder(BevelBorder.LOWERED);
		CompoundBorder cb1 = new CompoundBorder(eb1,bb1);
		statusPane.setBorder(cb1);

		upperPane.add(actionPane, BorderLayout.CENTER);
		lowerPane.add(statusPane, BorderLayout.CENTER);
		
		contentPane.add(upperPane, BorderLayout.CENTER);
		contentPane.add(lowerPane, BorderLayout.SOUTH);
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		mainFrame.setContentPane(contentPane);
		
	}

	protected void buildToolBar(JToolBar toolBar) {
			
		JButton resetB = new JButton("Reset");
		resetB.setEnabled(true);
		resetB.addActionListener(new ResetBAction());
			
		player = new Player(this);
		playerPanel = player.getPanel();

		toolBar.add(resetB);
		toolBar.add(playerPanel);
		
	}

	class ResetBAction implements ActionListener {
		
		public void actionPerformed(ActionEvent evt) {
			reset();
		}
	}


	private void reset(){

		player.clear();
		toneMap.clear();
		
		audioModel = null;
		midiModel = null;
		tunerModel = null;
		toneMap = null;
		player = null;

		System.gc();
		
		buildContent();
		
		mainFrame.validate();
		mainFrame.pack();
		
	}
	
	protected void buildMenus() {
	    
		menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		JMenu file = buildFileMenu();
		JMenu help = buildHelpMenu();

		menuBar.add(file);
		menuBar.add(help);
		mainFrame.setJMenuBar(menuBar);	
	}

	protected JMenu buildFileMenu() {
		
		JMenu file = new JMenu("File");
		JMenuItem open = new JMenuItem("Open");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem quit = new JMenuItem("Quit");
	
		open.addActionListener(new ActionListener() {
		                       public void actionPerformed(ActionEvent e) {
							    if (!openToneMap()) System.out.println("report error");
				       }});

		save.addActionListener(new ActionListener() {
		                       public void actionPerformed(ActionEvent e) {
							   if (!saveToneMap()) System.out.println("report error");
				       }});

		quit.addActionListener(new ActionListener() {
		                       public void actionPerformed(ActionEvent e) {
							   quit();
				       }});

		file.add(open);
		file.add(save);
		file.addSeparator();
		file.add(quit);
		return file;
	}

	
	protected JMenu buildHelpMenu() {
		
		JMenu help = new JMenu("Help");
	    JMenuItem about = new JMenuItem("About ToneMap...");
		JMenuItem openHelp = new JMenuItem("Open Help Window");
	
		about.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
		        showAboutBox();
		    }
		});
	
		openHelp.addActionListener(new ActionListener() {
	                   public void actionPerformed(ActionEvent e) {
					   openHelpWindow();
			       }});
	
		help.add(about);
		help.add(openHelp);
	
		return help;
	    }
	
		
	public void quit() {
		System.exit(0);
	}

	public void newToneMap() {
		
	}

	public boolean openToneMap() {
   		try {
		    file = new File(System.getProperty("user.dir"));
	        JFileChooser fc = new JFileChooser(file);
	        fc.setFileFilter(new javax.swing.filechooser.FileFilter () {
	            public boolean accept(File f) {
	                if (f.isDirectory()) {
	                    return true;
	                }
	       		    String name = f.getName();
	       			if (name.endsWith(".tom")) {
	       		        return true;
	       			}
			        	return false;
	           		}
	            	public String getDescription() {
	                    return ".tom";
	                }
	        });
	
	        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	            file = fc.getSelectedFile();
				fileName = file.getName();
	        }
	    } catch (SecurityException ex) { 
	        //JavaSound.showInfoDialog();
	        ex.printStackTrace();
	        return false;
	    } catch (Exception ex) { 
	        ex.printStackTrace();
	        return false;
	    }

		System.out.println("Opened filename, file: " + fileName + ", " + file);
		
		if (file.exists()) {
			if (!toneMap.open(file)) return false;
			return true;
		} else {
			// should put out error message on status panel !!
			System.err.println("No such file: " + file);
			return false;
		}
			
	}    

	public boolean saveToneMap() {
   		try {
		    file = new File(System.getProperty("user.dir"));
	        JFileChooser fc = new JFileChooser(file);
	        fc.setFileFilter(new javax.swing.filechooser.FileFilter () {
	            public boolean accept(File f) {
	                if (f.isDirectory()) {
	                    return true;
	                }
	       		    String name = f.getName();
	       			if (name.endsWith(".tom")) {
	       		        return true;
	       			}
			        	return false;
	           		}
	            	public String getDescription() {
	                    return ".tom";
	                }
	        });
	
	        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	            file = fc.getSelectedFile();
				fileName = file.getName();
	        }
	    } catch (SecurityException ex) { 
	        //JavaSound.showInfoDialog();
	        ex.printStackTrace();
	        return false;
	    } catch (Exception ex) { 
	        ex.printStackTrace();
	        return false;
	    }

		System.out.println("Save filename, file: " + fileName + ", " + file);
		
		if (file.exists()|| !file.exists()) {
			if (!toneMap.save(file)) return false;
			return true;
		} else {
			// should put out error message on status panel !!
			System.err.println("No such file: " + file);
			return false;
		}
	}    


	public void openHelpWindow() {
		JInternalFrame help = new ToneMapHelp();
		try { 
		    help.setVisible(true);
		    help.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}

	public void showAboutBox() {
		JOptionPane.showMessageDialog(mainFrame, ABOUTMSG);
	}

	public AudioModel getAudioModel() {
		return audioModel;
	}
	
	public MidiModel getMidiModel() {
		return midiModel;
	}
	
	public TunerModel getTunerModel() {
		return tunerModel;
	}
	
	public ToneMap getToneMap() {
		return toneMap;
	}

	public boolean getJNIStatus() {
		return JNIStatus;
	}

	public void reportStatus(int status){

		this.status = status;
		StatusInfo SI = ToneMapStatus.getSI(status);
		statusLabel.setText(SI.toString());
		
	} 
	
	private JFrame mainFrame;
	private JMenuBar menuBar;
	private JInternalFrame toolPalette;
	private JCheckBoxMenuItem showToolPaletteMenuItem;
	
	JFileChooser chooser;
		
	
	private JPanel toneMapPanel;
	private JPanel audioPanel;
	private JPanel midiPanel;
	private JPanel tunerPanel;
	private JPanel playerPanel;

	private JLabel statusLabel;
	
	private AudioModel audioModel;
	private MidiModel midiModel;
	private TunerModel tunerModel;
	private ToneMap toneMap;
	private Player player;
	
	private String fileName = "untitled";
	private String errStr;
	private File file;

	private int status;
	
	public Player getPlayer() {
		return player;
	}
}