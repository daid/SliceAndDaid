package daid.sliceAndDaid.util;

import java.util.HashSet;

/**
 * Logging class, has static functions for logging.
 * 
 * TODO: Different log listeners can connect to this logging service. So the GUI version can show a
 * nice progress dialog.
 */
public class Logger
{
	private static HashSet<LoggingInterface> loggers = new HashSet<LoggingInterface>();
	
	public static void updateStatus(String status)
	{
		System.out.println(status);
		for(LoggingInterface li : loggers)
			li.updateStatus(status);
	}
	
	public static void message(String message)
	{
		System.out.println(message);
		for(LoggingInterface li : loggers)
			li.message(message);
	}
	
	public static void warning(String warning)
	{
		System.err.println(warning);
		for(LoggingInterface li : loggers)
			li.warning(warning);
	}
	
	public static void error(String error)
	{
		System.err.println(error);
		for(LoggingInterface li : loggers)
			li.error(error);
	}
	
	public static void setProgress(int value, int max)
	{
		for(LoggingInterface li : loggers)
			li.setProgress(value, max);
	}

	public static void register(LoggingInterface obj)
	{
		loggers.add(obj);
	}
	
	public static void unRegister(LoggingInterface obj)
	{
		loggers.remove(obj);
	}
}
