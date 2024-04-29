package b100.tputils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texturepack.TexturePack;

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
	 * Get a resource only from the selected texture packs, not from the default pack
	 */
	public static InputStream getResourceFromCurrentTexturePack(Minecraft minecraft, String path) {
		List<TexturePack> selectedPacks = minecraft.texturePackList.selectedPacks;
		
		for(int i=0; i < selectedPacks.size(); i++) {
			TexturePack texturePack = selectedPacks.get(i);
			
			InputStream stream = texturePack.getResourceAsStream(path);
			if(stream != null) {
				return stream;
			}
		}
		return null;
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
