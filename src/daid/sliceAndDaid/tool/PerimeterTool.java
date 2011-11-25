package daid.sliceAndDaid.tool;

import sun.reflect.generics.tree.Tree;
import daid.sliceAndDaid.LayerPart;
import daid.sliceAndDaid.Polygon;
import daid.sliceAndDaid.Segment2D;
import daid.sliceAndDaid.config.CraftConfig;
import daid.sliceAndDaid.util.AABBTree;
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
		AABBTree<Segment2D> tree = new AABBTree<Segment2D>();
		for (Polygon poly : layerPart)
		{
			Polygon newPoly = new Polygon();
			Segment2D first = null, prev = null;
			for (Segment2D s : poly)
			{
				Vector2 start = s.start.sub(s.getNormal().mul(distance));
				Vector2 end = s.end.sub(s.getNormal().mul(distance));
				Segment2D newSeg = new Segment2D(Segment2D.TYPE_PERIMETER, start, end);
				newSeg.lineWidth = CraftConfig.perimeterWidth;

				newPoly.addEnd(newSeg);
				if (prev == null)
				{
					first = newSeg;
				} else
				{
					linkUp(ret, prev, newSeg);
				}

				prev = newSeg;
			}

			if (!newPoly.empty())
			{
				newPoly.close();
				linkUp(ret, prev, first);
				for (Segment2D s : newPoly)
				{
					tree.insert(s);
				}
				ret.addPolygon(newPoly);
			}
		}

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
		} else
		{
			prev.end = p;
			next.start = p;

			prev.next = next;
			next.prev = prev;
		}
	}
}
