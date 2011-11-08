package daid.sliceAndDaid.util;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import daid.sliceAndDaid.config.CraftConfig;

public class GCodeFile
{
	private FileWriter fileWriter;
	private double totalExtruderValue = 0;
	private double buildTime = 0;
	private double lastFeedrate = -1;
	private Vector3 oldPos = new Vector3();
	private DecimalFormat xyzFormat, eFormat, fFormat;
	
	public GCodeFile(FileWriter fileWriter)
	{
		this.fileWriter = fileWriter;
		xyzFormat = new DecimalFormat("#.##");
		eFormat = new DecimalFormat("#.###");
		fFormat = new DecimalFormat("#.#");
	}
	
	public void writeMoveZ(double z, double feedRate, String comment) throws IOException
	{
		switch (CraftConfig.gcodeType)
		{
		case CraftConfig.GCODE_FULL:
			fileWriter.write("G1 Z" + xyzFormat.format(z) + " F" + fFormat.format(feedRate * 60) + "; " + comment + "\n");
			break;
		case CraftConfig.GCODE_COMPACT:
			if (feedRate != lastFeedrate)
				fileWriter.write("G1 Z" + xyzFormat.format(z) + " F" + fFormat.format(feedRate * 60) + "\n");
			else
				fileWriter.write("G1 Z" + xyzFormat.format(z) + "\n");
			break;
		case CraftConfig.GCODE_TINY_COMPACT:
			if (feedRate != lastFeedrate)
				fileWriter.write("G1 Z" + xyzFormat.format(z) + " F" + fFormat.format(feedRate * 60) + "\n");
			else
				fileWriter.write("G1 Z" + xyzFormat.format(z) + "\n");
			break;
		}
		
		doMove(oldPos.x, oldPos.y, z, feedRate);
	}
	
	public void writeMoveXY(double x, double y, double feedRate, String comment) throws IOException
	{
		switch (CraftConfig.gcodeType)
		{
		case CraftConfig.GCODE_FULL:
			fileWriter.write("G1 X" + xyzFormat.format(x) + " Y" + xyzFormat.format(y) + " F" + fFormat.format(feedRate * 60) + "; " + comment + "\n");
			break;
		case CraftConfig.GCODE_COMPACT:
			if (feedRate != lastFeedrate)
				fileWriter.write("G1 X" + xyzFormat.format(x) + " Y" + xyzFormat.format(y) + " F" + fFormat.format(feedRate * 60) + "\n");
			else
				fileWriter.write("G1 X" + xyzFormat.format(x) + " Y" + xyzFormat.format(y) + "\n");
			break;
		case CraftConfig.GCODE_TINY_COMPACT:
			if (feedRate != lastFeedrate)
				fileWriter.write("G1X" + xyzFormat.format(x) + "Y" + xyzFormat.format(y) + "F" + fFormat.format(feedRate * 60) + "\n");
			else
				fileWriter.write("G1X" + xyzFormat.format(x) + "Y" + xyzFormat.format(y) + "\n");
			break;
		}
		
		doMove(x, y, oldPos.z, feedRate);
	}
	
	public void writeMoveXYE(double x, double y, double e, double feedRate, String comment) throws IOException
	{
		totalExtruderValue += e;
		switch (CraftConfig.gcodeType)
		{
		case CraftConfig.GCODE_FULL:
			fileWriter.write("G1 X" + xyzFormat.format(x) + " Y" + xyzFormat.format(y) + " E" + eFormat.format(totalExtruderValue) + " F" + fFormat.format(feedRate * 60) + "; " + comment + "\n");
			break;
		case CraftConfig.GCODE_COMPACT:
			if (lastFeedrate != feedRate)
				fileWriter.write("G1 X" + xyzFormat.format(x) + " Y" + xyzFormat.format(y) + " E" + eFormat.format(totalExtruderValue) + " F" + fFormat.format(feedRate * 60) + "\n");
			else
				fileWriter.write("G1 X" + xyzFormat.format(x) + " Y" + xyzFormat.format(y) + " E" + eFormat.format(totalExtruderValue) + "\n");
			break;
		case CraftConfig.GCODE_TINY_COMPACT:
			if (lastFeedrate != feedRate)
				fileWriter.write("G1X" + xyzFormat.format(x) + "Y" + xyzFormat.format(y) + "E" + eFormat.format(totalExtruderValue) + "F" + fFormat.format(feedRate * 60) + "\n");
			else
				fileWriter.write("G1X" + xyzFormat.format(x) + "Y" + xyzFormat.format(y) + "E" + eFormat.format(totalExtruderValue) + "\n");
			break;
		}
		
		doMove(x, y, oldPos.z, feedRate);
	}
	
	public void writeComment(String string) throws IOException
	{
		switch (CraftConfig.gcodeType)
		{
		case CraftConfig.GCODE_FULL:
			fileWriter.write("; " + string + "\n");
			break;
		case CraftConfig.GCODE_COMPACT:
			break;
		case CraftConfig.GCODE_TINY_COMPACT:
			break;
		}
	}
	
	public void write(String string) throws IOException
	{
		fileWriter.write(string + "\n");
	}
	
	public void close() throws IOException
	{
		fileWriter.close();
	}
	
	private void doMove(double x, double y, double z, double feedRate)
	{
		double dist = oldPos.sub(new Vector3(x, y, z)).vSize();
		buildTime += dist / feedRate;
		lastFeedrate = feedRate;
	}
	
	public double getBuildTime()
	{
		return buildTime;
	}
}
