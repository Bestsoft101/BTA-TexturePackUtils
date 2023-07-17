package b100.tputils.coloredtextures;

import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;

public class ChestTextureOverride implements TextureProvider {

	@Override
	public int getTexture(Block block, Side side, int meta) {
		if(!ColoredTexturesMod.enableWoodTextures) {
			int baseTex = ColoredTexturesMod.texCoordToIndex(0, 17);
			if(side == Side.SOUTH) {
				baseTex += 2;
			}else if(side.getAxis() != Axis.Y) {
				baseTex += 1;
			}
			return baseTex;
		}
		int baseTex = Block.chestPlanksOak.getBlockTextureFromSideAndMetadata(Side.TOP, 0);
		if(side == Side.SOUTH) {
			baseTex += 2;
		}else if(side.getAxis() != Axis.Y) {
			baseTex += 1;
		}
		
		baseTex += ColoredTexturesMod.texCoordToIndex(7, 15);
		meta = 15 - ((meta >> 4) & 0xF);
		baseTex += ColoredTexturesMod.texCoordToIndex((meta >> 2) * 3, (meta & 3) * 3);
		
		return baseTex;
	}

	@Override
	public int getTexture(Block block, WorldSource worldSource, int x, int y, int z, Side side) {
		int baseTex = Block.chestPlanksOak.getBlockTexture(worldSource, x, y, z, side);
		if(!ColoredTexturesMod.enableWoodTextures) {
			return baseTex + ColoredTexturesMod.texCoordToIndex(-9, 16);
		}
		
		baseTex += ColoredTexturesMod.texCoordToIndex(7, 15);
		
		int meta = worldSource.getBlockMetadata(x, y, z);
		meta = 15 - ((meta >> 4) & 0xF);
		
		baseTex += ColoredTexturesMod.texCoordToIndex((meta >> 2) * 3, (meta & 3) * 3);
		
		return baseTex;
	}
	
}
