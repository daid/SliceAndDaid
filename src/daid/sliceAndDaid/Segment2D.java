package daid.sliceAndDaid;

import daid.sliceAndDaid.util.AABBrect;
import daid.sliceAndDaid.util.Vector2;

/**
 * Segment2D represents a line in 2D space.
 */
public class Segment2D extends AABBrect
{
	public final static int TYPE_MODEL_SLICE = 0;
	public final static int TYPE_PERIMETER = 1;
	public final static int TYPE_MOVE = 2;
	public final static int TYPE_FILL = 3;
	public final static int TYPE_ERROR = 0xFFFF;

	public Vector2 start;
	public Vector2 end;
	private Vector2 normal;
	public Segment2D next, prev;

	public double lineWidth;
	public double feedRate;
	private int type;

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
	 * For large updates we need to fix the normal, and the AABB. Only call this when the segment is not in a Tree2D
	 */
	public void update(Vector2 start, Vector2 end)
	{
		this.start = start;
		this.end = end;
		this.normal = end.sub(start).crossZ().normal();
		updateAABB(start, end, 1.0);
	}

	public String toString()
	{
		return "Segment:" + start + " " + end;
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
			return null;
		} else
		{
			double a = start.x * end.y - start.y * end.x;
			double b = other.start.x * other.end.y - other.start.y * other.end.x;

			return new Vector2((a * x34 - b * x12) / c, (a * y34 - b * y12) / c);
		}
	}

	public Vector2 getCollisionPoint(Segment2D other)
	{
		Vector2 p = getIntersectionPoint(other);
		if (p == null)
			return null;
		if ((p.x >= start.x && p.x <= end.x) || (p.x >= end.x && p.x <= start.x))
		{
			if ((p.y >= start.y && p.y <= end.y) || (p.y >= end.y && p.y <= start.y))
				return p;
		}
		return null;
	}
	
	public Vector2 getNormal()
	{
		return normal;
	}
	
	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}
}
