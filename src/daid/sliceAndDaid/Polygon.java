package daid.sliceAndDaid;

import java.util.Iterator;

import daid.sliceAndDaid.util.AABBrect;
import daid.sliceAndDaid.util.Vector2;

public class Polygon implements Iterable<Segment2D>
{
	private Segment2D first = null;
	private boolean endToFirst = false;
	
	public Polygon()
	{
	}
	
	public Polygon(Segment2D segment)
	{
		first = segment;
		for (Segment2D s = first.next; s != null; s = s.next)
		{
			if (s == first)
			{
				endToFirst = true;
				break;
			}
		}
	}
	
	/**
	 * Get the closest segment in this segment loop
	 */
	public Segment2D closestTo(Vector2 p)
	{
		Segment2D best = first;
		double bestDist = 99999;
		for (Segment2D s : this)
		{
			if (s.start.sub(p).vSize2() < bestDist)
			{
				bestDist = s.start.sub(p).vSize2();
				best = s;
			}
		}
		return best;
	}
	
	public AABBrect getAABB()
	{
		AABBrect ret = new AABBrect(first);
		for (Segment2D s : this)
		{
			ret.addAABB(s);
		}
		return ret;
	}
	
	public Iterator<Segment2D> iterator()
	{
		return new Segment2DIterator(first);
	}
	
	private class Segment2DIterator implements Iterator<Segment2D>
	{
		private Segment2D start;
		private Segment2D next;
		
		public Segment2DIterator(Segment2D start)
		{
			this.start = start;
			this.next = start;
		}
		
		public boolean hasNext()
		{
			return next != null;
		}
		
		public Segment2D next()
		{
			Segment2D ret = next;
			next = next.next;
			if (next == start)
				next = null;
			return ret;
		}
		
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * removeEnd removes this segment from the segment list, and links up the next segment to the
	 * previous. Removing 1 point in the polygon. The point removed is the endpoint of this segment.
	 */
	public void removeEnd(Segment2D s)
	{
		if (s.next == null)
		{
			// Remove 's' from the linked list.
			s.prev = null;
		}else{
			// Update the start point of s.next to the end of the previous point. Effectively removing
			// s.end from the polygon.
			s.next.update(s.prev.end, s.next.end);
			// Remove 's' from the linked list.
			// We can set 's.next' to null here, even if we are iterating over 's',
			// because the next point of iteration has already been stored by the iterator.
			s.next.prev = s.prev;
			s.prev.next = s.next;
			s.prev = null;
			s.next = null;
		}
	}

	public Segment2D cutPoly(Segment2D s)
	{
		if (!endToFirst)
			throw new UnsupportedOperationException();
		endToFirst = false;
		Segment2D ret = s.prev;
		ret.next = null;
		s.prev = null;
		return ret;
	}
}
