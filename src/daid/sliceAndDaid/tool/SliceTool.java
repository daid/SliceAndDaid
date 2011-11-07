package daid.sliceAndDaid.tool;

import java.util.Vector;

import daid.sliceAndDaid.Layer;
import daid.sliceAndDaid.Model;
import daid.sliceAndDaid.Segment2D;
import daid.sliceAndDaid.config.CraftConfig;
import daid.sliceAndDaid.util.Triangle;
import daid.sliceAndDaid.util.Vector3;

/**
 * The slice tool slices the model into layers, it does so by going trough all model triangles and
 * slice those into 2D lines.
 */
public class SliceTool
{
	private Model model;
	
	public SliceTool(Model model)
	{
		this.model = model;
	}
	
	public Vector<Layer> sliceModel(int startLayer, int endLayer, double extraLayerOffset)
	{
		Vector<Layer> layers = new Vector<Layer>();
		
		double layerHeight = CraftConfig.layerHeight;
		Vector3 modelMin = model.getMin();
		Vector3 modelMax = model.getMax();
		double firstLayerHeight = ((double) CraftConfig.firstLayerHeightPercent) / 100.0 + extraLayerOffset;
		int layerCount = (int) (modelMax.z / layerHeight + firstLayerHeight);
		
		int firstLayer = startLayer;
		int lastLayer = endLayer;
		if (lastLayer > layerCount)
			lastLayer = layerCount;
		System.out.println("Slicing " + (lastLayer - firstLayer) + " layers");
		for (int i = firstLayer; i < lastLayer; i++)
		{
			layers.add(new Layer(i, modelMin.x, modelMin.y, modelMax.x, modelMax.y));
		}
		for (Triangle t : model.triangles)
		{
			double zMin = t.point[0].z;
			double zMax = t.point[0].z;
			if (t.point[1].z < zMin)
				zMin = t.point[1].z;
			if (t.point[2].z < zMin)
				zMin = t.point[2].z;
			if (t.point[1].z > zMax)
				zMax = t.point[1].z;
			if (t.point[2].z > zMax)
				zMax = t.point[2].z;
			for (int i = (int) (zMin / layerHeight + firstLayerHeight); i <= (int) (zMax / layerHeight + firstLayerHeight); i++)
			{
				if (i >= firstLayer && i < lastLayer)
				{
					double layerZ = (((double) i) + firstLayerHeight) * layerHeight;
					Segment2D s = t.project2D(layerZ);
					if (s != null)
						layers.get(i - firstLayer).addModelSegment(s);
				}
			}
		}
		System.out.println("Optimizing layers");
		for (int i = 0; i < layers.size(); i++)
		{
			layers.get(i).optimize();
		}
		return layers;
	}
}
