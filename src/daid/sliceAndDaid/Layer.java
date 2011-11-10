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
			if (s1.prev == null)
			{
				for (Segment2D s2 : modelSegmentTree.query(new AABBrect(s1.start, s1.start)))
				{
					if (s1 != s2 && s1.start.asGoodAsEqual(s2.end) && s2.next == null)
					{
						s1.start = s2.end;
						s1.prev = s2;
						s2.next = s1;
						break;
					}
				}
			}
			if (s1.next == null)
			{
				for (Segment2D s2 : modelSegmentTree.query(new AABBrect(s1.end, s1.end)))
				{
					if (s1 != s2 && s1.end.asGoodAsEqual(s2.start) && s2.prev == null)
					{
						s1.end = s2.start;
						s1.next = s2;
						s2.prev = s1;
						break;
					}
				}
			}
		}
		
		for (Segment2D s : modelSegmentList)
		{
			if (s.prev != null && s.prev.next != s)
				throw new RuntimeException();
			if (s.next != null && s.next.prev != s)
				throw new RuntimeException();
			if (s.next != null && !modelSegmentList.contains(s.next))
				throw new RuntimeException();
			if (s.prev != null && !modelSegmentList.contains(s.prev))
				throw new RuntimeException();
		}
		
		boolean manifoldErrorReported = false;
		HashSet<Segment2D> tmpSet = new HashSet<Segment2D>(modelSegmentList);
		while (tmpSet.size() > 0)
		{
			Segment2D start = tmpSet.iterator().next();
			boolean manifold = false;
			for (Segment2D s = start; s != null; s = s.next)
			{
				if (!tmpSet.contains(s))
				{
					Logger.warning("Problem in layer: " + layerNr + "\nTried to create a segment link from links that where already used...");
					break;
				}
				if (s.next == start)
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
				for (Segment2D s = start; s != null; s = s.next)
				{
					tmpSet.remove(s);
					s.type = Segment2D.TYPE_ERROR;
					if (s.next == start)
						break;
				}
				for (Segment2D s = start; s != null; s = s.prev)
				{
					tmpSet.remove(s);
					s.type = Segment2D.TYPE_ERROR;
					if (s.prev == start)
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
			if (s.normal.dot(s.next.normal) > CraftConfig.joinMinCosAngle)
			{
				removeModelSegment(s);
				Segment2D next = s.next;
				modelSegmentTree.remove(next);
				poly.removeEnd(s);
				modelSegmentTree.insert(next);
			}
		}
		modelPart.polygons.add(poly);
	}
}
