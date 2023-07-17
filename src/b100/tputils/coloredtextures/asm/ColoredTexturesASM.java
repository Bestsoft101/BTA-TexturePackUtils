package b100.tputils.coloredtextures.asm;

import java.util.HashMap;
import java.util.Map;

import b100.tputils.coloredtextures.ChestTextureOverride;
import b100.tputils.coloredtextures.LampTextureOverride;
import b100.tputils.coloredtextures.TextureProvider;
import b100.tputils.coloredtextures.WoodTextureOverride;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;

public class ColoredTexturesASM {
	
	public static Map<Block, TextureProvider> textureProviders = new HashMap<>();
	
	static {
		textureProviders.put(Block.planksOakPainted, new WoodTextureOverride(false));

		textureProviders.put(Block.slabPlanksOakPainted, new WoodTextureOverride(true));
		textureProviders.put(Block.stairsPlanksOakPainted, new WoodTextureOverride(true));
		
		textureProviders.put(Block.fencePlanksOakPainted, new WoodTextureOverride(false));
		textureProviders.put(Block.fencegatePlanksOakPainted, new WoodTextureOverride(true));

		textureProviders.put(Block.chestPlanksOakPainted, new ChestTextureOverride());
		textureProviders.put(Block.lampIdle, new LampTextureOverride(false));
		textureProviders.put(Block.lampActive, new LampTextureOverride(true));
	}
	
	public static int getTexture(Block block, Side side, int meta) {
		TextureProvider textureProvider = textureProviders.get(block);
		if(textureProvider == null) {
			return 0;
		}
		return textureProvider.getTexture(block, side, meta);
	}
	
	public static int getTexture(Block block, WorldSource world, int x, int y, int z, Side side) {
		TextureProvider textureProvider = textureProviders.get(block);
		if(textureProvider == null) {
			return 0;
		}
		return textureProvider.getTexture(block, world, x, y, z, side);
	}

}
