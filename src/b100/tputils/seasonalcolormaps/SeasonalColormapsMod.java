package b100.tputils.seasonalcolormaps;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import b100.tputils.ConfigHelper;
import b100.tputils.TexturePackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.season.Season;
import net.minecraft.core.world.season.Seasons;

public abstract class SeasonalColormapsMod {
	
	public static Minecraft mc;
	
	private static World lastTickWorld;
	private static Season lastTickSeason;
	private static float lastTickSeasonProgress;
	
	public static final ColorHandler grassColor = new ColorHandler("/grasscolor.png");
	public static final ColorHandler foliageColor = new ColorHandler("/foliagecolor.png");
	public static final ColorHandler waterColor = new ColorHandler("/watercolor.png");
	public static final ColorHandler birchColor = new ColorHandler("/birchcolor.png");
	public static final ColorHandler cherryColor = new ColorHandler("/cherrycolor.png");
	public static final ColorHandler pineColor = new ColorHandler("/pinecolor.png");
	public static final ColorHandler eucalyptusColor = new ColorHandler("/eucalyptuscolor.png");
	public static final ColorHandler shrubColor = new ColorHandler("/shrubcolor.png");
	public static final ColorHandler skyColor = new ColorHandler("/skycolor.png");
	public static final ColorHandler seaColor = new ColorHandler("/seacolor.png");
	public static final ColorHandler fogColor = new ColorHandler("/fogcolor.png");
	
	public static float fadeDuration = 1.0f;
	
	private static List<ColorHandler> colors = new ArrayList<>();
	
	private static final String configFileName = "/misc/seasonalcolormaps.properties";
	
	private static boolean registeredDispatchs = false;
	
	static {
		colors.add(grassColor);
		colors.add(foliageColor);
		colors.add(waterColor);
		colors.add(birchColor);
		colors.add(cherryColor);
		colors.add(pineColor);
		colors.add(eucalyptusColor);
		colors.add(shrubColor);
//		colors.add(skyColor);
//		colors.add(seaColor);
//		colors.add(fogColor);
	}
	
	public static void onStartup(Minecraft minecraft) {
		log("Startup!");
		
		mc = minecraft;
	}
	
	public static void onLoad() {
		loadConfig(mc);
		
		updateColors(true);
		
		if(!registeredDispatchs) {
			registeredDispatchs = true;
			
			BlockColorDispatcher blockColorDispatcher = BlockColorDispatcher.getInstance();

			addBlockColorOverride(blockColorDispatcher, Block.grass, grassColor);
			addBlockColorOverride(blockColorDispatcher, Block.tallgrass, grassColor);
			addBlockColorOverride(blockColorDispatcher, Block.tallgrassFern, grassColor);
			
			addBlockColorOverride(blockColorDispatcher, Block.leavesOak, foliageColor);
			addBlockColorOverride(blockColorDispatcher, Block.leavesBirch, birchColor);
			addBlockColorOverride(blockColorDispatcher, Block.leavesCherry, cherryColor);
			addBlockColorOverride(blockColorDispatcher, Block.leavesCherryFlowering, cherryColor);
			addBlockColorOverride(blockColorDispatcher, Block.leavesPine, pineColor);
			addBlockColorOverride(blockColorDispatcher, Block.leavesEucalyptus, eucalyptusColor);
			addBlockColorOverride(blockColorDispatcher, Block.leavesShrub, shrubColor);

			addBlockColorOverride(blockColorDispatcher, Block.layerLeavesOak, foliageColor);
			
			addBlockColorOverride(blockColorDispatcher, Block.mossStone, grassColor);
			addBlockColorOverride(blockColorDispatcher, Block.mossBasalt, grassColor);
			addBlockColorOverride(blockColorDispatcher, Block.mossLimestone, grassColor);
			addBlockColorOverride(blockColorDispatcher, Block.mossGranite, grassColor);
			
			addBlockColorOverride(blockColorDispatcher, Block.fluidWaterStill, waterColor);
			addBlockColorOverride(blockColorDispatcher, Block.fluidWaterFlowing, waterColor);
		}
	}
	
