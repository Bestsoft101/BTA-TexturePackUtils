package b100.tputils.seasonalcolormaps;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import b100.tputils.ConfigHelper;
import b100.tputils.TexturePackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import net.minecraft.core.world.season.Season;
import net.minecraft.core.world.season.Seasons;
import net.minecraft.core.world.type.WorldTypes;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.WeatherManager;

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
	public static final ColorHandler cacaoColor = new ColorHandler("/cacaocolor.png");
	public static final ColorHandler skyColor = new ColorHandler("/skycolor.png");
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
		colors.add(cacaoColor);
		colors.add(skyColor);
		colors.add(fogColor);
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
			addBlockColorOverride(blockColorDispatcher, Block.leavesCacao, cacaoColor);

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
		}else if(key.equals("overrideCacao")) {
			cacaoColor.enabledInConfig = Boolean.parseBoolean(value);
		}else if(key.equals("overrideSky")) {
			skyColor.enabledInConfig = Boolean.parseBoolean(value);
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
		Season currentSeason = world.getSeasonManager().getCurrentSeason();
		int dayInSeason = world.getSeasonManager().getDayInSeason();
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
		
		if(world.worldType == WorldTypes.OVERWORLD_WINTER) {
			for(int i=0; i < colors.size(); i++) {
				ColorHandler color = colors.get(i);
				if(color.enable()) {
					for(int j=0; j < color.finalColor.buffer.length; j++) {
						color.finalColor.buffer[j] = color.winter.buffer[j];
					}
				}
			}
			return;
		}
		
//		log("Season: " + currentSeason.getTranslatedName() + " Day: " + dayInSeason + " Progress: " + seasonProgress);
		
		Season season1, season2;
		float blend;
		
		float fadeDurationInverse = 1.0f - fadeDuration;
		
		if(seasonProgress < 0.5f) {
			season1 = world.getSeasonManager().getPreviousSeason();
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
			season2 = world.getSeasonManager().getNextSeason();
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
	
	public static Vec3d getSkyColor(World world, ICamera camera, float partialTicks) {
		if(skyColor.enable()) {
			float celestialAngle = world.getCelestialAngle(partialTicks);
			float f2 = MathHelper.clamp(MathHelper.cos(celestialAngle * 3.141593f * 2.0f) * 2.0f + 0.5f, 0.0f, 1.0f);
			
			int x = MathHelper.floor_double(mc.activeCamera.getPosition().xCoord);
			int z = MathHelper.floor_double(mc.activeCamera.getPosition().zCoord);
			
			int color = skyColor.getColor().getFromWorldPos(world, x, z);
			
			float r = ((color >> 16) & 0xFF) / 255.0f;
			float g = ((color >>  8) & 0xFF) / 255.0f;
			float b = ((color >>  0) & 0xFF) / 255.0f;
			
			r *= f2;
			g *= f2;
			b *= f2;
			
			Weather weather = world.getCurrentWeather();
			WeatherManager weatherManager = world.weatherManager;
			
			float weatherStrength = weather != null && weather.isPrecipitation ? weatherManager.getWeatherPower() * weatherManager.getWeatherIntensity() : 0.0f;
			if(weatherStrength > 0.0f) {
				float f12 = (r * 0.3f + g * 0.59f + b * 0.11f) * 0.6f;
				float f10 = weatherStrength * 0.75f;
				
				r = MathHelper.lerp(r, f12, f10);
				g = MathHelper.lerp(g, f12, f10);
				b = MathHelper.lerp(b, f12, f10);
			}
			
			if(world.lightningFlicker > 0) {
				float f12 = world.lightningFlicker - partialTicks;
				if(f12 > 1.0f) f12 = 1.0f;
				f12 *= 0.45f;
				r = MathHelper.lerp(r, 0.8f, f12);
				g = MathHelper.lerp(g, 0.8f, f12);
				b = MathHelper.lerp(b, 1.0f, f12);
			}
			
			return Vec3d.createVector(r, g, b);
		}
		return world.getSkyColor(camera, partialTicks);
	}
	
	public static Vec3d getFogColor(World world, float partialTicks) {
		double x = mc.activeCamera.getX(partialTicks);
		double z = mc.activeCamera.getZ(partialTicks);
		
		if(fogColor.enable()) {
			float celestialAngle = world.getCelestialAngle(partialTicks);
			float f2 = MathHelper.clamp(MathHelper.cos(celestialAngle * 3.141593f * 2.0f) * 2.0f + 0.5f, 0.0f, 1.0f);
			
			int blockX = MathHelper.floor_double(x);
			int blockZ = MathHelper.floor_double(z);
			
			int color = fogColor.getColor().getFromWorldPos(world, blockX, blockZ);

			float r = ((color >> 16) & 0xFF) / 255.0f;
			float g = ((color >>  8) & 0xFF) / 255.0f;
			float b = ((color >>  0) & 0xFF) / 255.0f;

	        r *= f2 * 0.94F + 0.06F;
	        g *= f2 * 0.94F + 0.06F;
	        b *= f2 * 0.91F + 0.09F;
			
			return Vec3d.createVector(r, g, b);
		}
		return world.getFogColor(mc.activeCamera, partialTicks);
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
		} else if(season == Seasons.PARADISE_SILVER) {
			return colormaps.silver != null ? colormaps.silver : colormaps.vanilla;
		} else if(season == Seasons.PARADISE_GOLD) {
			return colormaps.gold != null ? colormaps.gold : colormaps.vanilla;
		} else {
			return colormaps.vanilla;
		}
	}
	
	private static Colormap loadColormap(String path) {
		BufferedImage image = TexturePackUtils.readTexture(path);
		
		if(image == null) {
			return null;
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
		private Colormap silver;
		private Colormap gold;
		
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
				silver = null;
				gold = null;
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
			silver = loadColormap("/misc/silver" + path);
			gold = loadColormap("/misc/gold" + path);
			
			if(spring == null) spring = vanilla;
			if(summer == null) summer = vanilla;
			if(autumn == null) autumn = vanilla;
			if(winter == null) winter = vanilla;
			if(silver == null) silver = vanilla;
			if(gold == null) gold = vanilla;
			
			hasColormaps = vanilla != null && spring != null && summer != null && autumn != null && winter != null && silver != null && gold != null;
			
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
