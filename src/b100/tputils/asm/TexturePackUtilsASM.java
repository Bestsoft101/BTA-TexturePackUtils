package b100.tputils.asm;

import b100.tputils.TexturePackUtils;
import b100.tputils.betterfoliage.BetterFoliageMod;
import b100.tputils.seasonalcolormaps.SeasonalColormapsMod;
import b100.tputils.seasonaltextures.SeasonalTexturesMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;

public abstract class TexturePackUtilsASM {
	
	public static boolean debug = false;
	
	public static void onStartup(Minecraft minecraft) {
		TexturePackUtils.onStartup(minecraft);
		SeasonalColormapsMod.onStartup(minecraft);
		BetterFoliageMod.onStartup(minecraft);
		SeasonalTexturesMod.onStartup(minecraft);
	}
	
	public static void onRefreshTextures() {
		TexturePackUtils.onLoad();
		SeasonalColormapsMod.onLoad();
		BetterFoliageMod.onLoad();
		SeasonalTexturesMod.onLoad();
	}
	
	public static void beginRenderWorld() {
		SeasonalColormapsMod.update();
		SeasonalTexturesMod.update();
	}
	
	public static void onRenderBlock(RenderBlocks renderBlocks, Block block, int x, int y, int z, float r, float g, float b) {
		BetterFoliageMod.onRenderBlock(renderBlocks, block, x, y, z, r, g, b);
	}
	
	public static Vec3d getSkyColor(World world, ICamera camera, float partialTicks) {
		return SeasonalColormapsMod.getSkyColor(world, camera, partialTicks);
	}
	
	public static Vec3d getFogColor(World world, float partialTicks) {
		return SeasonalColormapsMod.getFogColor(world, partialTicks);
	}
	
	public static void log(String string) {
		System.out.print("[TPU ASM] " + string + "\n");
	}
	
	public static void debug(String string) {
		if(debug) {
			System.out.print("[TPU ASM] " + string + "\n");
		}
	}

}