	public static void addBlockColorOverride(BlockColorDispatcher blockColorDispatcher, Block block, ColorHandler override) {
		blockColorDispatcher.addDispatch(block, new CustomBlockColor(blockColorDispatcher.getDispatch(block), override));
	}
	
	private static void loadConfig(Minecraft minecraft) {
		for(int i=0; i < colors.size(); i++) {
			colors.get(i).enabledInConfig = false;
		}
		fadeDuration = 1.0f;
		
		ConfigHelper.readConfigFromCurrentTexturePack(minecraft, configFileName, (key, value) -> parseConfig(key, value));

		for(int i=0; i < colors.size(); i++) {
			colors.get(i).load();
		}
		
		for(int i=0; i < colors.size(); i++) {
			ColorHandler colorHandler = colors.get(i);
			log("Override: " + colorHandler.getPath() + " : " + colorHandler.enable());
		}
		log("Fade Duration: " + fadeDuration);
	}
	
	private static void parseConfig(String key, String value) {
		if(key.equals("overrideGrass")) {
			grassColor.enabledInConfig = Boolean.parseBoolean(value);
		}else if(key.equals("overrideFoliage")) {
			foliageColor.enabledInConfig = Boolean.parseBoolean(value);
		}else if(key.equals("overrideWater")) {
			waterColor.enabledInConfig = Boolean.parseBoolean(value);
		}else if(key.equals("overrideBirch")) {
			birchColor.enabledInConfig = Boolean.parseBoolean(value);
		}else if(key.equals("overrideCherry")) {
			cherryColor.enabledInConfig = Boolean.parseBoolean(value);
		}else if(key.equals("overridePine")) {
			pineColor.enabledInConfig = Boolean.parseBoolean(value);
		}else if(key.equals("overrideEucalyptus")) {
			eucalyptusColor.enabledInConfig = Boolean.parseBoolean(value);
		}else if(key.equals("overrideShrub")) {
			shrubColor.enabledInConfig = Boolean.parseBoolean(value);
		}else if(key.equals("overrideSky")) {
			skyColor.enabledInConfig = Boolean.parseBoolean(value);
		}else if(key.equals("overrideSea")) {
			seaColor.enabledInConfig = Boolean.parseBoolean(value);
		}else if(key.equals("overrideFog")) {
			fogColor.enabledInConfig = Boolean.parseBoolean(value);
		}else if(key.equals("fadeDuration")) {
			fadeDuration = Float.parseFloat(value);
		}else{
			log("Unknown Option: " + key);
		}
	}
	
	public static void update() {
		updateColors(false);
	}
	
	public static void updateColors(boolean force) {
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
		
//		log("Update Colors");
		
		lastTickSeason = currentSeason;
		lastTickSeasonProgress = seasonProgress;
		lastTickWorld = world;
		
		if(currentSeason == null) {
			for(int i=0; i < colors.size(); i++) {
				ColorHandler color = colors.get(i);
				if(color.enable()) {
					setDefaultColors(color);
				}
			}
			return;
		}
		
//		log("Season: " + currentSeason.getTranslatedName() + " Day: " + dayInSeason + " Progress: " + seasonProgress);
		
		Season season1, season2;
		float blend;
		
		float fadeDurationInverse = 1.0f - fadeDuration;
		
		if(seasonProgress < 0.5f) {
			season1 = world.seasonManager.getPreviousSeason();
			season2 = currentSeason;
			blend = seasonProgress + 0.5f; // 0.5 -> 1.0
			if(fadeDuration == 0.0f) {
				blend = 1.0f;
			}else {
				blend = seasonProgress * 2.0f; // 0.0 -> 1.0
				blend /= fadeDuration;
				blend = clamp(blend, 0.0f, 1.0f); // 0.0 -> 1.0
				blend = blend * 0.5f + 0.5f;
			}
		}else {
			season1 = currentSeason;
			season2 = world.seasonManager.getNextSeason();
			blend = seasonProgress - 0.5f; // 0.0 -> 0.5
			if(fadeDuration == 0.0f) {
				blend = 0.0f;
			}else if(fadeDuration != 1.0f) {
				blend = (seasonProgress - 0.5f) * 2.0f; //0.0 -> 1.0
				blend /= fadeDurationInverse;
				blend -= (1.0f / fadeDurationInverse) * fadeDuration;
				blend = clamp(blend, 0.0f, 1.0f); // 0.0 -> 1.0
			}
		}
		
//		log("Season1: " + season1.getName() + " Season2: " + season2.getName() + " Blend: " + blend);
		
		for(int i=0; i < colors.size(); i++) {
			ColorHandler color = colors.get(i);
			if(color.enable()) {
				updateColor(color, season1, season2, blend);
			}
		}
	}
	
