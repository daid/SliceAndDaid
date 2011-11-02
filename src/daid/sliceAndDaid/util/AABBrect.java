package daid.sliceAndDaid.util;

public class AABBrect
{
	private Vector2 lowerBound, upperBound;
	
	@SuppressWarnings("unchecked")
	public AABBTree.TreeNode node;
	
	public AABBrect(Vector2 p1, Vector2 p2)
	{
		lowerBound = p1;
		upperBound = p2;
	}
	
	public AABBrect(AABBrect aabb)
	{
		lowerBound = aabb.lowerBound;
		upperBound = aabb.upperBound;
	}
	
	public AABBrect(Vector2 p1, Vector2 p2, double extend)
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
	
	public void updateAABB(Vector2 p1, Vector2 p2, double extend)
	{
		if (node != null)
			throw new UnsupportedOperationException("Update on AABBrect while in a AABBTree");
		lowerBound = new Vector2(Math.min(p1.x, p2.x) - extend, Math.min(p1.y, p2.y) - extend);
		upperBound = new Vector2(Math.max(p1.x, p2.x) + extend, Math.max(p1.y, p2.y) + extend);
	}
	
	public AABBrect combine(AABBrect e)
	{
		return new AABBrect(new Vector2(Math.min(lowerBound.x, e.lowerBound.x), Math.min(lowerBound.y, e.lowerBound.y)), new Vector2(Math.max(upperBound.x, e.upperBound.x), Math.max(upperBound.y, e.upperBound.y)));
	}
	
	public void addAABB(AABBrect a)
	{
		if (node != null)
			throw new UnsupportedOperationException("addAABB on AABBrect while in a AABBTree");
		lowerBound = new Vector2(Math.min(lowerBound.x, a.lowerBound.x), Math.min(lowerBound.y, a.lowerBound.y));
		upperBound = new Vector2(Math.max(upperBound.x, a.upperBound.x), Math.max(upperBound.y, a.upperBound.y));
	}
	
	public boolean overlap(AABBrect t)
	{
		if (t.lowerBound.x - upperBound.x > 0.0f || t.lowerBound.y - upperBound.y > 0.0f)
			return false;
		
		if (lowerBound.x - t.upperBound.x > 0.0f || lowerBound.y - t.upperBound.y > 0.0f)
			return false;
		
		return true;
	}
	
	public double getAABBDist(AABBrect t)
	{
		return upperBound.add(lowerBound).div(2).sub(t.upperBound.add(t.lowerBound).div(2)).vSize();
	}
}
