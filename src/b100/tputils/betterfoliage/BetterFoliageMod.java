package b100.tputils.betterfoliage;

import b100.tputils.ConfigHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.Tessellator;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;

public abstract class BetterFoliageMod {
	
	private static Minecraft mc;
	
	private static final String configFileName = "/betterfoliage.properties";
	
	public static boolean enableBetterGrass = false;
	private static int grassTexCount;
	private static int grassTexPos;
	private static int grassTexWidth;
	private static int grassTexHeight;
	private static double grassWidth;
	private static double grassHeight;
	private static double grassRotation;
	
	public static boolean enableBetterScorchedGrass = false;
	private static int scorchedGrassTexCount;
	private static int scorchedGrassTexPos;
	
	public static boolean enableBetterRetroGrass = false;
	private static int retroGrassTexCount;
	private static int retroGrassTexPos;
	
	public static boolean enableBetterLeaves = false;
	private static int leavesTexSize;
	private static int leavesTexPos;
	private static int oakTex;
	private static int birchTex;
	private static int pineTex;
	private static int cherryTex;
	private static int eucalyptusTex;
	private static int retroTex;
	private static int shrubTex;
	private static double leavesSize;
	private static double leavesRotation;
	
	public static void onStartup(Minecraft minecraft) {
		log("Startup!");
		
		mc = minecraft;
	}
	
	public static void onLoad() {
		enableBetterGrass = false;
		grassTexCount = 0;
		grassTexPos = 0;
		grassTexWidth = 16;
		grassTexHeight = 16;
		grassWidth = 1.0;
		grassHeight = 1.0;
		grassRotation = 0.0;

		enableBetterScorchedGrass = false;
		scorchedGrassTexCount = 0;
		scorchedGrassTexPos = 0;
		
		enableBetterRetroGrass = false;
		retroGrassTexCount = 0;
		retroGrassTexPos = 0;
		
		enableBetterLeaves = false;
		leavesTexPos = 0;
		leavesTexSize = 16;
		oakTex = 0;
		birchTex = 0;
		pineTex = 0;
		cherryTex = 0;
		eucalyptusTex = 0;
		retroTex = 0;
		shrubTex = 0;
		leavesSize = 1.0;
		leavesRotation = 0.0;
		
		ConfigHelper.readConfigFromCurrentTexturePack(mc, configFileName, (key, value) -> parseConfig(key, value));

		if(enableBetterGrass) {
			if(grassTexCount == 0) {
				log("Grass texture count is 0, disabling better grass");
				enableBetterGrass = false;
			}
		}
		if(enableBetterRetroGrass) {
			if(retroGrassTexCount == 0) {
				log("Retro grass texture count is 0, disabling better grass");
				enableBetterRetroGrass = false;
			}
		}
		if(enableBetterScorchedGrass) {
			if(scorchedGrassTexCount == 0) {
				log("Scorched grass texture count is 0, disabling better grass");
				enableBetterScorchedGrass = false;
			}
		}

		log("Enable Better Grass: " + enableBetterGrass);
		log("Enable Better Retro Grass: " + enableBetterRetroGrass);
		log("Enable Better Scorched Grass: " + enableBetterScorchedGrass);
		log("Enable Better Leaves: " + enableBetterLeaves);
	}
	
	private static void parseConfig(String key, String value) {
		if(key.equals("enableBetterGrass")) {
			enableBetterGrass = Boolean.parseBoolean(value);
		}else if(key.equals("grassTexCount")) {
			grassTexCount = Integer.parseInt(value);
		}else if(key.equals("grassTexPos")) {
			grassTexPos = parseTexturePosition(value); 
		}else if(key.equals("grassTexWidth")) {
			grassTexWidth = Integer.parseInt(value); 
		}else if(key.equals("grassTexHeight")) {
			grassTexHeight = Integer.parseInt(value); 
		}else if(key.equals("grassWidth")) {
			grassWidth = Double.parseDouble(value); 
		}else if(key.equals("grassHeight")) {
			grassHeight = Double.parseDouble(value); 
		}else if(key.equals("grassRotation")) {
			grassRotation = Double.parseDouble(value); 
		}else if(key.equals("enableBetterScorchedGrass")) {
			enableBetterScorchedGrass = Boolean.parseBoolean(value); 
		}else if(key.equals("scorchedGrassTexCount")) {
			scorchedGrassTexCount = Integer.parseInt(value); 
		}else if(key.equals("scorchedGrassTexPos")) {
			scorchedGrassTexPos = parseTexturePosition(value); 
		}else if(key.equals("enableBetterRetroGrass")) {
			enableBetterRetroGrass = Boolean.parseBoolean(value); 
		}else if(key.equals("retroGrassTexCount")) {
			retroGrassTexCount = Integer.parseInt(value); 
		}else if(key.equals("retroGrassTexPos")) {
			retroGrassTexPos = parseTexturePosition(value); 
		}else if(key.equals("enableBetterLeaves")) {
			enableBetterLeaves = Boolean.parseBoolean(value);
		}else if(key.equals("leavesTexSize")) {
			leavesTexSize = Integer.parseInt(value);
		}else if(key.equals("leavesTexPos")) {
			leavesTexPos = parseTexturePosition(value);
		}else if(key.equals("oakTex")) {
			oakTex = Integer.parseInt(value);
		}else if(key.equals("birchTex")) {
			birchTex = Integer.parseInt(value);
		}else if(key.equals("pineTex")) {
			pineTex = Integer.parseInt(value);
		}else if(key.equals("cherryTex")) {
			cherryTex = Integer.parseInt(value);
		}else if(key.equals("eucalyptusTex")) {
			eucalyptusTex = Integer.parseInt(value);
		}else if(key.equals("retroTex")) {
			retroTex = Integer.parseInt(value);
		}else if(key.equals("shrubTex")) {
			shrubTex = Integer.parseInt(value);
		}else if(key.equals("leavesSize")) {
			leavesSize = Double.parseDouble(value);
		}else if(key.equals("leavesRotation")) {
			leavesRotation = Double.parseDouble(value);
		}else {
			log("Unknown Option: " + key);
		}
	}
	
