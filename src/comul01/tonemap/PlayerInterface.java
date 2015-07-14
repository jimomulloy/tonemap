package comul01.tonemap;

/**
 * Insert the type's description here.
 * Creation date: (05/07/2001 20:01:45)
 * @author: 
 */
public interface PlayerInterface {

	public boolean play();

	public void playStop();

	public void playPause();
	
	public void playResume();

	public void playLoop();

	public double playGetLength();
	
	public double playGetTime();
	
	public int playGetState();
	
	public void playSetSeek(double seekTime);

	public void playSetPlayer(Player player);
	
	
}