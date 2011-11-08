package daid.sliceAndDaid.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import daid.sliceAndDaid.util.Logger;

public class CraftConfigLoader
{
	/***************************
	 * Load and save functions
	 ***************************/
	
	/**
	 * loadConfig
	 * 
	 * Loads the configuration from a file, use 'null' for the default config file.
	 */
	public static void loadConfig(String filename)
	{
		if (filename == null)
			filename = System.getProperty("user.home") + "/.SliceAndDaid.conf";
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e)
		{
			return;
		}
		String line;
		String section = null;
		try
		{
			while ((line = br.readLine()) != null)
			{
				if (line.startsWith(";"))
					continue;
				if (line.startsWith("[") && line.endsWith("]"))
				{
					section = line;
					continue;
				}
				if (line.indexOf('=') < 0)
					continue;
				String key = line.substring(0, line.indexOf('='));
				String value = line.substring(line.indexOf('=') + 1);
				if ("[SliceAndDaid config]".equals(section))
				{
					setField(key, value);
				}
			}
		} catch (IOException e)
		{
			Logger.error("IOException during loading of config file...");
		}
	}
	
	private static void setField(String key, String value)
	{
		Class<?> c = CraftConfig.class;
		Field f = null;
		try
		{
			f = c.getField(key);
			if (f == null)
				return;
			if (f.getType() == Double.TYPE)
			{
				f.setDouble(null, Double.parseDouble(value));
			} else if (f.getType() == Integer.TYPE)
			{
				f.setInt(null, Integer.parseInt(value));
			} else if (f.getType() == String.class)
			{
				f.set(null, value);
			} else
			{
				throw new RuntimeException("Unknown config type: " + f.getType());
			}
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (NoSuchFieldException e)
		{
			Logger.warning("Found: " + key + " in the configuration, but I don't know this setting");
		}
		
	}
	
	/**
	 * saveConfig
	 * 
	 * Saves the configuration to a file, use 'null' for the default config file.
	 */
	public static void saveConfig(String filename)
	{
		if (filename == null)
			filename = System.getProperty("user.home") + "/.SliceAndDaid.conf";
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			bw.write(";Saved with version: " + CraftConfig.VERSION + "\n");
			bw.write("[SliceAndDaid config]\n");
			Class<CraftConfig> configClass = CraftConfig.class;
			for (final Field f : configClass.getFields())
			{
				Setting s = f.getAnnotation(Setting.class);
				if (s == null)
					continue;
				try
				{
					bw.write(f.getName() + "=" + f.get(null).toString() + "\n");
				} catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				} catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
			bw.close();
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
