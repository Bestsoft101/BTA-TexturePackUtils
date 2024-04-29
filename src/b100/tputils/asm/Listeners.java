package b100.tputils.asm;

import java.awt.image.BufferedImage;

import b100.tputils.TexturePackUtils;
import b100.tputils.betterfoliage.BetterFoliageMod;
import b100.tputils.customatlas.CustomAtlasMod;
import b100.tputils.seasonalcolormaps.SeasonalColormapsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.client.render.texturepack.TexturePackList;
import net.minecraft.client.util.helper.Textures;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;

public abstract class Listeners {
	
	public static boolean debug = false;
	
	public static void onStartup(Minecraft minecraft) {
		TexturePackUtils.onStartup(minecraft);
		SeasonalColormapsMod.onStartup(minecraft);
		BetterFoliageMod.onStartup(minecraft);
	}
	
	public static void beforeRefreshTextures() {
		CustomAtlasMod.beforeRefreshTextures();
		BetterFoliageMod.onLoad();
	}
	
	public static void onRefreshTextures() {
		TexturePackUtils.onLoad();
		SeasonalColormapsMod.onLoad();
	}
	
	public static void beginRenderWorld() {
		SeasonalColormapsMod.update();
	}
	
	public static void renderBlock(int x, int y, int z) {
		BetterFoliageMod.onRenderBlock(x, y, z);
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
	
	public static BufferedImage getTextureOverride(TexturePackList texturePackList, String path) {
		BufferedImage override = CustomAtlasMod.getTextureOverride(texturePackList, path);
		if(override != null) {
			return override;
		}
		return Textures.readImage(texturePackList.getResourceAsStream(path));
	}

}
