package daid.sliceAndDaid;

import java.util.Vector;

import daid.sliceAndDaid.util.AABBTree;
import daid.sliceAndDaid.util.AABBrect;

public class LayerPart
{
	private Layer layer;
	public Vector<Segment2D> polygons = new Vector<Segment2D>();
	public AABBTree<Segment2D> tree = new AABBTree<Segment2D>();
	
	public LayerPart(Layer layer)
	{
		this.layer = layer;
	}
	
	public LayerPart(LayerPart layerPart)
	{
		this.layer = layerPart.layer;
	}
	
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
	
	public void add(Segment2D newSeg)
	{
		tree.insert(newSeg);
	}
	
	/**
	 * makeConvex is used to generate a single convex polygon from the existing polygon set.
	 * 
	 * This is used for the skirt. And it's currently not working correctly.
	 */
	public LayerPart makeConvex()
	{
		Segment2D poly = getLargestPolygon();
		LayerPart ret = new LayerPart(this);

		Segment2D first = null;
		Segment2D prev = null;
		for(Segment2D s1 : poly)
		{
			Segment2D s = new Segment2D(Segment2D.TYPE_PERIMETER, s1.start, s1.end);
			
			if (prev == null)
			{
				first = s;
			} else
			{
				prev.next = s;
				s.prev = prev;
			}
			prev = s;
		}
		first.prev = prev;
		prev.next = first;
		ret.polygons.add(first);
		
		return ret;
	}
}
