package daid.sliceAndDaid.tool;

import java.util.Vector;

import daid.sliceAndDaid.Layer;
import daid.sliceAndDaid.Segment2D;
import daid.sliceAndDaid.util.Vector2;

/**
 * The path tool is the final tool run before the GCode is generated.
 * 
 * It takes all the separate lines and links those into a single large path, ready for GCode
 * generation.
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
			prev = layer.skirt.getLargestPolygon().closestTo(bestStartPoint);
			layer.pathStart = prev;
			prev = prev.prev;
		}
		
		for (int i = 0; i < layer.outlinePart.length; i++)
		{
			// Find the largest polygon. So we start with the biggest outline first.
			Segment2D next = layer.outlinePart[i].getLargestPolygon();
			
			Vector<Segment2D> polys = new Vector<Segment2D>(layer.outlinePart[i].polygons);
			polys.remove(next);
			polys.add(0, next);
			
			while (polys.size() > 0)
			{
				next = polys.get(0);
				if (prev != null)
				{
					for (int n = 0; n < polys.size(); n++)
					{
						if (polys.get(n).getAABBDist(prev) < next.getAABBDist(prev))
							next = polys.get(n);
					}
				}
				polys.remove(next);
				if (prev == null)
				{
					next = next.closestTo(bestStartPoint);
					layer.pathStart = next;
					prev = next.prev;
				} else
				{
					next = next.closestTo(prev.end);
					Segment2D newPrev = next.prev;
					new Segment2D(Segment2D.TYPE_MOVE, prev, next);
					prev = newPrev;
				}
			}
		}
	}
}
