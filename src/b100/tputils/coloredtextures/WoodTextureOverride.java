package b100.tputils.coloredtextures;

import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;

public class WoodTextureOverride implements TextureProvider {
	
	public boolean offset;
	
	public WoodTextureOverride(boolean offset) {
		this.offset = offset;
	}
	
	@Override
	public int getTexture(Block block, Side side, int meta) {
		if(ColoredTexturesMod.enableWoodTextures) {
			if(offset) {
				meta >>= 4;
			}
			return ColoredTexturesMod.getCustomPlankTexture(~(meta & 0xF));
		}
		return ColoredTexturesMod.texCoordToIndex(2, 3);
	}

	@Override
	public int getTexture(Block block, WorldSource worldSource, int x, int y, int z, Side side) {
		return getTexture(block, side, worldSource.getBlockMetadata(x, y, z));
	}

}
