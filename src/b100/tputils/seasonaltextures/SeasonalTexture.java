package b100.tputils.seasonaltextures;

import java.awt.image.BufferedImage;

import net.minecraft.core.world.season.Season;
import net.minecraft.core.world.season.Seasons;

public class SeasonalTexture {
	
	public int[] atlasTiles;
	public BufferedImage image;
	
	public int defaultTile;
	public int springTile = -1;
	public int summerTile = -1;
	public int autumnTile = -1;
	public int winterTile = -1;
	
	public boolean blend = false;
	
	public int getTileForSeason(Season season) {
		if(season == Seasons.OVERWORLD_SPRING) {
			return springTile != -1 ? springTile : defaultTile;
		}else if(season == Seasons.OVERWORLD_SUMMER) {
			return summerTile != -1 ? summerTile : defaultTile;
		}else if(season == Seasons.OVERWORLD_FALL) {
			return autumnTile != -1 ? autumnTile : defaultTile;
		}else if(season == Seasons.OVERWORLD_WINTER) {
			return winterTile != -1 ? winterTile : defaultTile;
		}
		return defaultTile;
	}

}
