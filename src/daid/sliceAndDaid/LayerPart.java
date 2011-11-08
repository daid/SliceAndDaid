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
		layer.segmentList.add(newSeg);
		tree.insert(newSeg);
	}
}
