package daid.sliceAndDaid;

import java.util.Iterator;

import daid.sliceAndDaid.util.Vector2;

public class Segment2D implements Iterable<Segment2D>
{
	public final static int TYPE_MODEL_SLICE = 0;
	public final static int TYPE_OUTLINE = 1;
	public final static int TYPE_PERIMITER = 2;
	public final static int TYPE_FILL = 3;
	public final static int TYPE_ERROR = 0xFFFF;
	
	public Vector2 start;
	public Vector2 end;
	public Vector2 normal;
	public Segment2D next, prev;
	
	public int type;
	
	public Segment2D(int type, Vector2 start, Vector2 end)
	{
		this.type = type;
		this.start = start;
		this.end = end;
		updateNormal();
	}
	
	public String toString()
	{
		return "Segment:" + start + " " + end;
	}

	public void updateNormal()
	{
		this.normal = end.sub(start).crossZ().normal();
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
		}
	}
}
