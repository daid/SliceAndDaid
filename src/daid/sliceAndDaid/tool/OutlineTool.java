package daid.sliceAndDaid.tool;

import daid.sliceAndDaid.Layer;
import daid.sliceAndDaid.Segment2D;
import daid.sliceAndDaid.config.CraftConfig;
import daid.sliceAndDaid.util.Vector2;

public class OutlineTool
{
	private Layer layer;
	private double distance;
	
	public OutlineTool(Layer layer, double distance)
	{
		this.layer = layer;
		this.distance = distance;
	}
	
	public void createOutline(int index)
	{
		for (Segment2D segStart : layer.modelPart.polygons)
		{
			Segment2D prev = null;
			Segment2D first = null;
			for (Segment2D s : segStart)
			{
				Vector2 start = s.start.sub(s.normal.mul(distance));
				Vector2 end = s.end.sub(s.normal.mul(distance));
				Segment2D newSeg = new Segment2D(Segment2D.TYPE_OUTLINE, start, end);
				newSeg.lineWidth = CraftConfig.outlineWidth;
				layer.segmentList.add(newSeg);
				
				if (prev == null)
				{
					first = newSeg;
				} else
				{
					linkUp(prev, newSeg);
				}
				
				prev = newSeg;
			}
			linkUp(prev, first);
			layer.outlinePart[index].polygons.add(first);
		}
	}
	
	/**
	 * Link up the 2 segments to each other, this will extend the segment so that the 2 segments
	 * cross, unless the extend it longer then the 'distance', at which point an extra segment is
	 * created. This will help with very high angle corners.
	 */
	private void linkUp(Segment2D prev, Segment2D next)
	{
		double x12 = prev.start.x - prev.end.x;
		double x34 = next.start.x - next.end.x;
		double y12 = prev.start.y - prev.end.y;
		double y34 = next.start.y - next.end.y;
		
		double c = x12 * y34 - y12 * x34;
		Vector2 p;
		if (Math.abs(c) < 0.0001)
		{
			p = prev.end.add(next.start).div(2);
		} else
		{
			double a = prev.start.x * prev.end.y - prev.start.y * prev.end.x;
			double b = next.start.x * next.end.y - next.start.y * next.end.x;
			
			p = new Vector2((a * x34 - b * x12) / c, (a * y34 - b * y12) / c);
		}
		
		if (prev.end.sub(p).vSize2() > distance * distance)
		{
			Vector2 p1 = prev.end.add(p.sub(prev.end).normal().mul(distance));
			Vector2 p2 = next.start.add(p.sub(next.start).normal().mul(distance));
			
			prev.end = p1;
			next.start = p2;
			Segment2D newSeg = new Segment2D(Segment2D.TYPE_OUTLINE, p1, p2);
			newSeg.lineWidth = CraftConfig.outlineWidth;
			prev.next = newSeg;
			newSeg.prev = prev;
			next.prev = newSeg;
			newSeg.next = next;
			layer.segmentList.add(newSeg);
		} else
		{
			prev.end = p;
			next.start = p;
			
			prev.next = next;
			next.prev = prev;
		}
	}
}
