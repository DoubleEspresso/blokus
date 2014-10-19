package blokus;
import java.util.Scanner;
import blokus.Logger.LogType;



// TODO: 
// 1. validate the do_move methods for simple cases
// 2. implement a point&click/select piece and place piece action
//    for the main grid frame 
// 3. add the simple 1-block piece to the grid-frame and make it click-able 
// 4. legality checks for moves.


public class Blokus {

	private static boolean gameFinished = false;
	private static String cmd="";

	//Logger log = null; //new Logger(platform_info, version_info);
	
	// main entry point for the application
	public static void main(String[] args) 
	{
		
		// draw a simple GUI,
		// TODO: move this to an intializer class.
		
		MainPane twoFrame = new MainPane();
		
		Platform platform_info = new Platform();
		Version version_info = new Version();
		Settings settings = new Settings(platform_info, version_info);
		
		
		// some example log-writing...
		Logger log = new Logger(platform_info, version_info);
		
		log.write("1st Test message!",LogType.NORMAL);
		log.write("2nd Test message!",LogType.NORMAL);
		log.write("3rd Test message!",LogType.NORMAL);
		
		
		// example of dictionary settings access here...
		System.out.println((String) settings.USER_SETTINGS.get("settings_var_threads"));
		
		
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		
		
		// simple command parsing utility
		// TODO could run this in "debug" mode,
		// which would be set in a settings file.
		while(!gameFinished)
		{
			// block here waiting for input
			while(cmd == "")
			{
				try
				{
				  System.out.println("Idle>");
				  cmd = in.nextLine();
				  cmd.replaceAll("\\s+","");
				}
				catch (NullPointerException e)
				{
					 System.err.println("NullPointerException: " + e.getMessage());
				}
				finally
				{
				  // nothing here.
				}
			}
			
			// parse done here
			System.out.println("  parsed: "+cmd);
			if (cmd.equals( "quit") || cmd.equals("q"))  gameFinished = true;
			else
			  cmd = "";
			
			
			// TODO: graceful exit
			log.log_writer.close();
		}
	
	}
}
