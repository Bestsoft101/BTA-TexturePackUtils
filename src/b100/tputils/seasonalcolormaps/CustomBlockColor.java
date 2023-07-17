package b100.tputils.seasonalcolormaps;

import b100.tputils.seasonalcolormaps.SeasonalColormapsMod.ColorHandler;
import net.minecraft.client.render.block.color.BlockColor;
import net.minecraft.core.world.World;

public class CustomBlockColor extends BlockColor {

	public BlockColor previous;
	public ColorHandler colorHandler;
	
	public CustomBlockColor(BlockColor previous, ColorHandler colorHandler) {
		this.previous = previous;
		this.colorHandler = colorHandler;
	}
	
	@Override
	public int getFallbackColor(int var1) {
		return previous.getFallbackColor(var1);
	}

	@Override
	public int getWorldColor(World world, int x, int y, int z) {
		if(colorHandler.enable()) {
			return colorHandler.getColor().getFromWorldPos(world, x, z);
		}
		return previous.getWorldColor(world, x, y, z);
	}

}
