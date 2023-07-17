package b100.tputils;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texturepack.TexturePackBase;
import net.minecraft.core.Global;
import net.minecraft.core.world.World;
import net.minecraft.core.world.season.Season;
import net.minecraft.core.world.season.SeasonManager;
import net.minecraft.core.world.season.SeasonManagerCycle;

public abstract class TexturePackUtils {
	
	public static Minecraft mc;
	
	public static TexturePackBase selectedTexturePack;
	
	public static void onStartup(Minecraft minecraft) {
		mc = minecraft;
	}
	
	public static void onLoad() {
		selectedTexturePack = mc.texturePackList.selectedTexturePack;
	}
	
	public static BufferedImage readTexture(String path) {
		InputStream stream = selectedTexturePack.getResourceAsStream(path);
		if(stream == null) {
			throw new NullPointerException("Resource does not exist: '"+path+"'!");
		}
		try {
			return ImageIO.read(stream);
		}catch (Exception e) {
			throw new RuntimeException("Error reading texture: '"+path+"'!");
		}finally {
			try {
				stream.close();
			}catch (Exception e) {}
		}
	}
	
	public static float getSeasonProgress(World world, Season currentSeason, int dayInSeason) {
		if(currentSeason == null) {
			return 0.0f;
		}
		
		SeasonManager seasonManager = world.seasonManager;
		if(seasonManager instanceof SeasonManagerCycle) {
			SeasonManagerCycle seasonManagerCycle = (SeasonManagerCycle) seasonManager;
			
			int seasonLengthTicks = seasonManagerCycle.getSeasonLengthTicks(currentSeason);
			int dayLengthTicks = Global.DAY_LENGTH_TICKS;
			
			return dayInSeason / (float) (seasonLengthTicks / dayLengthTicks);
		}
		
		return 0.5f;
	}

}
