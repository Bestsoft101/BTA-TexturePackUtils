package b100.tputils.betterfoliage.asm;

import b100.tputils.betterfoliage.BetterFoliageMod;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.core.block.Block;

public class BetterFoliageASM {
	
	public static void onRenderBlock(RenderBlocks renderBlocks, Block block, int x, int y, int z, float r, float g, float b) {
		BetterFoliageMod.onRenderBlock(renderBlocks, block, x, y, z, r, g, b);
	}

}
