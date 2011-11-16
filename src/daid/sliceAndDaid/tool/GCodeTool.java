package daid.sliceAndDaid.tool;

import java.io.IOException;

import daid.sliceAndDaid.Layer;
import daid.sliceAndDaid.Segment2D;
import daid.sliceAndDaid.config.CraftConfig;
import daid.sliceAndDaid.util.GCodeFile;

public class GCodeTool
{
	private Layer layer;
	private GCodeFile file;
	
	public GCodeTool(Layer layer, GCodeFile file)
	{
		this.layer = layer;
		this.file = file;
	}
	
	public void generateGCode() throws IOException
	{
		double filamentMM3PerMM = Math.PI * (CraftConfig.filamentDiameter / 2) * (CraftConfig.filamentDiameter / 2);
		
		file.writeComment("LAYER:" + layer.layerNr);
		file.writeMoveZ((double) (layer.layerNr + 1) * CraftConfig.layerHeight, CraftConfig.travelSpeed, "Move to layer: " + layer.layerNr);
		if (layer.pathStart == null)
			return;
		file.writeMoveXY(layer.pathStart.start.x, layer.pathStart.start.y, CraftConfig.travelSpeed, "");
		for (Segment2D s = layer.pathStart; s != null; s = s.next)
		{
			if (s.lineWidth < 0)
			{
				file.writeMoveXY(s.end.x, s.end.y, s.feedRate, "");
			} else
			{
				// First calculate the amount of filament we need in mm3
				double filamentAmount = s.end.sub(s.start).vSize() * s.lineWidth * CraftConfig.layerHeight;
				// Then divide this by the amount of mm3 we have per mm filament, so we get the
				// amount of mm of filament we need to extrude.
				filamentAmount = filamentAmount / filamentMM3PerMM;
				file.writeMoveXYE(s.end.x, s.end.y, filamentAmount, s.feedRate, "");
			}
		}
	}
}
