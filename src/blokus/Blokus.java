package blokus;
import java.awt.EventQueue;
import java.util.Scanner;

import javafx.application.Application;
import blokus.Logger.LogType;
import blokus.Settings;
import blokus.BlokusException;
import graphics.BlokusWindow;

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

		// TODO: move this to an intializer class.
		Platform platform_info = new Platform();
		Version version_info = new Version();
		Logger log = null;
		BlokusWindow MainWindow = null; 
		
		Settings settings = new Settings(platform_info,version_info);
		
		String CPU_CORES    = settings.USER_SETTINGS.get("settings_var_threads");
		String boardLength  = settings.USER_SETTINGS.get("settings_var_grid_size_x");
		String boardHeight  = settings.USER_SETTINGS.get("settings_var_grid_size_y");
		String useLog       = settings.USER_SETTINGS.get("settings_var_use_log");
		String boardGeom    = settings.USER_SETTINGS.get("settings_var_board_geometry");
		String aiStrength   = settings.USER_SETTINGS.get("settings_var_ai_strength");
				
		
		log = new Logger(platform_info, version_info);
		log.write("Using "+CPU_CORES+" cores",LogType.NORMAL);
		log.write("Board geometry is "+boardGeom+" size "+boardLength + " by " + boardHeight + " squares.",LogType.NORMAL);
		log.write("AI strength is " + aiStrength, LogType.NORMAL);
		

		
		try
		{
			int bwidth = Integer.parseInt(boardLength);
			int bheight = Integer.parseInt(boardHeight);
			
	        //EventQueue.invokeLater(new Runnable() {

	           // @Override
	            //public void run() {
	            	//final BlokusWindow MainWindow = new BlokusWindow(bwidth, bheight);//, log);
	            //}
	        //});
			MainWindow = new BlokusWindow(bwidth, bheight, log);
			Board blokusBoard = new Board(bwidth, bheight, log);
		}
		catch (Exception any)
		{
			log.write("..[MAIN] Exception, failed to create board arrays!",LogType.ERROR);
			System.exit(BlokusException.BLOKUS_IO_ERROR);
		}
		
		log.write("Created main board.",LogType.NORMAL);
		
		
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
