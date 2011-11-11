package daid.sliceAndDaid.tool;

import daid.sliceAndDaid.LayerPart;
import daid.sliceAndDaid.Polygon;
import daid.sliceAndDaid.Segment2D;
import daid.sliceAndDaid.config.CraftConfig;
import daid.sliceAndDaid.util.Vector2;

/**
 * The perimeter is the outer lines of the object, the "walls" so to say.
 */
public class PerimeterTool
{
	private LayerPart layerPart;
	private double distance;

	public PerimeterTool(LayerPart layerPart, double distance)
	{
		this.layerPart = layerPart;
		this.distance = distance;
	}

	public LayerPart createPerimeter()
	{
		LayerPart ret = new LayerPart(layerPart);
		for (Polygon poly : layerPart.polygons)
		{
			Segment2D prev = null;
			Segment2D first = null;
			for (Segment2D s : poly)
			{
				Vector2 start = s.start.sub(s.normal.mul(distance));
				Vector2 end = s.end.sub(s.normal.mul(distance));
				Segment2D newSeg = new Segment2D(Segment2D.TYPE_PERIMETER, start, end);
				newSeg.lineWidth = CraftConfig.perimeterWidth;
				ret.add(newSeg);

				if (prev == null)
				{
					first = newSeg;
				} else
				{
					linkUp(ret, prev, newSeg);
				}

				prev = newSeg;
			}

			if (first != null)
			{
				linkUp(ret, prev, first);

				Polygon newPoly = new Polygon(first);
				for (Segment2D s : newPoly)
				{
					if (s.end.sub(s.start).vSize2() < CraftConfig.minSegmentLength * CraftConfig.minSegmentLength)
					{
						ret.tree.remove(s);
						Segment2D next = s.next;
						ret.tree.remove(next);
						newPoly.remove(s);
						ret.tree.insert(next);
					}
				}
				newPoly.check();
				ret.polygons.add(newPoly);
			}
		}
		/*
		 * for (Polygon poly : ret.polygons) { for (Segment2D s : poly) { Vector2 p = s.prev.getCollisionPoint(s.next); if (p != null) { Segment2D prev =
		 * s.prev; Segment2D next = s.next; layerPart.tree.remove(s); layerPart.tree.remove(prev); layerPart.tree.remove(next); prev.update(prev.start, p);
		 * next.update(p, next.end); poly.remove(s); layerPart.tree.insert(prev); layerPart.tree.insert(next); } } }
		 */
		return ret;
	}

	/**
	 * Link up the 2 segments to each other, this will extend the segment so that the 2 segments cross, unless the extend it longer then the 'distance', at
	 * which point an extra segment is created. This will help with very high angle corners.
	 */
	private void linkUp(LayerPart ret, Segment2D prev, Segment2D next)
	{
		Vector2 p = prev.getIntersectionPoint(next);
		if (p == null)
			p = prev.end.add(next.start).div(2);
		// If the intersection point between the 2 moved lines is a bit further away then the line
		// distance, then we are a tight corner and we need to be capped.
		if (CraftConfig.perimeterCap && prev.end.sub(p).vSize2() > distance * 1.1 * distance * 1.1)
		{
			Vector2 p1 = prev.end.add(p.sub(prev.end).normal().mul(distance));
			Vector2 p2 = next.start.add(p.sub(next.start).normal().mul(distance));

			prev.end = p1;
			next.start = p2;
			Segment2D newSeg = new Segment2D(Segment2D.TYPE_PERIMETER, p1, p2);
			newSeg.lineWidth = CraftConfig.perimeterWidth;
			prev.next = newSeg;
			newSeg.prev = prev;
			next.prev = newSeg;
			newSeg.next = next;
			ret.add(newSeg);
		} else
		{
			prev.end = p;
			next.start = p;

			prev.next = next;
			next.prev = prev;
		}
	}
}