	private static void updateColor(ColorHandler colors, Season season1, Season season2, float blend) {
		Colormap colormap1 = getColormapForSeason(colors, season1);
		Colormap colormap2 = getColormapForSeason(colors, season2);
		
		int[] buffer1 = colormap1.buffer;
		int[] buffer2 = colormap2.buffer;
		int[] data = colors.finalColor.buffer;
		
		for(int i=0; i < data.length; i++) {
			data[i] = blendColor(buffer1[i], buffer2[i], blend);
		}
	}
	
	private static void setDefaultColors(ColorHandler colors) {
		if(colors.vanilla == null) {
			throw new NullPointerException("No default colormap: " + colors.path);
		}
		
		int[] buffer1 = colors.vanilla.buffer;
		int[] data = colors.finalColor.buffer;
		
		for(int i=0; i < data.length; i++) {
			data[i] = buffer1[i];
		}
	}
	
	public static int blendColor(int color1, int color2, float blend) {
		float blend1 = 1.0f - blend;
		
		int r1 = (color1 >> 16) & 0xFF;
		int g1 = (color1 >>  8) & 0xFF;
		int b1 = (color1 >>  0) & 0xFF;
		
		int r2 = (color2 >> 16) & 0xFF;
		int g2 = (color2 >>  8) & 0xFF;
		int b2 = (color2 >>  0) & 0xFF;
		
		int r = (int)(r1 * blend1 + r2 * blend);
		int g = (int)(g1 * blend1 + g2 * blend);
		int b = (int)(b1 * blend1 + b2 * blend);
		
		r = clamp(r, 0, 255);
		g = clamp(g, 0, 255);
		b = clamp(b, 0, 255);
		
		return r << 16 | g << 8 | b; 
	}
	
	public static int clamp(int val, int min, int max) {
		if(val < min) return min;
		if(val > max) return max;
		return val;
	}
	
	public static float clamp(float val, float min, float max) {
		if(val < min) return min;
		if(val > max) return max;
		return val;
	}
	
	private static Colormap getColormapForSeason(ColorHandler colormaps, Season season) {
		if(season == Seasons.OVERWORLD_SPRING) {
			return colormaps.spring != null ? colormaps.spring : colormaps.vanilla;
		} else if(season == Seasons.OVERWORLD_SUMMER) {
			return colormaps.summer != null ? colormaps.summer : colormaps.vanilla;
		} else if(season == Seasons.OVERWORLD_FALL) {
			return colormaps.autumn != null ? colormaps.autumn : colormaps.vanilla;
		} else if(season == Seasons.OVERWORLD_WINTER) {
			return colormaps.winter != null ? colormaps.winter : colormaps.vanilla;
		} else {
			return colormaps.vanilla;
		}
	}
	
	private static Colormap loadColormap(String path) {
		InputStream stream = null;
		
		try {
			stream = TexturePackUtils.selectedTexturePack.getResourceAsStream(path);
		}catch (Exception e) {
			e.printStackTrace();
			try {
				stream.close();
			}catch (Exception e1) {}
			return null;
		}
		
		if(stream == null) {
			log("Colormap does not exist: " + path);
			return null;
		}
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(stream);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			try {
				stream.close();
			}catch (Exception e) {}
		}
		
