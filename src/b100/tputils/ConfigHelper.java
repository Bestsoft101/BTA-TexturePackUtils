package b100.tputils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackCustom;
import net.minecraft.client.render.texturepack.TexturePackDefault;

public abstract class ConfigHelper {
	
	private static List<Closeable> closeables = new ArrayList<>();
	
	public static void readConfigFromCurrentTexturePack(Minecraft minecraft, String configFilePath, ConfigHandler configHandler) {
		InputStream stream = getResourceFromCurrentTexturePack(minecraft, configFilePath);
		if(stream == null) {
			log("No config file in texture pack: " + configFilePath);
			return;
		}
		readConfig(stream, configHandler);
	}
	
	/**
	 * Get a resource only from the current texture pack.
	 * If the resource does not exist in the texture pack, it will not get it from the jar file.
	 */
	public static InputStream getResourceFromCurrentTexturePack(Minecraft minecraft, String path) {
		TexturePack texturePack = minecraft.texturePackList.selectedTexturePack;
		
		if(texturePack instanceof TexturePackDefault) {
			return ConfigHelper.class.getResourceAsStream(path);
		}
		
		if(texturePack instanceof TexturePackCustom) {
			TexturePackCustom texturePackCustom = (TexturePackCustom) texturePack;
			File texturePackFile = texturePackCustom.file;
			if(texturePackFile.isDirectory()) {
				File file = new File(texturePackFile, path);
				
				if(file.exists()) {
					try {
						return new FileInputStream(file);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					return null;
				}
			}else {
				ZipFile zip = null;
				try{
					zip = new ZipFile(texturePackFile);
					closeables.add(zip);
					
					ZipEntry entry = zip.getEntry(path.substring(1));
					if(entry == null) {
						return null;
					}
					return zip.getInputStream(entry);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return texturePack.getResourceAsStream(path);
	}
	
	private static void closeInputStreams() {
		for(int i=0; i < closeables.size(); i++) {
			try{
				closeables.get(i).close();
			}catch (Exception e) {}
		}
		closeables.clear();
	}
	
	public static void readConfig(InputStream stream, ConfigHandler configHandler) {
		if(stream == null) {
			return;
		}
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(stream));
			
			while(true) {
				String line = br.readLine();
				if(line == null) {
					break;
				}
				
				int splitIndex = line.indexOf('=');
				if(splitIndex == -1) {
					continue;
				}
				
				String key = line.substring(0, splitIndex);
				String value = line.substring(splitIndex + 1);

				try{
					configHandler.parseConfig(key, value);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				br.close();
			}catch (Exception e) {}
			try {
				stream.close();
			}catch (Exception e) {}
		}
		
		closeInputStreams();
	}
	
	public static interface ConfigHandler {
		
		public void parseConfig(String key, String value);
		
	}
	
	public static void log(String string) {
		System.out.println("[ConfigHelper] " + string);
	}
	
}
