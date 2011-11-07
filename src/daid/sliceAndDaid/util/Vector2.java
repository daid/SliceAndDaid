package daid.sliceAndDaid.util;

public class Vector2
{
	public final double x, y;
	
	public Vector2(double x, double y)
	{
		this.x = x;
		this.y = y;
		if (Double.isNaN(x) || Double.isNaN(y))
			throw new RuntimeException("Vector has NaN component...");
	}
	
	public Vector2 add(Vector2 v)
	{
		return new Vector2(x + v.x, y + v.y);
	}
	
	public Vector2 sub(Vector2 v)
	{
		return new Vector2(x - v.x, y - v.y);
	}
	
	public Vector2 div(double f)
	{
		return new Vector2(x / f, y / f);
	}
	
	public Vector2 mul(double f)
	{
		return new Vector2(x * f, y * f);
	}
	
	public Vector2 crossZ()
	{
		return new Vector2(y, -x);
	}
	
	public double dot(Vector2 v)
	{
		return x * v.x + y * v.y;
	}
	
	public boolean asGoodAsEqual(Vector2 v)
	{
		return (Math.abs(x - v.x) + Math.abs(y - v.y)) < 0.00001;
	}
	
	public String toString()
	{
		return x + "," + y;
	}
	
	/**
	 * Returns a normalized vector with a length of 1, having the same direction as the origonal vector.
	 */
	public Vector2 normal()
	{
		double d = vSize();
		if (d < 0.0000001)
			return new Vector2(0, 0);
		return new Vector2(x / d, y / d);
	}
	
	/**
	 * Returns the length of the vector.
	 */
	public double vSize()
	{
		return Math.sqrt(x * x + y * y);
	}
	
	/**
	 * Returns the squared length of the vector (faster then vSize())
	 */
	public double vSize2()
	{
		return x * x + y * y;
	}
}
