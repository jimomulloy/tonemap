package comul01.tonemap;


/**
  * This is a class that encapsulates a set of Note Musical Symbols
  *
  * @version 1.0 01/01/01
  * @author Jim O'Mulloy
  */
public class NoteSymbol {
	
	public char noteChar;
	public char noteSharp;
	public int noteOctave;

	public String toString() {
		String s=null;
		if (noteSharp == ' ') s = s.valueOf(noteChar)+s.valueOf(noteOctave);
		else s= s.valueOf(noteChar)+s.valueOf(noteSharp)+s.valueOf(noteOctave);
		return s;
	}
		
} // End NoteSymbol