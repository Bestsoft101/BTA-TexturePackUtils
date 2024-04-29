package b100.tputils.customatlas;

import java.util.Comparator;

public class AtlasTile {
	
	public static final Comparator<AtlasTile> sorter = (o1, o2) -> o1.x == o2.x ? o1.y - o2.y : o1.x - o2.x;
	
	public final String name;
	public final int x;
	public final int y;
	
	public AtlasTile(String name, int x, int y) {
		if(x < 0 || x > 0xFFFF) {
			throw new RuntimeException("x out of bounds: " + x);
		}
		if(y < 0 || y > 0xFFFF) {
			throw new RuntimeException("y out of bounds: " + y);
		}
		
		this.name = name;
		this.x = x;
		this.y = y;
	}
	
	public static int getIndex(int x, int y) {
		return (x & 0xFFFF) << 16 | (y & 0xFFFF);
	}
	
}
