package b100.tputils.coloredtextures;

import net.minecraft.client.render.block.color.BlockColor;
import net.minecraft.core.world.World;

public class LampColorOverride extends BlockColor {

	public BlockColor previousColor;
	
	public LampColorOverride(BlockColor previousColor) {
		this.previousColor = previousColor;
	}
	
	@Override
	public int getFallbackColor(int var1) {
		if(ColoredTexturesMod.enableLampTextures) {
			return 0xFFFFFFFF;
		}
		return previousColor.getFallbackColor(var1);
	}

	@Override
	public int getWorldColor(World world, int x, int y, int z) {
		if(ColoredTexturesMod.enableLampTextures) {
			return 0xFFFFFFFF;
		}
		return previousColor.getWorldColor(world, x, y, z);
	}

}
