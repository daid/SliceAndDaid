package daid.sliceAndDaid.tool;

import java.util.Vector;

import daid.sliceAndDaid.Layer;
import daid.sliceAndDaid.Polygon;
import daid.sliceAndDaid.Segment2D;
import daid.sliceAndDaid.util.Vector2;

/**
 * The path tool is the final tool run before the GCode is generated.
 * 
 * It takes all the separate lines and links those into a single large path, ready for GCode generation.
 */
public class PathTool
{
	private Layer layer;

	public PathTool(Layer layer)
	{
		this.layer = layer;
	}

	public void generatePath(Vector2 bestStartPoint)
	{
		Segment2D prev = null;
		if (layer.skirt != null)
		{
			Polygon poly = layer.skirt.getLargestPolygon();
			if (poly != null)
			{
				prev = poly.closestTo(bestStartPoint);
				layer.pathStart = prev;
				prev = poly.cutPoly(prev);
			}
		}

		for (int i = 0; i < layer.outlinePart.length; i++)
		{
			// Find the largest polygon. So we start with the biggest outline first.
			Polygon nextPoly = layer.outlinePart[i].getLargestPolygon();
			if (nextPoly == null)
				return;

			Vector<Polygon> polys = layer.outlinePart[i].getPolygonListClone();
			polys.remove(nextPoly);
			polys.add(0, nextPoly);

			while (polys.size() > 0)
			{
				nextPoly = polys.get(0);
				if (prev != null)
				{
					for (int n = 0; n < polys.size(); n++)
					{
						if (polys.get(n).getAABB().getAABBDist(prev) < nextPoly.getAABB().getAABBDist(prev))
							nextPoly = polys.get(n);
					}
				}
				polys.remove(nextPoly);
				if (prev == null)
				{
					Segment2D startSegment = nextPoly.closestTo(bestStartPoint);
					layer.pathStart = startSegment;
					prev = nextPoly.cutPoly(startSegment);
				} else
				{
					Segment2D startSegment = nextPoly.closestTo(prev.end);
					Segment2D newPrev = nextPoly.cutPoly(startSegment);
					new Segment2D(Segment2D.TYPE_MOVE, prev, startSegment);
					prev = newPrev;
				}
			}
		}
	}
}
