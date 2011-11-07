package daid.sliceAndDaid;

import java.util.HashSet;
import java.util.Vector;

import daid.sliceAndDaid.config.CraftConfig;
import daid.sliceAndDaid.util.AABBTree;
import daid.sliceAndDaid.util.AABBrect;

public class Layer
{
	public int layerNr;
	
	public Vector<Segment2D> segmentList = new Vector<Segment2D>();
	
	public Segment2D pathStart;
	public LayerPart modelPart = new LayerPart();
	public LayerPart[] outlinePart;
	private AABBTree<Segment2D> modelSegmentTree = new AABBTree<Segment2D>();
	
	public Layer(int layerNr, double minX, double minY, double maxX, double maxY)
	{
		this.layerNr = layerNr;
		this.outlinePart = new LayerPart[CraftConfig.outlineCount];
		for (int i = 0; i < CraftConfig.outlineCount; i++)
			this.outlinePart[i] = new LayerPart();
	}
	
	public void addModelSegment(Segment2D segment)
	{
		if (segment.start.asGoodAsEqual(segment.end))
			return;
		modelSegmentTree.insert(segment);
		segmentList.add(segment);
	}
	
	private void removeModelSegment(Segment2D segment)
	{
		segmentList.remove(segment);
		modelSegmentTree.remove(segment);
	}
	
	public boolean optimize()
	{
		// Link up the segments with start/ends, so polygons are created.
		for (Segment2D s1 : segmentList)
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
		
		for (Segment2D s : segmentList)
		{
			if (s.prev != null && s.prev.next != s)
				throw new RuntimeException();
			if (s.next != null && s.next.prev != s)
				throw new RuntimeException();
			if (s.next != null && !segmentList.contains(s.next))
				throw new RuntimeException();
			if (s.prev != null && !segmentList.contains(s.prev))
				throw new RuntimeException();
		}
		
		boolean manifoldErrorReported = false;
		HashSet<Segment2D> tmpSet = new HashSet<Segment2D>(segmentList);
		while (tmpSet.size() > 0)
		{
			Segment2D start = tmpSet.iterator().next();
			boolean manifold = false;
			for (Segment2D s = start; s != null; s = s.next)
			{
				if (!tmpSet.contains(s))
				{
					System.err.println("Problem in layer: " + layerNr);
					System.err.println("Tried to create a segment link from links that where already used...");
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
				for (Segment2D s : start)
				{
					tmpSet.remove(s);
				}
				addModelPolygon(start);
			} else
			{
				if (!manifoldErrorReported)
					System.err.println("Object not manifold in layer: " + layerNr);
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
	
	private void addModelPolygon(Segment2D start)
	{
		for (Segment2D s = start.next; s != start; s = s.next)
		{
			if (s.normal.dot(s.next.normal) > CraftConfig.joinMinCosAngle)
			{
				removeModelSegment(s.next);
				if (s.next == start)
					start = s.next.next;
				
				modelSegmentTree.remove(s);
				s.update(s.start, s.next.end);
				modelSegmentTree.insert(s);
				s.next = s.next.next;
				s.next.prev = s;
				if (s.prev != start)
					s = s.prev;
			}
		}
		modelPart.polygons.add(start);
	}
}
