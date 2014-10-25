package blokus;



// a simple exception class for the blokus application
public class BlokusException extends Exception {

	private static final long serialVersionUID = 1L;
	public BlokusException() { super(); }
	public BlokusException(String message) { super(message); }
	public BlokusException(String message, Throwable cause) { super(message, cause); }
	public BlokusException(Throwable cause) { super(cause); }

	// exit codes for blokus
	public static int OUT_OF_MEMORY = 1;
	public static int ILLEGAL_MOVE  = 2;
	public static int MUST_IMPLEMENT = 3;
	public static int BLOKUS_IO_ERROR = 4;
	public static int GRAPHICS_ERROR = 5;
}


