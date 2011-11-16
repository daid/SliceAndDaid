package daid.sliceAndDaid;

import java.util.Iterator;
import java.util.Vector;

import daid.sliceAndDaid.util.AABBTree;
import daid.sliceAndDaid.util.AABBrect;

public class LayerPart implements Iterable<Polygon>
{
	private Layer layer;
	private Vector<Polygon> polygons = new Vector<Polygon>();
	public AABBTree<Segment2D> tree = new AABBTree<Segment2D>();
	
	public LayerPart(Layer layer)
	{
		this.layer = layer;
	}
	
	public LayerPart(LayerPart layerPart)
	{
		this.layer = layerPart.layer;
	}
	
	public Polygon getLargestPolygon()
	{
		Polygon largestPoly = null;
		double largestPolySize = 0;
		for (Polygon poly : polygons)
		{
			AABBrect polygonRect = poly.getAABB();
			
			if (polygonRect.getPerimeter() > largestPolySize)
			{
				largestPolySize = polygonRect.getPerimeter();
				largestPoly = poly;
			}
		}
		return largestPoly;
	}
	
	public void add(Segment2D newSeg)
	{
		tree.insert(newSeg);
	}
	
	public void addPolygon(Polygon poly)
	{
		poly.check();
		if (poly.empty())
		{
			System.out.println("E");
			return;
		}
		polygons.add(poly);
	}
	
	/**
	 * makeConvex is used to generate a single convex polygon from the existing polygon set.
	 * 
	 * This is used for the skirt. And it's currently not working correctly.
	 */
	public LayerPart makeConvex()
	{
		Polygon poly = getLargestPolygon();
		LayerPart ret = new LayerPart(this);
		if (poly == null)
			return ret;

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
		ret.polygons.add(new Polygon(first));
		
		return ret;
	}

	public Iterator<Polygon> iterator()
	{
		return polygons.iterator();
	}

	public Vector<Polygon> getPolygonListClone()
	{
		return new Vector<Polygon>(polygons);
	}
}
