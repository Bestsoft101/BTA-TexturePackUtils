package b100.tputils.coloredtextures;

import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;

public interface TextureProvider {
	
	public int getTexture(Block block, Side side, int meta);
	
	public int getTexture(Block block, WorldSource worldSource, int x, int y, int z, Side side);
	
}
