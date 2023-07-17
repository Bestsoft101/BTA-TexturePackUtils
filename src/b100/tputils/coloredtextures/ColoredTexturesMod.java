package b100.tputils.coloredtextures;

import b100.tputils.ConfigHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;

public abstract class ColoredTexturesMod {
	
	public static Minecraft mc;
	
	public static boolean enableWoodTextures = false;
	public static boolean enableLampTextures = false;
	
	private static final String configFileName = "/coloredtextures.properties";
	
	private static boolean registeredDispatchs = false;
	
	public static void onStartup(Minecraft minecraft) {
		log("Startup!");
		
		mc = minecraft;
	}
	
	public static void onLoad() {
		log("Loading config");
		
		enableLampTextures = false;
		enableWoodTextures = false;
		
		ConfigHelper.readConfigFromCurrentTexturePack(mc, configFileName, (key, value) -> parseConfig(key, value));
		
		log("Wood textures: " + enableWoodTextures);
		log("Lamp textures: " + enableLampTextures);
		
		if(!registeredDispatchs) {
			registeredDispatchs = true;
			
			BlockColorDispatcher blockColorDispatcher = BlockColorDispatcher.getInstance();
			BlockModelDispatcher blockModelDispatcher = BlockModelDispatcher.getInstance();
			
			blockColorDispatcher.addDispatch(Block.planksOakPainted, new WoodColorOverride(blockColorDispatcher.getDispatch(Block.planksOakPainted)));
			blockColorDispatcher.addDispatch(Block.slabPlanksOakPainted, new WoodColorOverride(blockColorDispatcher.getDispatch(Block.slabPlanksOakPainted)));
			blockColorDispatcher.addDispatch(Block.stairsPlanksOakPainted, new WoodColorOverride(blockColorDispatcher.getDispatch(Block.stairsPlanksOakPainted)));
			blockColorDispatcher.addDispatch(Block.fencePlanksOakPainted, new WoodColorOverride(blockColorDispatcher.getDispatch(Block.fencePlanksOakPainted)));
			blockColorDispatcher.addDispatch(Block.fencegatePlanksOakPainted, new WoodColorOverride(blockColorDispatcher.getDispatch(Block.fencegatePlanksOakPainted)));
			blockColorDispatcher.addDispatch(Block.chestPlanksOakPainted, new WoodColorOverride(blockColorDispatcher.getDispatch(Block.chestPlanksOakPainted)));
			blockColorDispatcher.addDispatch(Block.lampIdle, new LampColorOverride(blockColorDispatcher.getDispatch(Block.lampIdle)));
			blockColorDispatcher.addDispatch(Block.lampActive, new LampColorOverride(blockColorDispatcher.getDispatch(Block.lampActive)));
			
			blockModelDispatcher.addDispatch(Block.chestPlanksOakPainted, new ChestModelOverride());
		}
		
		if(mc.renderGlobal != null) {
			mc.renderGlobal.loadRenderers();
		}
	}
	
	private static void parseConfig(String key, String value) {
		if(key.equals("enablePlanks")) {
			enableWoodTextures = Boolean.parseBoolean(value); 
		}else if(key.equals("enableLamps")) {
			enableLampTextures = Boolean.parseBoolean(value); 
		}else {
			log("Unknown Option: " + key);
		}
	}

	public static int texCoordToIndex(int x, int y) {
		return x + y * Global.TEXTURE_ATLAS_WIDTH_TILES;
	}
	
	public static int getCustomPlankTexture(int meta) {
		int x = 18;
		int y = 17;
		meta = meta & 0xF;
		x += (meta / 4) * 3;
		y += (meta % 4) * 3;
		return ColoredTexturesMod.texCoordToIndex(x, y);
	}
	
	public static void log(String str) {
		System.out.print("[ColoredTexturesMod] " + str + "\n");
	}
	
}
