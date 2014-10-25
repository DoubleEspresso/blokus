package blokus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Date;


public class Logger 
{
	
	// enum the log types
	public enum LogType
	{
		NORMAL, CRITICAL, ERROR
	}

	// global writer for the log file
	public PrintWriter log_writer = null;
	
	// the os-platform, used for some specific naming conventions
	// in the logging/settings paths/files.
	private Platform p = null;
	private Version v = null;
	
	
	// c'tor for this class..
	public Logger(Platform platform, Version version)
	{
		// this c'tor will set the settings-file-path
		// and determine the OS-type. 
		
		// default copy operations here (probably slow)
		p = platform;
		v = version;
		
		init_log_file(p.OS_TYPE == "Windows"? true : false);

	}
	
	
	
	private void init_log_file(Boolean is_Windows)
	{
		// standard header information for a new settings file

		Date date = new Date();
		String log_var_date = date.toString();
			
		// create a new one
		File f = new File( p.LOG_FILE.replace("\\","/"));

		
		// delete the old log-file if it exists
		try {
			Files.deleteIfExists(f.toPath());
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		

		
		// set the file writer
		if (log_writer == null)
		{
			try 
			{
				log_writer = new PrintWriter(p.LOG_FILE, "UTF-8");
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			}
		}
			
			
		log_writer.println("#");
		log_writer.println("#----------------------------------------------------------------------------------------");
		log_writer.println("#	THIS SETTINGS FILE WAS AUTO-GENERATED FOR DEBUG PURPOSES");
		log_writer.println("#  DATE CREATED: "+log_var_date);
		log_writer.println("#");
		log_writer.println("#  "+v.MAIN_APP_NAME+" "+v.VERSION+" ");
		log_writer.println("#  "+v.LICENSE_INFO);
		log_writer.println("#  Author list: "+v.AUTHOR_LIST);
		log_writer.println("#----------------------------------------------------------------------------------------");
		log_writer.println("#");
		log_writer.flush();
		
	}

	public void write(String msg, LogType lt)
	{
		Date date = new Date();
		String theTime = date.toString();
		String data = "";

		
		switch (lt)
		{
		case NORMAL: data = "[NORMAL]("+theTime+")"+" :: "; break;
		case CRITICAL: data = "[CRITICAL]("+theTime+")"+" :: "; break;
		case ERROR: data = "[ERROR]("+theTime+")"+" :: "; break;
		default:
			break;
		}
		
		// write to the log file
		log_writer.println(data+msg);
		log_writer.flush();
	}
	
}