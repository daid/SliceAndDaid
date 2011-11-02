package daid.sliceAndDaid.util;

public class TreeAABB
{
	private Vector2 lowerBound, upperBound;
	
	public TreeAABB(Vector2 p1, Vector2 p2)
	{
		lowerBound = p1;
		upperBound = p2;
	}
	
	public TreeAABB(Vector2 p1, Vector2 p2, double extend)
	{
		lowerBound = new Vector2(Math.min(p1.x, p2.x) - extend, Math.min(p1.y, p2.y) - extend);
		upperBound = new Vector2(Math.max(p1.x, p2.x) + extend, Math.max(p1.y, p2.y) + extend);
	}
	
	public double getPerimeter()
	{
		double w = upperBound.x - lowerBound.x;
		double h = upperBound.y - lowerBound.y;
		return (w + h) * 2.0;
	}
	
	public TreeAABB combine(TreeAABB e)
	{
		return new TreeAABB(new Vector2(Math.min(lowerBound.x, e.lowerBound.x), Math.min(lowerBound.y, e.lowerBound.y)), new Vector2(Math.max(upperBound.x, e.upperBound.x), Math.max(upperBound.y, e.upperBound.y)));
	}
}