	private static int parseTexturePosition(String value) {
		int i = value.indexOf(',');
		int x = Integer.parseInt(value.substring(0, i));
		int y = Integer.parseInt(value.substring(i + 1));
		return Block.texCoordToIndex(x, y);
	}
	
	public static boolean onRenderBlock(RenderBlocks renderBlocks, Block block, int x, int y, int z, float r, float g, float b) {
		if(enableBetterGrass) {
			if(block == Block.grass) {
				if(mc.theWorld.getBlockId(x, y + 1, z) != 0) {
					return false;
				}
				return renderBetterGrass(renderBlocks, block, x, y, z, r, g, b, grassTexPos, grassTexCount);
			}
		}
		if(enableBetterRetroGrass) {
			if(block == Block.grassRetro) {
				if(mc.theWorld.getBlockId(x, y + 1, z) != 0) {
					return false;
				}
				return renderBetterGrass(renderBlocks, block, x, y, z, r, g, b, retroGrassTexPos, retroGrassTexCount);
			}
		}
		if(enableBetterScorchedGrass) {
			if(block == Block.grassScorched) {
				if(mc.theWorld.getBlockId(x, y + 1, z) != 0) {
					return false;
				}
				return renderBetterGrass(renderBlocks, block, x, y, z, r, g, b, scorchedGrassTexPos, scorchedGrassTexCount);
			}
		}
		if(enableBetterLeaves) {
			if(block == Block.leavesOak) return renderBetterLeaves(renderBlocks, block, x, y, z, r, g, b, oakTex);
			if(block == Block.leavesBirch) return renderBetterLeaves(renderBlocks, block, x, y, z, r, g, b, birchTex);
			if(block == Block.leavesPine) return renderBetterLeaves(renderBlocks, block, x, y, z, r, g, b, pineTex);
			if(block == Block.leavesEucalyptus) return renderBetterLeaves(renderBlocks, block, x, y, z, r, g, b, eucalyptusTex);
			if(block == Block.leavesCherry || block == Block.leavesCherryFlowering) return renderBetterLeaves(renderBlocks, block, x, y, z, r, g, b, cherryTex);
			if(block == Block.leavesOakRetro) return renderBetterLeaves(renderBlocks, block, x, y, z, r, g, b, retroTex);
			if(block == Block.leavesShrub) return renderBetterLeaves(renderBlocks, block, x, y, z, r, g, b, shrubTex);
		}
		return false;
	}
	
