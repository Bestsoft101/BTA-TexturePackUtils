package b100.tputils.coloredtextures;

import net.minecraft.client.render.block.model.BlockModelRenderBlocks;
import net.minecraft.core.block.Block;

public class ChestModelOverride extends BlockModelRenderBlocks {

	public ChestModelOverride() {
		super(0);
	}
	
	@Override
	public boolean render(Block block, int x, int y, int z) {
		this.renderType = ColoredTexturesMod.enableWoodTextures ? 0 : 22;
		
		return super.render(block, x, y, z);
	}

}
