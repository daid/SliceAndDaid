package daid.sliceAndDaid.util;

import daid.sliceAndDaid.Segment2D;

/**
 * The triangle class represents a 3D triangle in a 3D model
 */
public class Triangle
{
	public Vector3[] point = new Vector3[3];
	
	public Segment2D project2D(double layerZ)
	{
		Segment2D ret = null;
		
		if (point[0].z < layerZ && point[1].z >= layerZ && point[2].z >= layerZ)
			ret = setSegment(ret, layerZ, point[0], point[2], point[1]);
		else if (point[0].z > layerZ && point[1].z <= layerZ && point[2].z <= layerZ)
			ret = setSegment(ret, layerZ, point[0], point[1], point[2]);
		
		else if (point[1].z < layerZ && point[0].z >= layerZ && point[2].z >= layerZ)
			ret = setSegment(ret, layerZ, point[1], point[0], point[2]);
		else if (point[1].z > layerZ && point[0].z <= layerZ && point[2].z <= layerZ)
			ret = setSegment(ret, layerZ, point[1], point[2], point[0]);
		
		else if (point[2].z < layerZ && point[1].z >= layerZ && point[0].z >= layerZ)
			ret = setSegment(ret, layerZ, point[2], point[1], point[0]);
		else if (point[2].z > layerZ && point[1].z <= layerZ && point[0].z <= layerZ)
			ret = setSegment(ret, layerZ, point[2], point[0], point[1]);
		else
		{
			// System.err.println("Cannot handle triangle:\n" + point[0] + "\n" + point[1] + "\n" + point[2] + "\non Z: " + layerZ);
			return null;
		}
		if (Double.isNaN(ret.start.x) || Double.isNaN(ret.end.x))
		{
			System.err.println("Error on triangle:\n" + point[0] + "\n" + point[1] + "\n" + point[2] + "\non Z: " + layerZ);
		}
		
		return ret;
	}
	
	private Segment2D setSegment(Segment2D ret, double layerZ, Vector3 v0, Vector3 v1, Vector3 v2)
	{
		double a1 = (layerZ - v0.z) / (v1.z - v0.z);
		double a2 = (layerZ - v0.z) / (v2.z - v0.z);
		Vector2 start = new Vector2(v0.x + (v1.x - v0.x) * a1, v0.y + (v1.y - v0.y) * a1);
		Vector2 end = new Vector2(v0.x + (v2.x - v0.x) * a2, v0.y + (v2.y - v0.y) * a2);
		return new Segment2D(Segment2D.TYPE_MODEL_SLICE, start, end);
	}
	
	public Vector3 getNormal()
	{
		return point[1].sub(point[0]).cross(point[2].sub(point[0])).normal();
	}
}
