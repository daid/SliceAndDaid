package daid.sliceAndDaid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import daid.sliceAndDaid.util.Logger;
import daid.sliceAndDaid.util.Triangle;
import daid.sliceAndDaid.util.Vector3;

public class Model
{
	public Vector<Triangle> triangles;
	
	public Model(String filename) throws IOException
	{
		Logger.updateStatus("Loading: " + filename);
		
		if (filename.toLowerCase().endsWith(".stl"))
		{
			char[] buf = new char[5];
			BufferedReader br = new BufferedReader(new FileReader(filename));
			br.mark(5);
			br.read(buf);
			br.close();
			String header = new String(buf);
			
			if (header.equals("solid"))
				readAsciiSTL(filename);
			else
				readBinarySTL(filename);
		} else
		{
			new RuntimeException("Unknown model format: " + filename);
		}
		Logger.message("Triangle count: " + triangles.size());
	}
	
	public Vector3 getMin()
	{
		Vector3 ret = new Vector3();
		ret.x = Double.MAX_VALUE;
		ret.y = Double.MAX_VALUE;
		ret.z = Double.MAX_VALUE;
		for (Triangle t : triangles)
		{
			for (int i = 0; i < 3; i++)
			{
				if (ret.x > t.point[i].x)
					ret.x = t.point[i].x;
				if (ret.y > t.point[i].y)
					ret.y = t.point[i].y;
				if (ret.z > t.point[i].z)
					ret.z = t.point[i].z;
			}
		}
		return ret;
	}
	
	public Vector3 getMax()
	{
		Vector3 ret = new Vector3();
		ret.x = Double.MIN_VALUE;
		ret.y = Double.MIN_VALUE;
		ret.z = Double.MIN_VALUE;
		for (Triangle t : triangles)
		{
			for (int i = 0; i < 3; i++)
			{
				if (ret.x < t.point[i].x)
					ret.x = t.point[i].x;
				if (ret.y < t.point[i].y)
					ret.y = t.point[i].y;
				if (ret.z < t.point[i].z)
					ret.z = t.point[i].z;
			}
		}
		return ret;
	}
	
	private void readBinarySTL(String filename) throws IOException
	{
		RandomAccessFile raf = new RandomAccessFile(filename, "r");
		byte[] header = new byte[80];
		raf.read(header);
		int triangleCount = Integer.reverseBytes(raf.readInt());
		triangles = new Vector<Triangle>();
		for (int i = 0; i < triangleCount; i++)
		{
			Logger.setProgress(i, triangleCount);
			for (int j = 0; j < 3; j++)
				raf.readFloat();
			
			Triangle t = new Triangle();
			float x = Float.intBitsToFloat(Integer.reverseBytes(raf.readInt()));
			float y = Float.intBitsToFloat(Integer.reverseBytes(raf.readInt()));
			float z = Float.intBitsToFloat(Integer.reverseBytes(raf.readInt()));
			t.point[0] = new Vector3(x, y, z);
			x = Float.intBitsToFloat(Integer.reverseBytes(raf.readInt()));
			y = Float.intBitsToFloat(Integer.reverseBytes(raf.readInt()));
			z = Float.intBitsToFloat(Integer.reverseBytes(raf.readInt()));
			t.point[1] = new Vector3(x, y, z);
			x = Float.intBitsToFloat(Integer.reverseBytes(raf.readInt()));
			y = Float.intBitsToFloat(Integer.reverseBytes(raf.readInt()));
			z = Float.intBitsToFloat(Integer.reverseBytes(raf.readInt()));
			t.point[2] = new Vector3(x, y, z);
			raf.readShort();// flags
			triangles.add(t);
		}
		System.out.println(getMin());
	}
	
	private void readAsciiSTL(String filename) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		int i = 0;
		Vector3 normal = null;
		Triangle nextTri = new Triangle();
		triangles = new Vector<Triangle>();
		while ((line = br.readLine()) != null)
		{
			line = line.trim();
			if (line.startsWith("facet normal"))
			{
				String[] parts = line.split(" +");
				normal = new Vector3(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Double.parseDouble(parts[4]));
			}
			if (line.startsWith("vertex"))
			{
				String[] parts = line.split(" +");
				nextTri.point[i] = new Vector3(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
				i++;
				if (i == 3)
				{
					if (normal.vSize2() > 0.1 && nextTri.getNormal().dot(normal) < 0.5)
					{
						// Triangle winding order and normal don't point in the same direction...
						// Flip the triangle?
					}
					triangles.add(nextTri);
					nextTri = new Triangle();
					i = 0;
				}
			}
		}
		br.close();
	}
	
	public void center()
	{
		Vector3 min = getMin();
		Vector3 max = getMax();
		Vector3 translate = new Vector3();
		translate.z = -min.z;
		translate.x = -(max.x + min.x) / 2;
		translate.y = -(max.y + min.y) / 2;
		
		move(translate);
	}
	
	private void move(Vector3 translate)
	{
		for (Triangle t : triangles)
		{
			for (int i = 0; i < 3; i++)
			{
				t.point[i].addToSelf(translate);
			}
		}
	}
}
