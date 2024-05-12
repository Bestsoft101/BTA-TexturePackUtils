package b100.tputils.customatlas;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import b100.tputils.TexturePackUtils;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackCustom;
import net.minecraft.client.render.texturepack.TexturePackList;
import net.minecraft.client.util.helper.Textures;

public class CustomAtlasMod {

	private static Atlas legacyTerrainAtlas = null;
	private static Atlas legacyTerrainOldAtlas = null;
	private static Atlas legacyItemsAtlas = null;
	
	private static Map<String, BufferedImage> textureOverrides = new HashMap<>();
	
	private static void loadLegacyTiles() {
		if(legacyTerrainAtlas != null && legacyItemsAtlas != null) {
			return;
		}
		
		log("Loading legacy tiles");

		legacyTerrainAtlas = Atlas.read(CustomAtlasMod.class.getResourceAsStream("/terrain.tiles"));
		legacyTerrainOldAtlas = Atlas.read(CustomAtlasMod.class.getResourceAsStream("/terrain_old.tiles"));
		legacyItemsAtlas = Atlas.read(CustomAtlasMod.class.getResourceAsStream("/items.tiles"));
		
		log(legacyTerrainAtlas.size() + " terrain tiles, " + legacyItemsAtlas.size() + " item tiles");
	}
	
	public static void beforeRefreshTextures() {
		textureOverrides.clear();
		
		List<TexturePack> texturePacks = TexturePackUtils.mc.texturePackList.selectedPacks;
		List<String> overrides = new ArrayList<>();
		
		log("Active Texture Packs: ");
		for(int texturePackIndex=0; texturePackIndex < texturePacks.size(); texturePackIndex++) {
			TexturePack texturePack = texturePacks.get(texturePackIndex);
			log("  " + texturePack.manifest.getName());

			if(texturePack.hasFile("/terrain.png")) {
				log("    has terrain.png");
				loadLegacyTiles();
				loadAtlas(texturePack, legacyTerrainAtlas, "/terrain.png", "/assets/minecraft/textures/block/");
			}

			if(texturePack.hasFile("/terrain_old.png")) {
				log("    has terrain_old.png");
				loadLegacyTiles();
				loadAtlas(texturePack, legacyTerrainOldAtlas, "/terrain_old.png", "/assets/minecraft/textures/block/");
			}
			
			if(texturePack.hasFile("/gui/items.png")) {
				log("    has items.png");
				loadLegacyTiles();
				loadAtlas(texturePack, legacyItemsAtlas, "/gui/items.png", "/assets/minecraft/textures/item/");
			}
			
			List<String> namespaces = findEntries(texturePack, "/assets/", (s) -> s.endsWith("/"));
			if(namespaces != null) {
				for(int namespaceIndex=0; namespaceIndex < namespaces.size(); namespaceIndex++) {
					String namespace = namespaces.get(namespaceIndex);
					
					log("    namespace: " + namespace);
					
					List<String> customBlockAtlases = findEntries(texturePack, namespace + "textures/block/", (s) -> s.endsWith(".tiles"));
					if(customBlockAtlases != null) {
						for(int i=0; i < customBlockAtlases.size(); i++) {
							String customAtlasPath = customBlockAtlases.get(i);
							log("      found custom atlas: " + customAtlasPath);
							loadAtlas(texturePack, Atlas.read(texturePack.getResourceAsStream(customAtlasPath)), customAtlasPath.replace(".tiles", ".png"), namespace + "textures/block/");
						}
					}
					
					List<String> customItemAtlases = findEntries(texturePack, namespace + "textures/item/", (s) -> s.endsWith(".tiles"));
					if(customItemAtlases != null) {
						for(int i=0; i < customItemAtlases.size(); i++) {
							String customAtlasPath = customItemAtlases.get(i);
							log("      found custom atlas: " + customAtlasPath);
							loadAtlas(texturePack, Atlas.read(texturePack.getResourceAsStream(customAtlasPath)), customAtlasPath.replace(".tiles", ".png"), namespace + "textures/item/");
						}
					}

				}
			}
			
			overrides.clear();
			overrides.addAll(textureOverrides.keySet());
			for(int i=0; i < overrides.size(); i++) {
				String override = overrides.get(i);
				if(texturePack.hasFile(override)) {
					textureOverrides.remove(override);
				}
			}
		}
	}
	
