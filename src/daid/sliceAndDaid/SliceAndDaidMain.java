package daid.sliceAndDaid;

import java.io.IOException;
import java.util.Vector;

import javax.swing.SwingUtilities;

import daid.sliceAndDaid.config.CraftConfig;
import daid.sliceAndDaid.config.CraftConfigLoader;
import daid.sliceAndDaid.tool.OutlineTool;
import daid.sliceAndDaid.tool.PathTool;
import daid.sliceAndDaid.tool.SliceTool;
import daid.sliceAndDaid.ui.ConfigWindow;
import daid.sliceAndDaid.ui.PreviewFrame;

public class SliceAndDaidMain
{
	public static void main(String[] args)
	{
		CraftConfigLoader.loadConfig(null);
		
		if (args.length < 1)
		{
			
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					new ConfigWindow();
				}
			});
		} else
		{
			for (int i = 0; i < args.length; i++)
				sliceModel(args[i]);
		}
	}
	
	public static void sliceModel(String filename)
	{
		CraftConfig.lastSlicedFile = filename;
		CraftConfigLoader.saveConfig(null);
		
		Model m;
		try
		{
			m = new Model(filename);
		} catch (IOException e)
		{
			System.err.println("Failed to load model");
			return;
		}
		m.center();
		SliceTool slicer = new SliceTool(m);
		final Vector<Layer> layers = slicer.sliceModel(CraftConfig.startLayerNr, CraftConfig.endLayerNr, 0.0);
		System.out.println("Creating outlines");
		for (int i = 0; i < layers.size(); i++)
		{
			for (int c = 0; c < CraftConfig.outlineCount; c++)
			{
				new OutlineTool(layers.get(i), CraftConfig.outlineWidth * (0.5 + (double) c)).createOutline(c);
			}
		}
		for (int i = 0; i < layers.size(); i++)
		{
			new PathTool(layers.get(i)).generatePath();
		}
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new PreviewFrame(layers);
			}
		});
	}
}
