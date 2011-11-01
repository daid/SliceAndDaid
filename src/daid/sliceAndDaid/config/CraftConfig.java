package daid.sliceAndDaid.config;

public class CraftConfig
{
	public static final String VERSION = "Dev-Prerelease";
	
	/**************************
	 * Configuration settings *
	 **************************/

	@Setting(level = Setting.LEVEL_STARTER,
			title = "Layer height (mm)",
			description = "Height of each sliced layer",
			minValue = 0.0, maxValue = 1.0)
	public static double layerHeight = 0.2;

	@Setting(level = Setting.LEVEL_STARTER,
			title = "Width of the outline",
			description = "The width of the outline, a good value is the inner radius of your nozzle tip.",
			minValue = 0, maxValue = Integer.MAX_VALUE)
	public static double outlineWidth = 0.4;

	@Setting(level = Setting.LEVEL_NORMAL,
			title = "Outline count",
			description = "Amount of outline walls",
			minValue = 1, maxValue = Integer.MAX_VALUE)
	public static double outlineCount = 3;

	@Setting(level = Setting.LEVEL_ADVANCED,
			title = "First layer slice height (%)",
			description = "Starting height of the first slice in the model. 50% is the default.",
			minValue = 0, maxValue = 200)
	public static int firstLayerHeightPercent = 50;

	@Setting(level = Setting.LEVEL_ADVANCED,
			title = "Minimal line segment cosinus value",
			description = "If the cosinus of the line angle difference is higher then this value then 2 lines are joined into 1.\nSpeeding up the slicing, and creating less gcode commands. Lower values makes circles less round,\nfor a faster slicing and less GCode. A value of 1.0 leaves every line intact.",
			minValue = 0.95, maxValue = 1.0)
	public static double joinMinCosAngle = 0.995;

	@Setting(level = Setting.LEVEL_ADVANCED,
			title = "Start layer number",
			description = "First layer that is sliced, can be used to remove the bottom X layers",
			minValue = 0, maxValue = Integer.MAX_VALUE)
	public static int startLayerNr = 0;
	
	@Setting(level = Setting.LEVEL_ADVANCED,
			title = "Final layer number",
			description = "Last layer that is sliced, can be used to remove the top X layers.",
			minValue = 0, maxValue = Integer.MAX_VALUE)
	public static int endLayerNr = Integer.MAX_VALUE;
}