	public static void loadAtlas(TexturePack texturePack, Atlas atlas, String atlasTexturePath, String pathPrefix) {
		if(atlas.width <= 0 || atlas.height <= 0) {
			throw new IllegalArgumentException("Invalid atlas size: " + atlas.width + "x" + atlas.height);
		}
		
		BufferedImage image = Textures.readImage(texturePack.getResourceAsStream(atlasTexturePath));
		if(image == null) {
			throw new RuntimeException("Atlas image does not exist: " + atlasTexturePath);
		}
		
		int tileWidth = image.getWidth() / atlas.width;
		int tileHeight = image.getHeight() / atlas.height;
		
		List<AtlasTile> atlasTiles = atlas.tileList;
		for(int tileIndex = 0; tileIndex < atlasTiles.size(); tileIndex++) {
			AtlasTile atlasTile = atlasTiles.get(tileIndex);
			
			BufferedImage tileImage = image.getSubimage(tileWidth * atlasTile.x, tileHeight * atlasTile.y, tileWidth, tileHeight);
			
			String path = pathPrefix + atlasTile.name + ".png";
			textureOverrides.put(path, tileImage);
		}
	}
	
	public static List<String> findEntries(TexturePack texturePack, String path, Filter filter) {
		if(!path.startsWith("/")) {
			throw new IllegalArgumentException(path);
		}
		
		if(texturePack instanceof TexturePackCustom) {
			TexturePackCustom texturePackCustom = (TexturePackCustom) texturePack;
			
			if(texturePackCustom.file.isDirectory()) {
				List<String> result = new ArrayList<>();
				
				File searchDirectory = new File(texturePackCustom.file, path.substring(1));
				File[] files = searchDirectory.listFiles();
				if(files == null) {
					return null;
				}
				
				for(int i=0; i < files.length; i++) {
					File file = files[i];
					String relativePath = files[i].getAbsolutePath().substring(texturePackCustom.file.getAbsolutePath().length());
					relativePath = relativePath.replace('\\', '/');
					if(file.isDirectory()) {
						relativePath = relativePath + "/";
					}
					if(filter == null || filter.accept(relativePath)) {
						result.add(relativePath);
					}
				}
				return result;
			}
			if(texturePackCustom.file.isFile()) {
				path = path.substring(1);
				
				List<String> result = new ArrayList<>();
				
				ZipFile zipFile = null;
				try {
					zipFile = new ZipFile(texturePackCustom.file);
					
					Enumeration<? extends ZipEntry> entries = zipFile.entries();
					while(entries.hasMoreElements()) {
						String entry = entries.nextElement().toString();
						if(!entry.startsWith(path)) {
							continue;
						}
						
						entry = entry.substring(path.length());
						if(entry.length() == 0) {
							continue;
						}
						
						// Only find entries in the given directory, no subdirectories
						if(entry.indexOf('/') != entry.lastIndexOf('/')) {
							continue;
						}
						
						entry = "/" + path + entry;
						if(filter == null || filter.accept(entry)) {
							result.add(entry);
						}
					}
				}catch (Exception e) {
					throw new RuntimeException(e);
				}finally {
					try {
						zipFile.close();
					}catch (Exception e) {}
				}
				return result;
			}
		}
		
		return null;
	}
	
	public static BufferedImage getTextureOverride(TexturePackList texturePackList, String path) {
		return textureOverrides.get(path);
	}
	
	public static void log(String str) {
		System.out.print("[CustomAtlasMod] " + str + "\n");
	}
	
	static interface Filter {
		
		public boolean accept(String string);
		
	}
	
}
