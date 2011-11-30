package daid.sliceAndDaid;

import java.util.Iterator;

import daid.sliceAndDaid.util.AABBrect;
import daid.sliceAndDaid.util.Vector2;

public class Polygon implements Iterable<Segment2D>
{
	private Segment2D first = null;
	private Segment2D last = null;
	private boolean enclosed = false;

	public Polygon()
	{
	}

	public Polygon(Segment2D segment)
	{
		first = segment;
		if (first == null)
			return;
		last = first;
		for (Segment2D s = first.getNext(); s != null; s = s.getNext())
		{
			if (s == first)
			{
				enclosed = true;
				break;
			}
			last = s;
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

	public void addEnd(Segment2D s)
	{
		if (enclosed)
			throw new RuntimeException();
		if (first == null)
		{
			first = s;
		}
		if (last != null)
			last.setNext(s);
		last = s;
	}

	/**
	 * removeEnd removes this segment from the segment list, and links up the next segment to the previous. Removing 1 point in the polygon. The point removed
	 * is the endpoint of this segment.
	 */
	public void remove(Segment2D s)
	{
		if (s == first)
		{
			first = s.getNext();
			//In case we are enclosed with a single segment, the next is the same one. So we are back to an empty polygon.
			if (first == s)
				first = null;
		}
		if (s == last)
		{
			last = last.getPrev();
			//In case we are enclosed with a single segment, the prev is the same one. So we are back to an empty polygon.
			if (last == s)
				last = null;
		}

		if (s.getNext() == null)
		{
			if (enclosed)
				throw new RuntimeException();
			// Remove 's' from the linked list.
			s.getPrev().setNext(null);
		} else
		{
			// Update the start point of s.next to the end of the previous point. Effectively removing
			// s.end from the polygon.
			s.getNext().update(s.getPrev().end, s.getNext().end);
			// Remove 's' from the linked list.
			// We can set 's.next' to null here, even if we are iterating over 's',
			// because the next point of iteration has already been stored by the iterator.
			Segment2D prev = s.getPrev();
			Segment2D next = s.getNext();
			prev.setNext(null);
			s.setNext(null);
			prev.setNext(next);
		}
	}

	public void close()
	{
		if (enclosed)
			throw new UnsupportedOperationException();
		check();
		enclosed = true;
		last.setNext(first);
		check();
	}

	public Segment2D cutPoly(Segment2D s)
	{
		if (!enclosed)
			throw new UnsupportedOperationException();
		enclosed = false;
		Segment2D ret = s.getPrev();
		ret.setNext(null);
		return ret;
	}

	public void check()
	{
		if (first == null)
			return;
		if (enclosed)
		{
			if (first.getPrev() == null)
				throw new RuntimeException();
			if (last.getNext() == null)
				throw new RuntimeException();
			if (last.getNext() != first)
				throw new RuntimeException();
			if (first.getPrev() != last)
				throw new RuntimeException();
			for (Segment2D s = first.getNext(); s != first; s = s.getNext())
			{
				if (s == null)
					throw new RuntimeException();
				if (s.getPrev().getNext() != s)
					throw new RuntimeException();
			}
		} else
		{
			if (first.getPrev() != null)
				throw new RuntimeException();
			if (last.getNext() != null)
				throw new RuntimeException();
		}
	}

	public boolean empty()
	{
		return first == null;
	}

	public Iterator<Segment2D> iterator()
	{
		return new Segment2DIterator();
	}

	private class Segment2DIterator implements Iterator<Segment2D>
	{
		private Segment2D next;

		public Segment2DIterator()
		{
			this.next = first;
		}

		public boolean hasNext()
		{
			return next != null;
		}

		public Segment2D next()
		{
			Segment2D ret = next;
			next = next.getNext();
			if (next == first)
				next = null;
			return ret;
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
}
