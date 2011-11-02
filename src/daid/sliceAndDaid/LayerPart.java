package daid.sliceAndDaid;

import java.util.Vector;

import daid.sliceAndDaid.util.AABBrect;

public class LayerPart
{
	public Vector<Segment2D> polygons = new Vector<Segment2D>();
	
	public Segment2D getLargestPolygon()
	{
		Segment2D largestPoly = null;
		double largestPolySize = 0;
		for (Segment2D start : polygons)
		{
			AABBrect polygonRect = new AABBrect(start);
			for (Segment2D s : start)
			{
				polygonRect.addAABB(s);
			}
			if (polygonRect.getPerimeter() > largestPolySize)
			{
				largestPolySize = polygonRect.getPerimeter();
				largestPoly = start;
			}
		}
		return largestPoly;
	}
}
