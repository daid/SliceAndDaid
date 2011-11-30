package daid.sliceAndDaid;

import java.util.HashSet;
import java.util.Vector;

import daid.sliceAndDaid.config.CraftConfig;
import daid.sliceAndDaid.util.AABBTree;
import daid.sliceAndDaid.util.AABBrect;
import daid.sliceAndDaid.util.Logger;

public class Layer
{
	public int layerNr;
	
	public Vector<Segment2D> modelSegmentList = new Vector<Segment2D>();
	
	public Segment2D pathStart;
	public LayerPart skirt = null;
	public LayerPart modelPart = new LayerPart(this);
	public LayerPart[] outlinePart;
	private AABBTree<Segment2D> modelSegmentTree = new AABBTree<Segment2D>();
	
	public Layer(int layerNr, double minX, double minY, double maxX, double maxY)
	{
		this.layerNr = layerNr;
		this.outlinePart = new LayerPart[CraftConfig.perimeterCount];
	}
	
	public void addModelSegment(Segment2D segment)
	{
		if (segment.start.asGoodAsEqual(segment.end))
			return;
		modelSegmentTree.insert(segment);
		modelSegmentList.add(segment);
	}
	
	private void removeModelSegment(Segment2D segment)
	{
		modelSegmentList.remove(segment);
		modelSegmentTree.remove(segment);
	}
	
	public boolean optimize()
	{
		// Link up the segments with start/ends, so polygons are created.
		for (Segment2D s1 : modelSegmentList)
		{
			if (s1.getPrev() == null)
			{
				Segment2D best = null;
				double bestDist2 = 0.01;
				for (Segment2D s2 : modelSegmentTree.query(new AABBrect(s1.start, s1.start)))
				{
					if (s1 != s2 && s2.getNext() == null && s1.start.asGoodAsEqual(s2.end) && s1.start.sub(s2.end).vSize2() < bestDist2)
					{
						best = s2;
						bestDist2 = s1.start.sub(s2.end).vSize2();
						break;
					}
				}
				if (best != null)
				{
					s1.start = best.end;
					best.setNext(s1);
				}
			}
			if (s1.getNext() == null)
			{
				Segment2D best = null;
				double bestDist2 = 0.01;
				for (Segment2D s2 : modelSegmentTree.query(new AABBrect(s1.end, s1.end)))
				{
					if (s1 != s2 && s2.getPrev() == null && s1.end.asGoodAsEqual(s2.start) && s1.end.sub(s2.start).vSize2() < bestDist2)
					{
						best = s2;
						bestDist2 = s1.end.sub(s2.start).vSize2();
						break;
					}
				}
				if (best != null)
				{
					s1.end = best.start;
					s1.setNext(best);
				}
			}
		}
		
		for (Segment2D s : modelSegmentList)
		{
			if (s.getPrev() != null && s.getPrev().getNext() != s)
				throw new RuntimeException();
			if (s.getNext() != null && s.getNext().getPrev() != s)
				throw new RuntimeException();
			if (s.getNext() != null && !modelSegmentList.contains(s.getNext()))
				throw new RuntimeException();
			if (s.getPrev() != null && !modelSegmentList.contains(s.getPrev()))
				throw new RuntimeException();
		}
		
		boolean manifoldErrorReported = false;
		HashSet<Segment2D> tmpSet = new HashSet<Segment2D>(modelSegmentList);
		while (tmpSet.size() > 0)
		{
			Segment2D start = tmpSet.iterator().next();
			boolean manifold = false;
			for (Segment2D s = start; s != null; s = s.getNext())
			{
				if (!tmpSet.contains(s))
				{
					Logger.warning("Problem in layer: " + layerNr + "\nTried to create a segment link from links that where already used...");
					break;
				}
				if (s.getNext() == start)
				{
					manifold = true;
					break;
				}
			}
			if (manifold)
			{
				Polygon poly = new Polygon(start);
				for (Segment2D s : poly)
				{
					tmpSet.remove(s);
				}
				addModelPolygon(poly);
			} else
			{
				if (!manifoldErrorReported)
					Logger.warning("Object not manifold in layer: " + layerNr);
				manifoldErrorReported = true;
				for (Segment2D s = start; s != null; s = s.getNext())
				{
					tmpSet.remove(s);
					s.setType(Segment2D.TYPE_ERROR);
					if (s.getNext() == start)
						break;
				}
				for (Segment2D s = start; s != null; s = s.getPrev())
				{
					tmpSet.remove(s);
					s.setType(Segment2D.TYPE_ERROR);
					if (s.getPrev() == start)
						break;
				}
			}
		}
		return manifoldErrorReported;
	}
	
	private void addModelPolygon(Polygon poly)
	{
		for (Segment2D s : poly)
		{
			if (s.getNormal().dot(s.getNext().getNormal()) > CraftConfig.joinMinCosAngle)
			{
				removeModelSegment(s);
				Segment2D next = s.getNext();
				modelSegmentTree.remove(next);
				poly.remove(s);
				modelSegmentTree.insert(next);
			}
		}
		modelPart.addPolygon(poly);
	}
}
