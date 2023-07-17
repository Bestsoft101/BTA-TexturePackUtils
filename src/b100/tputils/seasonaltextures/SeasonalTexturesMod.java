package b100.tputils.seasonaltextures;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import b100.json.element.JsonArray;
import b100.json.element.JsonEntry;
import b100.json.element.JsonObject;
import b100.tputils.TexturePackUtils;
import b100.utils.StringReader;
import b100.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextureFX;
import net.minecraft.core.Global;
import net.minecraft.core.util.helper.Buffer;
import net.minecraft.core.world.World;
import net.minecraft.core.world.season.Season;

public abstract class SeasonalTexturesMod {
	
	public static Minecraft mc;
	
	private static List<SeasonalTexture> textures = new ArrayList<>();
	
	private static Season lastTickSeason;
	private static World lastTickWorld;
	private static float lastTickSeasonProgress;
	
	public static void onStartup(Minecraft minecraft) {
		mc = minecraft;
	}
	
	public static void onLoad() {
		setupSeasonalTextures();
		
		updateSeasonalTextures(true);
	}
	
	public static void update() {
		updateSeasonalTextures(false);
	}
	
	public static void setupSeasonalTextures() {
		log("Setup Seasonal Textures");
		
		textures.clear();
		
		InputStream stream = TexturePackUtils.selectedTexturePack.getResourceAsStream("/seasonaltextures/seasonaltextures.json");
		if(stream == null) {
			log("No seasonaltextures json");
			return;
		}
		
		JsonObject jsonObject = new JsonObject(new StringReader(StringUtils.readInputString(stream)));
		
		for(JsonEntry entry : jsonObject) {
			log("Loading texture: " + entry.name);
			
			JsonObject textureObject = entry.value.getAsObject();
			
			SeasonalTexture texture = new SeasonalTexture();
			
			if(textureObject.has("atlasTile")) {
				texture.atlasTiles = new int[] { textureObject.getInt("atlasTile") };
			}else if(textureObject.has("atlasTiles")) {
				JsonArray array = textureObject.get("atlasTiles").getAsArray();
				texture.atlasTiles = new int[array.length()];
				for(int i=0; i < texture.atlasTiles.length; i++) {
					texture.atlasTiles[i] = array.get(i).getAsNumber().getInteger();
				}
			}else {
				log("No atlas tiles specified!");
				continue;
			}
			
			texture.defaultTile = textureObject.getInt("default");
			texture.springTile = textureObject.getInt("spring", -1);
			texture.summerTile = textureObject.getInt("summer", -1);
			texture.autumnTile = textureObject.getInt("autumn", -1);
			texture.winterTile = textureObject.getInt("winter", -1);
			texture.blend = textureObject.getBoolean("blend", false);

			String textureName = entry.name;
			if(textureObject.has("texture")) {
				textureName = textureObject.getString("texture");
			}
			texture.image = TexturePackUtils.readTexture("/seasonaltextures/" + textureName + ".png");
			
			
			if(texture.defaultTile < 0) {
				log("Invalid default tile: "+texture.defaultTile+".");
				continue;
			}
			
			int maxTile = texture.defaultTile;
			maxTile = Math.max(maxTile, texture.springTile);
			maxTile = Math.max(maxTile, texture.summerTile);
			maxTile = Math.max(maxTile, texture.autumnTile);
			maxTile = Math.max(maxTile, texture.winterTile);
			
			int expectedWidth = (maxTile + 1) * TextureFX.tileWidthTerrain;
			if(texture.image.getWidth() != expectedWidth) {
				log("Invalid texture width: "+texture.image.getWidth()+", expected "+expectedWidth+"!");
				continue;
			}
			int expectedHeight = texture.atlasTiles.length * TextureFX.tileWidthTerrain;
			if(texture.image.getHeight() != expectedHeight) {
				log("Invalid texture height: "+texture.image.getHeight()+"!");
				continue;
			}
			
			textures.add(texture);
		}
	}
	
	public static void updateSeasonalTextures(boolean force) {
		if(mc.theWorld == null) {
			lastTickWorld = null;
			return;
		}
		
		World world = mc.theWorld;
		Season currentSeason = world.seasonManager.getCurrentSeason();
		int dayInSeason = world.seasonManager.getDayInSeason();
		float seasonProgress = TexturePackUtils.getSeasonProgress(world, currentSeason, dayInSeason);
		
		boolean update = force || lastTickWorld != world || lastTickSeason != currentSeason || lastTickSeasonProgress != seasonProgress;
		if(!update) {
			return;
		}
		
		log("Update Textures");

		lastTickSeason = currentSeason;
		lastTickSeasonProgress = seasonProgress;
		lastTickWorld = world;
		
		int resolution = TextureFX.tileWidthTerrain;
		log("Resolution: "+resolution+"x");
		
		glBindTexture(GL_TEXTURE_2D, TexturePackUtils.mc.renderEngine.getTexture("/terrain.png"));
		
		int bufferSize = resolution * resolution * 4;
		Buffer.checkBufferSize(bufferSize);
		ByteBuffer buffer = Buffer.buffer;
		buffer.limit(bufferSize);
		
		for(int i=0; i < textures.size(); i++) {
			updateSeasonalTexture(textures.get(i), buffer);
		}
	}
	
	public static void updateSeasonalTexture(SeasonalTexture texture, ByteBuffer buffer) {
		for(int atlasTileIndex = 0; atlasTileIndex < texture.atlasTiles.length; atlasTileIndex++) {
			int atlasTile = texture.atlasTiles[atlasTileIndex];
			if(atlasTile < 0) {
				continue;
			}
			
			int resolution = TextureFX.tileWidthTerrain;
			int tile = texture.getTileForSeason(lastTickSeason);
			
			int offsetX = tile * resolution;
			int offsetY = atlasTileIndex * resolution;
			
			log("Offset: "+offsetX+" Image size: "+texture.image.getWidth()+"x"+texture.image.getHeight());
			
			int tileX = atlasTile % Global.TEXTURE_ATLAS_WIDTH_TILES;
			int tileY = atlasTile / Global.TEXTURE_ATLAS_WIDTH_TILES;
			
			BufferedImage image = texture.image;

			buffer.position(0);
			for(int y=0; y < resolution; y++) {
				for(int x=0; x < resolution; x++) {
					int color = image.getRGB(x + offsetX, y + offsetY);
					
					int a = (color >> 24) & 0xFF;
					int r = (color >> 16) & 0xFF;
					int g = (color >>  8) & 0xFF;
					int b = (color >>  0) & 0xFF;
					
					buffer.put((byte) r);
					buffer.put((byte) g);
					buffer.put((byte) b);
					buffer.put((byte) a);
				}
			}
			
			buffer.position(0);
			glTexSubImage2D(GL_TEXTURE_2D, 0, tileX * resolution, tileY * resolution, resolution, resolution, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		}
	}
	
	public static void log(String str) {
		System.out.print("[SeasonalTexturesMod] " + str + "\n");
	}

}
