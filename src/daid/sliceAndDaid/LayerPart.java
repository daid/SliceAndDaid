package daid.sliceAndDaid;

import java.util.Iterator;
import java.util.Vector;

import daid.sliceAndDaid.util.AABBTree;
import daid.sliceAndDaid.util.AABBrect;
import daid.sliceAndDaid.util.Vector2;

public class LayerPart implements Iterable<Polygon>
{
	private Layer layer;
	private Vector<Polygon> polygons = new Vector<Polygon>();
	
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
	
	public void addPolygon(Polygon poly)
	{
		poly.check();
		if (poly.empty())
		{
			return;
		}
		polygons.add(poly);
	}
	
	/**
	 * makeConvex is used to generate a single convex polygon from the existing polygon set.
	 * 
	 * This is used for the skirt. Right now it creates a square box around the object.
	 */
	public LayerPart makeConvex()
	{
		LayerPart ret = new LayerPart(this);
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		for (Polygon p : polygons)
		{
			for (Segment2D s : p)
			{
				if (s.start.x < minX)
					minX = s.start.x;
				if (s.start.y < minY)
					minY = s.start.y;
				if (s.start.x > maxX)
					maxX = s.start.x;
				if (s.start.y > maxY)
					maxY = s.start.y;
			}
		}
		Polygon p = new Polygon();
		p.addEnd(new Segment2D(Segment2D.TYPE_PERIMETER, new Vector2(minX, minY), new Vector2(minX, maxY)));
		p.addEnd(new Segment2D(Segment2D.TYPE_PERIMETER, new Vector2(minX, maxY), new Vector2(maxX, maxY)));
		p.addEnd(new Segment2D(Segment2D.TYPE_PERIMETER, new Vector2(maxX, maxY), new Vector2(maxX, minY)));
		p.addEnd(new Segment2D(Segment2D.TYPE_PERIMETER, new Vector2(maxX, minY), new Vector2(minX, minY)));
		p.close();
		ret.addPolygon(p);
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
