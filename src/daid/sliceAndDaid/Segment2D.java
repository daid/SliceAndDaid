package daid.sliceAndDaid;

import java.util.Iterator;

import daid.sliceAndDaid.util.AABBrect;
import daid.sliceAndDaid.util.Vector2;

/**
 * Segment2D represents a line in 2D space.
 * 
 * It also knows about it's previous and next line segment. This caused more code to creep into this
 * class, and now it is also used to manage segment loops, sortof. It's kinda messy, but works for
 * now.
 */
public class Segment2D extends AABBrect implements Iterable<Segment2D>
{
	public final static int TYPE_MODEL_SLICE = 0;
	public final static int TYPE_PERIMETER = 1;
	public final static int TYPE_MOVE = 2;
	public final static int TYPE_FILL = 3;
	public final static int TYPE_ERROR = 0xFFFF;
	
	public Vector2 start;
	public Vector2 end;
	public Vector2 normal;
	public Segment2D next, prev;
	
	public double lineWidth;
	public int type;
	
	public Segment2D(int type, Vector2 start, Vector2 end)
	{
		// Make the AABB 1mm larger then the actual segment, to account for inaccuracies and moving
		// around the segment ends a bit.
		super(start, end, 1.0);
		
		this.type = type;
		this.lineWidth = -1;
		update(start, end);
	}
	
	public Segment2D(int type, Segment2D prev, Segment2D next)
	{
		super(prev.end, next.start, 1.0);
		this.type = type;
		this.start = prev.end;
		this.end = next.start;
		
		if (prev.next != null)
			prev.next.prev = null;
		prev.next = this;
		if (next.prev != null)
			next.prev.next = null;
		next.prev = this;
		
		this.prev = prev;
		this.next = next;
		
		update(this.start, this.end);
	}
	
	/**
	 * For large updates we need to fix the normal, and the AABB. Only call this when the segment is
	 * not in a Tree2D
	 */
	public void update(Vector2 start, Vector2 end)
	{
		this.start = start;
		this.end = end;
		this.normal = end.sub(start).crossZ().normal();
		updateAABB(start, end, 1.0);
	}
	
	/**
	 * Get the closest segment in this segment loop
	 */
	public Segment2D closestTo(Vector2 p)
	{
		Segment2D best = this;
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
	
	public String toString()
	{
		return "Segment:" + start + " " + end;
	}
	
	public Iterator<Segment2D> iterator()
	{
		return new Segment2DIterator(this);
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
	
	public Vector2 getIntersectionPoint(Segment2D other)
	{
		double x12 = start.x - end.x;
		double x34 = other.start.x - other.end.x;
		double y12 = start.y - end.y;
		double y34 = other.start.y - other.end.y;
		
		// Calculate the intersection of the 2 segments.
		double c = x12 * y34 - y12 * x34;
		if (Math.abs(c) < 0.0001)
		{
			return end.add(other.start).div(2);
		} else
		{
			double a = start.x * end.y - start.y * end.x;
			double b = other.start.x * other.end.y - other.start.y * other.end.x;
			
			return new Vector2((a * x34 - b * x12) / c, (a * y34 - b * y12) / c);
		}
	}
}
