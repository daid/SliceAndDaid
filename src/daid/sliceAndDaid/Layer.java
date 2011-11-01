package daid.sliceAndDaid;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import daid.sliceAndDaid.config.CraftConfig;
import daid.sliceAndDaid.util.Vector2;

public class Layer
{
	private final int SEGMENT_SET_SIZE = 32;
	
	public int layerNr;
	public Vector<Segment2D> segmentList = new Vector<Segment2D>();
	private Vector<Segment2D> modelSegmentList = new Vector<Segment2D>();
	public Vector<Segment2D> modelSegmentOutlines = new Vector<Segment2D>();
	
	private Set<Segment2D>[] segmentSets;
	private double minX, minY;
	private double segmentSetSizeX, segmentSetSizeY;
	
	@SuppressWarnings("unchecked")
	public Layer(int layerNr, double minX, double minY, double maxX, double maxY)
	{
		this.layerNr = layerNr;
		this.minX = minX;
		this.minY = minY;
		segmentSetSizeX = (maxX - minX) / SEGMENT_SET_SIZE;
		segmentSetSizeY = (maxY - minY) / SEGMENT_SET_SIZE;
		segmentSets = new Set[SEGMENT_SET_SIZE * SEGMENT_SET_SIZE];
		for (int x = 0; x < SEGMENT_SET_SIZE; x++)
		{
			for (int y = 0; y < SEGMENT_SET_SIZE; y++)
			{
				segmentSets[x + y * SEGMENT_SET_SIZE] = new HashSet<Segment2D>();
			}
		}
	}
	
	public void addModelSegment(Segment2D segment)
	{
		if (segment.start.asGoodAsEqual(segment.end))
			return;
		segmentList.add(segment);
		modelSegmentList.add(segment);
		getSegmentSetFor(segment.start).add(segment);
		getSegmentSetFor(segment.start.add(new Vector2(segmentSetSizeX / 2, segmentSetSizeY / 2))).add(segment);
		getSegmentSetFor(segment.start.add(new Vector2(-segmentSetSizeX / 2, segmentSetSizeY / 2))).add(segment);
		getSegmentSetFor(segment.start.add(new Vector2(segmentSetSizeX / 2, -segmentSetSizeY / 2))).add(segment);
		getSegmentSetFor(segment.start.add(new Vector2(-segmentSetSizeX / 2, -segmentSetSizeY / 2))).add(segment);
		getSegmentSetFor(segment.end).add(segment);
		getSegmentSetFor(segment.end.add(new Vector2(segmentSetSizeX / 2, segmentSetSizeY / 2))).add(segment);
		getSegmentSetFor(segment.end.add(new Vector2(-segmentSetSizeX / 2, segmentSetSizeY / 2))).add(segment);
		getSegmentSetFor(segment.end.add(new Vector2(segmentSetSizeX / 2, -segmentSetSizeY / 2))).add(segment);
		getSegmentSetFor(segment.end.add(new Vector2(-segmentSetSizeX / 2, -segmentSetSizeY / 2))).add(segment);
	}
	
	private void removeModelSegment(Segment2D segment)
	{
		segmentList.remove(segment);
		modelSegmentList.remove(segment);
		getSegmentSetFor(segment.start).remove(segment);
		getSegmentSetFor(segment.start.add(new Vector2(segmentSetSizeX / 2, segmentSetSizeY / 2))).remove(segment);
		getSegmentSetFor(segment.start.add(new Vector2(-segmentSetSizeX / 2, segmentSetSizeY / 2))).remove(segment);
		getSegmentSetFor(segment.start.add(new Vector2(segmentSetSizeX / 2, -segmentSetSizeY / 2))).remove(segment);
		getSegmentSetFor(segment.start.add(new Vector2(-segmentSetSizeX / 2, -segmentSetSizeY / 2))).remove(segment);
		getSegmentSetFor(segment.end).add(segment);
		getSegmentSetFor(segment.end.add(new Vector2(segmentSetSizeX / 2, segmentSetSizeY / 2))).remove(segment);
		getSegmentSetFor(segment.end.add(new Vector2(-segmentSetSizeX / 2, segmentSetSizeY / 2))).remove(segment);
		getSegmentSetFor(segment.end.add(new Vector2(segmentSetSizeX / 2, -segmentSetSizeY / 2))).remove(segment);
		getSegmentSetFor(segment.end.add(new Vector2(-segmentSetSizeX / 2, -segmentSetSizeY / 2))).remove(segment);
	}
	
	private Set<Segment2D> getSegmentSetFor(Vector2 p)
	{
		int x = (int) ((p.x - minX) / segmentSetSizeX);
		int y = (int) ((p.y - minY) / segmentSetSizeY);
		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		if (x >= SEGMENT_SET_SIZE)
			x = SEGMENT_SET_SIZE - 1;
		if (y >= SEGMENT_SET_SIZE)
			y = SEGMENT_SET_SIZE - 1;
		return segmentSets[x + y * SEGMENT_SET_SIZE];
	}
	
	public boolean optimize()
	{
		// Link up the segments with start/ends.
		for (Segment2D s1 : modelSegmentList)
		{
			if (s1.prev == null)
			{
				for (Segment2D s2 : getSegmentSetFor(s1.start))
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
				for (Segment2D s2 : getSegmentSetFor(s1.end))
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
		// Vector<Segment2D> tmpSet = new Vector<Segment2D>(modelSegmentList);
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
				addOutline(start);
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
	
	private void addOutline(Segment2D start)
	{
		for (Segment2D s = start.next; s != start; s = s.next)
		{
			if (s.normal.dot(s.next.normal) > CraftConfig.joinMinCosAngle)
			{
				removeModelSegment(s.next);
				if (s.next == start)
					start = s.next.next;
				
				s.end = s.next.end;
				s.updateNormal();
				s.next = s.next.next;
				s.next.prev = s;
				if (s.prev != start)
					s = s.prev;
			}
		}
		modelSegmentOutlines.add(start);
	}
}