	public static boolean renderBetterGrass(RenderBlocks renderBlocks, Block block, int x, int y, int z, float r, float g, float b, int texPos, int texCount) {
		Tessellator t = Tessellator.instance;
		
		float brightness = block.getBlockBrightness(mc.theWorld, x, y + 1, z);
		
		t.setColorOpaque_F(r * brightness, g * brightness, b * brightness);
		
        long rand = (long)(x * 0x2fc20f) ^ (long)z * 0x6ebfff5L ^ (long)y;
        rand = rand * rand * 0x285b825L + rand * 11L;
        int tex = (int) (((rand & 0xFFFF) / (float)0xFFFF) * texCount);
        
        int atlasWidth = Global.TEXTURE_ATLAS_WIDTH_TILES;
        float atlasWidthReal = atlasWidth * 16;
        
        int texX = texPos % atlasWidth;
        int texY = texPos / atlasWidth;
        
        float texWidth = grassTexWidth / atlasWidthReal;
        float texHeight = grassTexHeight / atlasWidthReal;
        
        float u0 = texX / (float) atlasWidth;
        float v0 = texY / (float) atlasWidth + tex * texHeight;
        
        float u1 = u0 + texWidth;
        float v1 = v0 + texHeight;

		double width = grassWidth / 2.0;
		double height = grassHeight;

		double xOff = ((double) ((float) (rand >> 16 & 15L) / 15F) - 0.5D) * 0.25D;
		double zOff = ((double) ((float) (rand >> 24 & 15L) / 15F) - 0.5D) * 0.25D;

		double x0 = x + 0.5 - width + xOff;
		double x1 = x + 0.5 + width + xOff;

        double z0 = z + 0.5 - width + zOff;
        double z1 = z + 0.5 + width + zOff;
        
        double y0 = y + 1;
        double y1 = y + 1 + height;
        
        double rot = grassRotation;
        
        t.addVertexWithUV(x0+rot, y0, z0, u0, v1);
        t.addVertexWithUV(x0+rot, y1, z0, u0, v0);
        t.addVertexWithUV(x1-rot, y1, z1, u1, v0);
        t.addVertexWithUV(x1-rot, y0, z1, u1, v1);
        
        t.addVertexWithUV(x0+rot, y0, z0, u1, v1);
        t.addVertexWithUV(x1-rot, y0, z1, u0, v1);
        t.addVertexWithUV(x1-rot, y1, z1, u0, v0);
        t.addVertexWithUV(x0+rot, y1, z0, u1, v0);
        
        t.addVertexWithUV(x1, y0, z0+rot, u0, v1);
        t.addVertexWithUV(x1, y1, z0+rot, u0, v0);
        t.addVertexWithUV(x0, y1, z1-rot, u1, v0);
        t.addVertexWithUV(x0, y0, z1-rot, u1, v1);
        
        t.addVertexWithUV(x1, y0, z0+rot, u1, v1);
        t.addVertexWithUV(x0, y0, z1-rot, u0, v1);
        t.addVertexWithUV(x0, y1, z1-rot, u0, v0);
        t.addVertexWithUV(x1, y1, z0+rot, u1, v0);
        return true;
	}
	
	public static boolean renderBetterLeaves(RenderBlocks renderBlocks, Block block, int x, int y, int z, float r, float g, float b, int tex) {
		if(tex == -1) {
			return false;
		}
		
		Tessellator t = Tessellator.instance;
		
		float brightness = mc.theWorld.getBrightness(x, y, z, 0);
		
		t.setColorOpaque_F(r * brightness, g * brightness, b * brightness);

        long rand = (long)(x * 0x2fc20f) ^ (long)z * 0x6ebfff5L ^ (long)y;
        rand = rand * rand * 0x285b825L + rand * 11L;
        
        int atlasWidth = Global.TEXTURE_ATLAS_WIDTH_TILES;
        float atlasWidthReal = atlasWidth * 16;
        
        int texX = leavesTexPos % atlasWidth;
        int texY = leavesTexPos / atlasWidth;
        
        float texWidth = leavesTexSize / atlasWidthReal;
        float texHeight = leavesTexSize / atlasWidthReal;
        
        float u0 = texX / (float) atlasWidth;
        float v0 = texY / (float) atlasWidth + tex * texHeight;
        
        float u1 = u0 + texWidth;
        float v1 = v0 + texHeight;

        double width = leavesSize / 2.0;

		double xOff = ((double) ((float) (rand >> 16 & 15L) / 15F) - 0.5D) * 0.25D;
		double zOff = ((double) ((float) (rand >> 24 & 15L) / 15F) - 0.5D) * 0.25D;
        
        double x0 = x + 0.5 - width + xOff;
        double x1 = x + 0.5 + width + xOff;
        
        double z0 = z + 0.5 - width + zOff;
        double z1 = z + 0.5 + width + zOff;
        
        double y0 = y + 0.5 - width;
        double y1 = y + 0.5 + width;
        
        double rot = leavesRotation;
        
        t.addVertexWithUV(x0+rot, y0, z0, u0, v1);
        t.addVertexWithUV(x0+rot, y1, z0, u0, v0);
        t.addVertexWithUV(x1-rot, y1, z1, u1, v0);
        t.addVertexWithUV(x1-rot, y0, z1, u1, v1);
        
        t.addVertexWithUV(x0+rot, y0, z0, u1, v1);
        t.addVertexWithUV(x1-rot, y0, z1, u0, v1);
        t.addVertexWithUV(x1-rot, y1, z1, u0, v0);
        t.addVertexWithUV(x0+rot, y1, z0, u1, v0);
        
        t.addVertexWithUV(x1, y0, z0+rot, u0, v1);
        t.addVertexWithUV(x1, y1, z0+rot, u0, v0);
        t.addVertexWithUV(x0, y1, z1-rot, u1, v0);
        t.addVertexWithUV(x0, y0, z1-rot, u1, v1);
        
        t.addVertexWithUV(x1, y0, z0+rot, u1, v1);
        t.addVertexWithUV(x0, y0, z1-rot, u0, v1);
        t.addVertexWithUV(x0, y1, z1-rot, u0, v0);
        t.addVertexWithUV(x1, y1, z0+rot, u1, v0);
        return true;
	}
	
	public static void log(String str) {
		System.out.print("[BetterFoliageMod] " + str + "\n");
	}
	

}