		if(image.getWidth() != 256 || image.getHeight() != 256) {
			log("Error loading colormap: " + path);
			log("Image is invalid size, got " + image.getWidth() + " x " + image.getHeight() + ", expected 256x256!");
			return null;
		}
		
		Colormap colormap = new Colormap();
		
		int[] buffer = colormap.buffer;
		
		for(int i=0; i < buffer.length; i++) {
			int color = image.getRGB(i & 0xFF, i >> 8);
			
			buffer[i] = color;
		}
		log("Loaded colormap: " + path);
		return colormap;
	}
	
	public static class ColorHandler {
		
		private String path;
		
		private boolean hasColormaps;
		private boolean enabledInConfig;
		
		private Colormap vanilla;
		
		private Colormap spring;
		private Colormap summer;
		private Colormap autumn;
		private Colormap winter;
		
		private Colormap finalColor;
		
		public ColorHandler(String path) {
			this.path = path;
		}
		
		public void load() {
			if(!enabledInConfig) {
				vanilla = null;
				spring = null;
				summer = null;
				autumn = null;
				winter = null;
				finalColor = null;
				return;
			}
			
			vanilla = loadColormap("/misc/default" + path);
			if(vanilla == null) {
				vanilla = loadColormap("/misc" + path);
			}
			spring = loadColormap("/misc/spring" + path);
			summer = loadColormap("/misc/summer" + path);
			autumn = loadColormap("/misc/autumn" + path);
			winter = loadColormap("/misc/winter" + path);
			
			if(spring == null) spring = vanilla;
			if(summer == null) summer = vanilla;
			if(autumn == null) autumn = vanilla;
			if(winter == null) winter = vanilla;
			
			hasColormaps = spring != null && summer != null && autumn != null && winter != null && vanilla != null;
			
			if(enable()) {
				finalColor = new Colormap();
			}
		}
		
		public boolean enable() {
			return hasColormaps && enabledInConfig;
		}
		
		public Colormap getColor() {
			return finalColor;
		}
		
		public String getPath() {
			return path;
		}
		
	}
	
//	public static float modify(float progress, float fadeDuration) {
//		if(fadeDuration == 0.0f) {
//			return progress > 0.5f ? 1.0f : 0.0f;
//		}
//		
//		progress -= 0.5f;
//		progress /= fadeDuration;
//		progress += 0.5f;
//		
//		return clamp(progress, 0.0f, 1.0f);
//	}
//	
//	@SuppressWarnings("serial")
//	public static void main(String[] args) {
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		final float fadeDuration = Float.parseFloat(JOptionPane.showInputDialog("Enter Fade Duration"));
//		
//		int size = 256;
//		int pad = 16;
//		
//		JFrame frame = new JFrame();
//		JPanel panel = new JPanel() {
//			@Override
//			public void paint(Graphics g) {
//				super.paint(g);
//				
//				int w = getWidth() - pad * 2;
//				int h = getHeight() - pad * 2;
//				
//				for(int i=0; i < w; i++) {
//					float x = i / (float) w;
//					float y = modify(x, fadeDuration);
//					
//					int j = (int) (h * y);
//					
//					g.setColor(Color.black);
//					g.fillRect(i + pad, h - j - 1 + pad, 1, 1);
//				}
//			}
//		};
//
//		log("0.0: " + modify(0.0f, fadeDuration));
//		log("0.25: " + modify(0.25f, fadeDuration));
//		log("0.5: " + modify(0.5f, fadeDuration));
//		log("0.75: " + modify(0.75f, fadeDuration));
//		log("1.0: " + modify(1.0f, fadeDuration));
//		
//		panel.setPreferredSize(new Dimension(size + pad * 2, size + pad * 2));
//		
//		frame.add(panel);
//		frame.pack();
//		frame.setLocationRelativeTo(null);
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setVisible(true);
//	}
	
	public static void log(String str) {
		System.out.print("[SeasonalColormapsMod] " + str + "\n");
	}
	
}
