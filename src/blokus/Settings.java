package blokus;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Settings {

	// file writer for the settings file
	private PrintWriter settings_writer = null;

	
	
	// settings specific data types -- no typedefs in java
	// so we list them in a descriptive way here.
	private String settings_tag_comment = "#";

	
	// only a short list for now.
	private String settings_var_threads = null;
	private String settings_var_ai_strength = null;
	private String settings_var_config_path = null;
	private String settings_var_logging_path = null;
	private String settings_var_use_log = null;
	private String settings_var_grid_size_x = null;
	private String settings_var_grid_size_y = null;
	private String settings_var_piece_number = null;
	private String settings_var_piece_types = null;
	private String settings_var_board_geometry = null;

	// I/O maintenence specific for the settings file
	private Boolean auto_generate_settings = false;
	private Boolean settings_file_is_ok = false;
	private Boolean save_settings_on_exit = true; // default value for now
	private Boolean use_log_file = true; // default value

	// header stuff for the settings file
	private String settings_var_date = null;

	// the main settings container, accessible from anywhere in the
	// the program
	public Map<String, String> USER_SETTINGS;
	
	// the os-platform, used for some specific naming conventions
	// in the logging/settings paths/files.
	private Platform p = null;
	private Version v = null;
	
	// c'tor for this class..
	public Settings(Platform platform, Version version)
	{
		// this c'tor will set the settings-file-path
		// and determine the OS-type. 
		USER_SETTINGS = new HashMap<>(); 
		// default copy operations here (probably slow)
		p = platform;
		v = version;
		
		// see if the settings file is there??
		if ( is_settings_file_ok() )
		{
			settings_file_is_ok = true;
		}

	}
	
	
	
	///////////////////////////////////////////////////////////////
	// check if the settings file is there, and parse it
	// return true on success
	private Boolean is_settings_file_ok() 
	{
		Boolean file_state_ok = false;
		// first check if the file exists
		File settings_file = new File(p.SETTINGS_FILE);
		
		if (settings_file.exists())
		{
			// TODO: log this event
			file_state_ok = true;
			
			// parse the settings file since it exists,
			// check if it is corrupted/or contains incorrect 
			// entries.
			try {
				parse_settings_file(p.IS_WINDOWS_OS);
			} catch (BlokusException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else 
		{
			// TODO: log this event
			// create a new (hidden) settings file 
			try
			{
				
				// the windows version
				if (p.IS_WINDOWS_OS)
				{
					
					
					write_header_info(p.IS_WINDOWS_OS);
					write_main_info(p.IS_WINDOWS_OS);
					
					// set file to hidden and readonly after creation
					Runtime.getRuntime().exec("attrib +H +r "+p.SETTINGS_FILE);
					
					// update the keys in the global settings dictionary.
					try {
						update_settings_dictionary();
					} catch (BlokusException e) {
					
						e.printStackTrace();
					}
				}
				
				// the linux version
				else
				{
					
					write_header_info(p.IS_WINDOWS_OS);
					write_main_info(p.IS_WINDOWS_OS);
					
					// make the file read-only? 
					Runtime.getRuntime().exec("chmod 0444"+ " "+ p.SETTINGS_FILE);
					
					
					// update the keys in the global settings dictionary.
					try {
						update_settings_dictionary();
					} catch (BlokusException e) {
					
						e.printStackTrace();
					};
				}
				
				file_state_ok = true;
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		
		} 
		return file_state_ok;
	}
	
	
	
	// standard header information for a new settings file
	private void write_header_info(Boolean is_Windows)
	{
		Date date = new Date();
		settings_var_date = date.toString();
		
		// set the file writer
		if (settings_writer == null)
		{
			try {
				settings_writer = new PrintWriter(p.SETTINGS_FILE, "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		
		settings_writer.println("#");
		settings_writer.println("#----------------------------------------------------------------------------------------");
		settings_writer.println("#	THIS SETTINGS FILE WAS AUTO-GENERATED, ONLY EDIT IF YOU KNOW WHAT YOU ARE DOING!");
		settings_writer.println("#  DATE CREATED: "+settings_var_date);
		settings_writer.println("#");
		settings_writer.println("#  "+v.MAIN_APP_NAME+" "+v.VERSION+" ");
		settings_writer.println("#  "+v.LICENSE_INFO);
		settings_writer.println("#  Author list: "+v.AUTHOR_LIST);
		settings_writer.println("#----------------------------------------------------------------------------------------");
		settings_writer.println("#");
		//settings_writer.close();
		
	}
	
	
	
	
	private void write_main_info(Boolean is_Windows)
	{
		
		// set the file writer
		if (settings_writer == null)
		{
			try {
				settings_writer = new PrintWriter((is_Windows ? p.SETTINGS_FILE : "."+p.SETTINGS_FILE), "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		
		//=========================================================
		// some example settings
		settings_writer.println("SYSTEM_OS: "+p.OS_TYPE);
		settings_writer.println("CPU_CORES: "+Integer.toString(p.CPU_CORES));
		settings_writer.println("USE_LOG: "+"True");
		settings_writer.println("AI_DIFFICULTY: "+"Normal");
		settings_writer.println("BOARD_SIZE_ROWS: "+ "16");
		settings_writer.println("BOARD_SIZE_COLS: "+ "16");
		settings_writer.println("BOARD_GEOMETRY: "+"SQUARE");
		settings_writer.close();
		//=========================================================
		
		// set the relevant (local) variables to their default variables.
		settings_var_threads = Integer.toString(p.CPU_CORES);
		settings_var_ai_strength ="Normal";
		settings_var_use_log = "True";
		settings_var_grid_size_x = "20";
		settings_var_grid_size_y = "20";
		settings_var_board_geometry = "SQUARE";

		
	}
	
	
	// update the keys in the global settings dictionary.
	private void update_settings_dictionary() throws BlokusException
	{
		USER_SETTINGS.put("settings_var_threads",settings_var_threads);
		USER_SETTINGS.put("settings_var_ai_strength",settings_var_ai_strength);
		USER_SETTINGS.put("settings_var_use_log",settings_var_use_log);
		USER_SETTINGS.put("settings_var_grid_size_x",settings_var_grid_size_x);
		USER_SETTINGS.put("settings_var_grid_size_y", settings_var_grid_size_y);
		USER_SETTINGS.put("settings_var_board_geometry",settings_var_board_geometry);

	}
	
	

	
	void parse_settings_file(Boolean is_Windows) throws BlokusException
	{

		Charset charset = Charset.forName("UTF-8");
		File f = new File((is_Windows ? p.SETTINGS_FILE : "."+p.SETTINGS_FILE).replace("\\","/"));

		
		// parse the settings file
		try ( BufferedReader reader = new BufferedReader( new FileReader(f) ) ) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	line = line.replace(" ", "");
		        if(line.startsWith("#")) continue;
		        String dictionary_entries[] = line.split(":");
		        
		        
		        //System.out.println(dictionary_entries[0]+":"+ dictionary_entries[1]);
		        //TODO parse the values, error checking...
		        String key = "";
		        switch(dictionary_entries[0])
		        {
		        case "CPU_CORES": key = "settings_var_threads"; break;
		        case "USE_LOG":   key = "settings_var_use_log"; break;
		        case "BOARD_SIZE_ROWS": key = "settings_var_grid_size_x"; break;
		        case "BOARD_SIZE_COLS": key = "settings_var_grid_size_y"; break;
		        case "BOARD_GEOMETRY": key = "settings_var_board_geometry";break;
		        }
		        
		        // append the settings to the dictionary.
		        USER_SETTINGS.put(key, dictionary_entries[1]);
		        
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
}
