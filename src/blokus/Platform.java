package blokus;


// simple class to detect the OS
// and set a global? variable

public class Platform {
	
	public String OS_TYPE = null;
	private String[] OS_PROPS = null;
	public String WORKING_DIR = null;
	
	// OS-platform flags
	public Boolean IS_WINDOWS_OS = false;
	public Boolean IS_LINUX_OS = false;
	
	// path specific -- hidden files etc.
	public String SETTINGS_FILE = null;
	public String LOG_FILE = null;
	
	
	// misc. checks on the system hardware.
	public int CPU_CORES = 0;
	public Boolean IS_THREAD_CAPABLE = false;
	
	
	
	public Platform()
	{
		try
		{
			OS_TYPE = System.getProperty("os.name");
			OS_PROPS = OS_TYPE.split(" ");
			WORKING_DIR = System.getProperty("user.dir");
			//System.out.println(WORKING_DIR);
			
			switch (OS_PROPS[0])
			{
			
			case "Windows":
				get_windows_info();			
				break;
				
			case "Linux":
				get_linux_info();
				break;
			}
		}
		catch (IllegalStateException e)
		{
			System.err.println("Could not determine OS exception: " + e.getMessage());
		}
	}

	public String OS()
	{
		if (OS_PROPS.length > 0)
		return OS_PROPS[0];
		else return OS_TYPE;
	}
	
	// silly function...
	public String OS_VER()
	{
		if (OS_PROPS.length >0)
			return OS_PROPS[1];
		else return "";
	}
	
	
	private void get_windows_info()
	{
		IS_WINDOWS_OS = true;
		SETTINGS_FILE = WORKING_DIR + "\\" + "cfg"; // should be writeable (hidden text document)
		LOG_FILE = WORKING_DIR+"\\"+"log";
		CPU_CORES = Runtime.getRuntime().availableProcessors();
		if (CPU_CORES > 1) IS_THREAD_CAPABLE = true;
	}
	
	
	private void get_linux_info()
	{
		IS_LINUX_OS = true;
		SETTINGS_FILE = WORKING_DIR + "/" + ".cfg";
		LOG_FILE = WORKING_DIR+"/"+"log";
		CPU_CORES = Runtime.getRuntime().availableProcessors();
		if (CPU_CORES > 1) IS_THREAD_CAPABLE = true;
	}
}
