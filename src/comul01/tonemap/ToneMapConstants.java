package comul01.tonemap;

/**
 * Insert the type's description here.
 * Creation date: (10/07/2001 15:36:10)
 * @author: 
 */
public interface ToneMapConstants {

	public static final int AUDIO_OPEN=1;
	
	public static final int PLAYING=1;
	public static final int STOPPED=0;
	public static final int PAUSED=2;
	public static final int EOM=3; 

	public static final int INIT_SAMPLE_SIZE=100;
	public static final int MIN_SAMPLE_SIZE=10;
	public static final int MAX_SAMPLE_SIZE=1000;

	public static final double MAX_AUDIO_DURATION=60.0;
	public static final long MAX_CLIP_LENGTH=4000000; 

	public static final int INIT_RESOLUTION=10;
	public static final int MIN_RESOLUTION=1;
	public static final int MAX_RESOLUTION=100;
	
	public static final int INIT_PAN_SETTING=50;	
	public static final int INIT_VOLUME_SETTING=50;
	
	public final int PROGRAM = 192;
	public final int NOTEON = 144;
	public final int NOTEOFF = 128;
	public final int SUSTAIN = 64;
	public final int REVERB = 91;
	
	public final int OFF = 0;
	public final int ON = 1;
	public final int START = 2;
	public final int END = 3;
	public final int PENDING = 4;
	
	public final static int INIT_PITCH_LOW=36;
	public final static int INIT_PITCH_HIGH=96;
	public final static int INIT_PITCH_MIN=12;
	public final static int INIT_PITCH_MAX=108;
	public final static int PITCH_RANGE_MAX=60;
	public final static int INIT_PITCH_INC=1;

	public static final int PLAY_MODE_AUDIO=1;
	public static final int PLAY_MODE_MAP=2;	
	public static final int PLAY_MODE_MIDI=3;	

	public final static int INIT_TIME_START=0;
	public final static int INIT_TIME_END=60000;
	public final static int INIT_TIME_MIN=0;
	public final static int INIT_TIME_MAX=60000;
	public final static int INIT_TIME_INC=100;

	public static final int ONE_SECOND = 1000;

	public static final int TRANSFORM_MODE_JAVA = 0;
	public static final int TRANSFORM_MODE_JNI = 1;
	
	public static final int VIEW_MODE_AUDIO = 0;
	public static final int VIEW_MODE_PRE = 1;
	public static final int VIEW_MODE_POST = 2;
	public static final int VIEW_MODE_NOTE = 3;
	public static final int INIT_TIME_SCALE = 100;
	public static final int INIT_PITCH_SCALE = 100;
	public static final int INIT_LOW_THRESHHOLD = 80;
	public static final int INIT_HIGH_THRESHHOLD = 80;

	public static final int NOTE_MODE = 0;
	public static final int BEAT_MODE = 1;
	public static final int CHIRP_MODE = 2;
	public static final int CHORD_MODE = 3;

	public static final int INIT_NOTE_LOW=80;
	public static final int INIT_NOTE_HIGH=80;
	public static final int INIT_NOTE_SUSTAIN=100;
	public static final int INIT_NOTE_MIN_DURATION=100;
	public static final int INIT_NOTE_MAX_DURATION=100;
	
	public static final int INIT_NOISE_LOW=0;
	public static final int INIT_NOISE_HIGH=100;

	public static final int INIT_BPM_SETTING=120;
	public static final int INIT_INSTRUMENT_SETTING=1;
	public static final int MAX_BPM_SETTING=180;
	public static final int INIT_VELOCITY_SETTING=64;
	public static final int INIT_PRESSURE_SETTING=64;
	public static final int INIT_REVERB_SETTING=64;
	public static final int INIT_BEND_SETTING=8192;

	public static final int ST_INFO = 1;
	public static final int ST_ERROR = 2;
	
	public static final int SC_TONEMAP_LOADING = 1;
	public static final int SC_TONEMAP_LOADED = 2;
	public static final int SC_TONEMAP_PROCESSING = 3;
	public static final int SC_TONEMAP_PROCESSED = 4;
	

	
}