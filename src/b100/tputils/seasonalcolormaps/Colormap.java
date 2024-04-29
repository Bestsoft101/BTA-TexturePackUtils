package b100.tputils.seasonalcolormaps;

import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class Colormap {
	
	public final int[] buffer = new int[0x10000];
	
	private int get(int x, int z) {
		return buffer[z << 8 | x];
	}
	
	public int getFromWorldPos(WorldSource world, int x, int z) {
		double temp = world.getBlockTemperature(x, z);
		double humidity = world.getBlockHumidity(x, z);
		
		humidity *= temp;
		
		int x1 = (int) ((1.0 - temp) * 255);
		int z1 = (int) ((1.0 - humidity) * 255);
		
		return get(x1, z1);
	}
	
	public Vec3d getFromWorldPosVec3(World world, int x, int z) {
		int color = getFromWorldPos(world, x, z);
		
		float r = ((color >> 16) & 0xFF) / 255.0f;
		float g = ((color >>  8) & 0xFF) / 255.0f;
		float b = ((color >>  0) & 0xFF) / 255.0f;
		
		return Vec3d.createVector(r, g, b);
	}
	
}