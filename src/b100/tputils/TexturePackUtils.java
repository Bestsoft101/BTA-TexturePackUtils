package b100.tputils;

import java.awt.image.BufferedImage;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.util.helper.Textures;
import net.minecraft.core.Global;
import net.minecraft.core.world.World;
import net.minecraft.core.world.season.Season;
import net.minecraft.core.world.season.SeasonManager;
import net.minecraft.core.world.season.SeasonManagerCycle;

public abstract class TexturePackUtils {
	
	public static Minecraft mc;
	
	public static List<TexturePack> selectedPacks;
	
	public static void onStartup(Minecraft minecraft) {
		mc = minecraft;
	}
	
	public static void onLoad() {
		selectedPacks = mc.texturePackList.selectedPacks;
	}
	
	public static BufferedImage readTexture(String path) {
		BufferedImage image = mc.renderEngine.getImage(path);
		if(image == Textures.missingTexture) {
			// throw new RuntimeException("Texture does not exist: '" + path + "'");
			return null;
		}
		return image;
	}
	
	public static float getSeasonProgress(World world, Season currentSeason, int dayInSeason) {
		if(currentSeason == null) {
			return 0.0f;
		}
		
		SeasonManager seasonManager = world.getSeasonManager();
		if(seasonManager instanceof SeasonManagerCycle) {
			SeasonManagerCycle seasonManagerCycle = (SeasonManagerCycle) seasonManager;
			
			int seasonLengthTicks = seasonManagerCycle.getSeasonLengthTicks(currentSeason);
			int dayLengthTicks = Global.DAY_LENGTH_TICKS;
			
			return dayInSeason / (float) (seasonLengthTicks / dayLengthTicks);
		}
		
		return 0.5f;
	}

}
