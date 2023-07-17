package b100.tputils.asm;

import b100.tputils.TexturePackUtils;
import b100.tputils.betterfoliage.BetterFoliageMod;
import b100.tputils.coloredtextures.ColoredTexturesMod;
import b100.tputils.seasonalcolormaps.SeasonalColormapsMod;
import b100.tputils.seasonaltextures.SeasonalTexturesMod;
import net.minecraft.client.Minecraft;

public abstract class TexturePackUtilsASM {
	
	public static boolean debug = false;
	
	public static void onStartup(Minecraft minecraft) {
		TexturePackUtils.onStartup(minecraft);
		SeasonalColormapsMod.onStartup(minecraft);
		ColoredTexturesMod.onStartup(minecraft);
		BetterFoliageMod.onStartup(minecraft);
		SeasonalTexturesMod.onStartup(minecraft);
	}
	
	public static void onRefreshTextures() {
		TexturePackUtils.onLoad();
		SeasonalColormapsMod.onLoad();
		ColoredTexturesMod.onLoad();
		BetterFoliageMod.onLoad();
		SeasonalTexturesMod.onLoad();
	}
	
	public static void beginRenderWorld() {
		SeasonalColormapsMod.update();
		SeasonalTexturesMod.update();
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
