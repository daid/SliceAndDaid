package daid.sliceAndDaid.tool;

import daid.sliceAndDaid.Layer;
import daid.sliceAndDaid.Segment2D;
import daid.sliceAndDaid.config.CraftConfig;

public class SpeedTool
{
	private Layer layer;
	
	public SpeedTool(Layer layer)
	{
		this.layer = layer;
	}
	
	public void updateSpeed()
	{
		double layerTime = 0;
		for (Segment2D s = layer.pathStart; s != null; s = s.next)
		{
			if (s.lineWidth < 0)
			{
				s.feedRate = CraftConfig.travelSpeed;
			} else
			{
				s.feedRate = CraftConfig.printSpeed;
			}
			layerTime += s.start.sub(s.end).vSize() / s.feedRate;
		}
		
		if (layerTime < CraftConfig.minLayerTime)
		{
			double multiply = layerTime / CraftConfig.minLayerTime;
			for (Segment2D s = layer.pathStart; s != null; s = s.next)
			{
				s.feedRate *= multiply;
			}
		}
	}
}
