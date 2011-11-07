package daid.sliceAndDaid.util;

/**
 * Logging class, has static functions for logging.
 * 
 * Different log listeners can connect to this logging service.
 */
public class Logger
{
	public static void updateStatus(String status)
	{
		System.out.println(status);
	}
	
	public static void message(String message)
	{
		System.out.println(message);
	}
	
	public static void warning(String warning)
	{
		System.err.println(warning);
	}
	
	public static void error(String error)
	{
		System.err.println(error);
	}
}
