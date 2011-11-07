package daid.sliceAndDaid.config;

/**
 * The CraftConfig class contains the configurable
 * settings for the slicer. Reflection and annotations
 * are used to make it easy to generate the configuration
 * dialog.
 * NOTE: Do not auto format this file. Manual format keeps it readable!
 */
public class CraftConfig
{
	public static final String VERSION = "Dev-Prerelease";

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
	public static int outlineCount = 3;

	@Setting(level = Setting.LEVEL_STARTER,
			title = "Print speed (mm/s)",
			description = "Speed at which the head is moved while it's printing",
			minValue = 0, maxValue = 10000)
	public static double printSpeed = 40.0;

	@Setting(level = Setting.LEVEL_NORMAL,
			title = "Travel speed (mm/s)",
			description = "Speed at which the head is moved while it's not printing",
			minValue = 0, maxValue = 10000)
	public static double travelSpeed = 150.0;

	@Setting(level = Setting.LEVEL_STARTER,
			title = "Filament diameter (mm)",
			description = "The diameter of the filament, as accurate as possible. Else, if you get to little extrusion reduce this number, if you get to much, increase this number.",
			minValue = 0, maxValue = 10)
	public static double filamentDiameter = 2.89;

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
	
	
	
	@Setting(level = Setting.LEVEL_HIDDEN)
	public static int showLevel = Integer.MAX_VALUE;
	@Setting(level = Setting.LEVEL_HIDDEN)
	public static String lastSlicedFile = "";
}
