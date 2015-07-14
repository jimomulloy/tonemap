package comul01.tonemap;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.sound.sampled.*;
import javax.sound.midi.*;

/**
  * This is the main driver class for ToneMap App
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class ToneMapMain {

	public static void main(String[] args) {
	
		try { 
		    if (MidiSystem.getSequencer() == null) {
	            System.err.println("MidiSystem Sequencer Unavailable, exiting!");
				System.exit(1);
			} else if (AudioSystem.getMixer(null) == null) {
				System.err.println("AudioSystem Unavailable, exiting!");
				System.exit(1);
			}
		} catch (Exception ex) { ex.printStackTrace(); System.exit(1); }

		final ToneMapFrame TMF = new ToneMapFrame();
				
	}
}