package b100.tputils.betterfoliage;

import b100.tputils.ConfigHelper;
import b100.tputils.TexturePackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.block.color.BlockColor;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.client.render.stitcher.TextureRegistry;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tessellator.TessellatorStandard;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public abstract class BetterFoliageMod {
	
	private static Minecraft mc;
	
	private static final String configFileName = "/assets/betterfoliage/textures/block/betterfoliage.properties";
	
	public static boolean enableBetterGrass = false;
	private static IconCoordinate[] grassTextures;
	private static double grassWidth;
	private static double grassHeight;
	private static double grassRotation;
	
	public static boolean enableBetterScorchedGrass = false;
	private static IconCoordinate[] scorchedGrassTextures;
	
	public static boolean enableBetterRetroGrass = false;
	private static IconCoordinate[] retroGrassTextures;
	
	public static boolean enableBetterLeaves = false;
	private static IconCoordinate oakTex;
	private static IconCoordinate birchTex;
	private static IconCoordinate pineTex;
	private static IconCoordinate cherryTex;
	private static IconCoordinate eucalyptusTex;
	private static IconCoordinate retroTex;
	private static IconCoordinate shrubTex;
	private static IconCoordinate cacaoTex;
	private static IconCoordinate thornTex;
	private static IconCoordinate palmTex;
	private static double leavesSize;
	private static double leavesRotation;
	
	private static final boolean shaderModInstalled;
	
	static {
		boolean flag = true;
		try {
			b100.shaders.asm.Listeners.class.getName();
		}catch (Throwable e) {
			flag = false;
		}
		shaderModInstalled = flag;
	}
	
	public static void onStartup(Minecraft minecraft) {
		log("Startup!");
		
		mc = minecraft;
	}
	
	public static void onLoad() {
		enableBetterGrass = false;
		grassTextures = null;
		grassWidth = 1.0;
		grassHeight = 1.0;
		grassRotation = 0.0;

		enableBetterScorchedGrass = false;
		scorchedGrassTextures = null;
		
		enableBetterRetroGrass = false;
		retroGrassTextures = null;
		
		enableBetterLeaves = false;
		oakTex = null;
		birchTex = null;
		pineTex = null;
		cherryTex = null;
		eucalyptusTex = null;
		retroTex = null;
		shrubTex = null;
		cacaoTex = null;
		thornTex = null;
		palmTex = null;
		leavesSize = 1.0;
		leavesRotation = 0.0;
		
		ConfigHelper.readConfigFromCurrentTexturePack(mc, configFileName, (key, value) -> parseConfig(key, value));

		if(enableBetterGrass) {
			if(grassTextures == null || grassTextures.length == 0) {
				log("No grass textures, disabling better grass");
				enableBetterGrass = false;
			}
		}
		if(enableBetterRetroGrass) {
			if(grassTextures == null || grassTextures.length == 0) {
				log("No retro grass textures, disabling better grass");
				enableBetterGrass = false;
			}
		}
		if(enableBetterScorchedGrass) {
			if(grassTextures == null || grassTextures.length == 0) {
				log("No scorched grass textures, disabling better grass");
				enableBetterGrass = false;
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
			
		}else if(key.equals("grassTextures")) {
			grassTextures = parseTextures(value);
			
		}else if(key.equals("grassWidth")) {
			grassWidth = Double.parseDouble(value);
			
		}else if(key.equals("grassHeight")) {
			grassHeight = Double.parseDouble(value); 
			
		}else if(key.equals("grassRotation")) {
			grassRotation = Double.parseDouble(value); 
			
		}else if(key.equals("enableBetterScorchedGrass")) {
			enableBetterScorchedGrass = Boolean.parseBoolean(value); 
			
		}else if(key.equals("scorchedGrassTextures")) {
			scorchedGrassTextures = parseTextures(value);
			
		}else if(key.equals("enableBetterRetroGrass")) {
			enableBetterRetroGrass = Boolean.parseBoolean(value);
			
		}else if(key.equals("retroGrassTextures")) {
			retroGrassTextures = parseTextures(value);
			
		}else if(key.equals("enableBetterLeaves")) {
			enableBetterLeaves = Boolean.parseBoolean(value);
			
		}else if(key.equals("oakTex")) {
			oakTex = parseTexture(value);
			
		}else if(key.equals("birchTex")) {
			birchTex = parseTexture(value);
			
		}else if(key.equals("pineTex")) {
			pineTex = parseTexture(value);
			
		}else if(key.equals("cherryTex")) {
			cherryTex = parseTexture(value);
			
		}else if(key.equals("eucalyptusTex")) {
			eucalyptusTex = parseTexture(value);
			
		}else if(key.equals("retroTex")) {
			retroTex = parseTexture(value);
			
		}else if(key.equals("shrubTex")) {
			shrubTex = parseTexture(value);
			
		}else if(key.equals("cacaoTex")) {
			cacaoTex = parseTexture(value);
			
		}else if(key.equals("thornTex")) {
			thornTex = parseTexture(value);
			
		}else if(key.equals("palmTex")) {
			palmTex = parseTexture(value);
			
		}else if(key.equals("leavesSize")) {
			leavesSize = Double.parseDouble(value);
			
		}else if(key.equals("leavesRotation")) {
			leavesRotation = Double.parseDouble(value);
			
		}else {
			log("Unknown Option: " + key);
		}
	}
	
	public static IconCoordinate[] parseTextures(String value) {
		String[] textureNames = value.split(",");
		IconCoordinate[] coordinates = new IconCoordinate[textureNames.length];
		for(int i=0; i < coordinates.length; i++) {
			coordinates[i] = TextureRegistry.getTexture("betterfoliage:block/" + textureNames[i]); 
		}
		return coordinates;
	}
	
	public static IconCoordinate parseTexture(String value) {
		return TextureRegistry.getTexture("betterfoliage:block/" + value); 
	}
	
	public static void onRenderBlock(int x, int y, int z) {
		World world = TexturePackUtils.mc.theWorld;
		Block block = world.getBlock(x, y, z);
		
		if(enableBetterGrass) {
			if(block == Block.grass && world.getBlockId(x, y + 1, z) == 0) {
				renderBetterGrass(world, block, x, y, z, grassTextures);
			}else if(block == Block.grassRetro && world.getBlockId(x, y + 1, z) == 0) {
				renderBetterGrass(world, block, x, y, z, retroGrassTextures);
			}else if(block == Block.grassScorched && world.getBlockId(x, y + 1, z) == 0) {
				renderBetterGrass(world, block, x, y, z, scorchedGrassTextures);
			}
		}
		
		if(enableBetterLeaves) {
			if(block == Block.leavesOak) {
				renderBetterLeaves(world, block, x, y, z, oakTex);
			}else if(block == Block.leavesBirch) {
				renderBetterLeaves(world, block, x, y, z, birchTex);
			}else if(block == Block.leavesPine) {
				renderBetterLeaves(world, block, x, y, z, pineTex);
			}else if(block == Block.leavesEucalyptus) {
				renderBetterLeaves(world, block, x, y, z, eucalyptusTex);
			}else if(block == Block.leavesCherry || block == Block.leavesCherryFlowering) {
				renderBetterLeaves(world, block, x, y, z, cherryTex);
			}else if(block == Block.leavesOakRetro) {
				renderBetterLeaves(world, block, x, y, z, retroTex);
			}else if(block == Block.leavesShrub) {
				renderBetterLeaves(world, block, x, y, z, shrubTex);
			}else if(block == Block.leavesCacao) {
				renderBetterLeaves(world, block, x, y, z, cacaoTex);
			}else if(block == Block.leavesThorn) {
				renderBetterLeaves(world, block, x, y, z, thornTex);
			}else if(block == Block.leavesPalm) {
				renderBetterLeaves(world, block, x, y, z, palmTex);
			}
		}
	}
	
	public static boolean renderBetterGrass(WorldSource worldSource, Block block, int x, int y, int z, IconCoordinate[] textures) {
		if(textures == null || textures.length == 0) {
			return false;
		}
		if(shaderModInstalled) {
			b100.shaders.asm.Listeners.setBlockID(Block.tallgrass);
		}
		
		Tessellator t = TessellatorStandard.instance;
		
		float brightness = 1.0f;
		if(LightmapHelper.isLightmapEnabled()) {
			t.setLightmapCoord(worldSource.getLightmapCoord(x, y + 1, z, 0));
		}else {
			brightness = block.getBlockBrightness(mc.theWorld, x, y + 1, z);
		}
		
		float r, g, b;
		BlockColor blockColor = BlockColorDispatcher.getInstance().getDispatch(block);
		if(blockColor != null) {
			int color = blockColor.getWorldColor(worldSource, x, y, z);
			
			r = ((color >> 16) & 0xFF) / 255.0f;
			g = ((color >>  8) & 0xFF) / 255.0f;
			b = ((color      ) & 0xFF) / 255.0f;
		}else {
			// No idea if this can even happen
			r = g = b = 1.0f;
		}
		
		t.setColorOpaque_F(r * brightness, g * brightness, b * brightness);
		
		long rand = (long)(x * 0x2fc20f) ^ (long)z * 0x6ebfff5L ^ (long)y;
		rand = rand * rand * 0x285b825L + rand * 11L;
		int tex = (int) (((rand & 0xFFFF) / (float)0xFFFF) * textures.length);
		
		IconCoordinate texture = textures[tex];
		
		double u0 = texture.getIconUMin();
		double v0 = texture.getIconVMin();
		
		double u1 = texture.getIconUMax();
		double v1 = texture.getIconVMax();
		
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
		
		if(shaderModInstalled) b100.shaders.asm.Listeners.setIsTopVertex(0.0f);
		t.addVertexWithUV(x0+rot, y0, z0, u0, v1);
		if(shaderModInstalled) b100.shaders.asm.Listeners.setIsTopVertex(1.0f);
		t.addVertexWithUV(x0+rot, y1, z0, u0, v0);
		t.addVertexWithUV(x1-rot, y1, z1, u1, v0);
		if(shaderModInstalled) b100.shaders.asm.Listeners.setIsTopVertex(0.0f);
		t.addVertexWithUV(x1-rot, y0, z1, u1, v1);
		
		if(shaderModInstalled) b100.shaders.asm.Listeners.setIsTopVertex(0.0f);
		t.addVertexWithUV(x0+rot, y0, z0, u1, v1);
		t.addVertexWithUV(x1-rot, y0, z1, u0, v1);
		if(shaderModInstalled) b100.shaders.asm.Listeners.setIsTopVertex(1.0f);
		t.addVertexWithUV(x1-rot, y1, z1, u0, v0);
		t.addVertexWithUV(x0+rot, y1, z0, u1, v0);
		
		if(shaderModInstalled) b100.shaders.asm.Listeners.setIsTopVertex(0.0f);
		t.addVertexWithUV(x1, y0, z0+rot, u0, v1);
		if(shaderModInstalled) b100.shaders.asm.Listeners.setIsTopVertex(1.0f);
		t.addVertexWithUV(x1, y1, z0+rot, u0, v0);
		t.addVertexWithUV(x0, y1, z1-rot, u1, v0);
		if(shaderModInstalled) b100.shaders.asm.Listeners.setIsTopVertex(0.0f);
		t.addVertexWithUV(x0, y0, z1-rot, u1, v1);
		
		if(shaderModInstalled) b100.shaders.asm.Listeners.setIsTopVertex(0.0f);
		t.addVertexWithUV(x1, y0, z0+rot, u1, v1);
		t.addVertexWithUV(x0, y0, z1-rot, u0, v1);
		if(shaderModInstalled) b100.shaders.asm.Listeners.setIsTopVertex(1.0f);
		t.addVertexWithUV(x0, y1, z1-rot, u0, v0);
		t.addVertexWithUV(x1, y1, z0+rot, u1, v0);
		if(shaderModInstalled) b100.shaders.asm.Listeners.setIsTopVertex(0.0f);
		return true;
	}
	
	public static boolean renderBetterLeaves(WorldSource worldSource, Block block, int x, int y, int z, IconCoordinate texture) {
		if(texture == null) {
			return false;
		}

		Tessellator t = TessellatorStandard.instance;
		
		float brightness = 1.0f;
		if(LightmapHelper.isLightmapEnabled()) {
			t.setLightmapCoord(worldSource.getLightmapCoord(x, y, z, 0));
		}else {
			brightness = block.getBlockBrightness(mc.theWorld, x, y, z);
		}
		
		float r, g, b;
		BlockColor blockColor = BlockColorDispatcher.getInstance().getDispatch(block);
		if(blockColor != null) {
			int color = blockColor.getWorldColor(worldSource, x, y, z);
			
			r = ((color >> 16) & 0xFF) / 255.0f;
			g = ((color >>  8) & 0xFF) / 255.0f;
			b = ((color      ) & 0xFF) / 255.0f;
		}else {
			// No idea if this can even happen
			r = g = b = 1.0f;
		}
		
		t.setColorOpaque_F(r * brightness, g * brightness, b * brightness);
		
		long rand = (long)(x * 0x2fc20f) ^ (long)z * 0x6ebfff5L ^ (long)y;
		rand = rand * rand * 0x285b825L + rand * 11L;
		
		double u0 = texture.getIconUMin();
		double v0 = texture.getIconVMin();
		
		double u1 = texture.getIconUMax();
		double v1 = texture.getIconVMax();
		
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
