package b100.tputils.coloredtextures;

import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;

public class LampTextureOverride implements TextureProvider {

	private boolean lit;
	
	public LampTextureOverride(boolean lit) {
		this.lit = lit;
	}
	
	@Override
	public int getTexture(Block block, Side side, int meta) {
		if(ColoredTexturesMod.enableLampTextures) {
			return getTexture(meta, true);
		}
		return ColoredTexturesMod.texCoordToIndex(5, 12);
	}

	@Override
	public int getTexture(Block block, WorldSource worldSource, int x, int y, int z, Side side) {
		if(ColoredTexturesMod.enableLampTextures) {
			return getTexture(worldSource.getBlockMetadata(x, y, z), lit);
		}
		return ColoredTexturesMod.texCoordToIndex(lit ? 5 : 4, 12);
	}
	
	public int getTexture(int meta, boolean lit) {
		int texX = 28;
		int texY = 16;
		
		if(lit) {
			texX += 2;
		}
		
		meta = 15 - meta;
		
		texX += meta / 8;
		texY += meta % 8;
		
		return ColoredTexturesMod.texCoordToIndex(texX, texY);
	}

}
