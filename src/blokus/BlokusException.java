package blokus;



// a simple exception class for the blokus application
public class BlokusException extends Exception {

	private static final long serialVersionUID = 1L;
	public BlokusException() { super(); }
	public BlokusException(String message) { super(message); }
	public BlokusException(String message, Throwable cause) { super(message, cause); }
	public BlokusException(Throwable cause) { super(cause); }

}


