package daid.sliceAndDaid.util;

import java.io.FileWriter;
import java.io.IOException;

public class GCodeFile
{
	private FileWriter fileWriter;
	private double totalExtruderValue = 0;
	
	public GCodeFile(FileWriter fileWriter)
	{
		this.fileWriter = fileWriter;
	}
	
	public void writeMoveZ(double z, double feedRate, String comment) throws IOException
	{
		fileWriter.write("G1 Z" + z + " F" + (feedRate * 60) + "; " + comment + "\n");
	}
	
	public void writeMoveXY(double x, double y, double feedRate, String comment) throws IOException
	{
		fileWriter.write("G1 X" + x + " Y" + y + " F" + (feedRate * 60) + "; " + comment + "\n");
	}
	
	public void writeMoveXYE(double x, double y, double e, double feedRate, String comment) throws IOException
	{
		totalExtruderValue += e;
		fileWriter.write("G1 X" + x + " Y" + y + " E" + totalExtruderValue + " F" + (feedRate * 60) + "; " + comment + "\n");
	}
	
	public void writeComment(String string) throws IOException
	{
		fileWriter.write("G21; " + string + "\n");
	}
	
	public void write(String string) throws IOException
	{
		fileWriter.write(string + "\n");
	}
	
	public void close() throws IOException
	{
		fileWriter.close();
	}
}
