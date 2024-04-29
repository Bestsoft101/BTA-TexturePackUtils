package b100.tputils.customatlas;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Atlas {
	
	public final List<AtlasTile> tileList = new ArrayList<>();
	
	public int width;
	public int height;
	
	public Atlas() {
		
	}
	
	public int size() {
		return tileList.size();
	}
	
	public void setSizeAuto() {
		width = 1;
		height = 1;
		
		for(int i=0; i < tileList.size(); i++) {
			AtlasTile tile = tileList.get(i);
			
			width = Math.max(width, tile.x + 1);
			height = Math.max(height, tile.y + 1);
		}
	}
	
	@Override
	public String toString() {
		return "Atlas[" + tileList.size() + " tiles, " + width + "x" + height + "]";
	}
	
	public static Atlas read(InputStream in) {
		Atlas atlas = new Atlas();
		
		Map<Integer, AtlasTile> map = new HashMap<>();
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			
			while(true) {
				String line = br.readLine();
				if(line == null) {
					break;
				}
				
				line = line.trim();
				if(line.length() == 0 || line.startsWith("#")) {
					continue;
				}
				
				try {
					int seperatorIndex = line.indexOf('=');
					if(seperatorIndex < 0) {
						throw new RuntimeException("No separator! Expected '='!");
					}
					
					String key = line.substring(0, seperatorIndex);
					String value = line.substring(seperatorIndex + 1);
					
					if(key.equals("size")) {
						String[] size = value.split("x");
						if(size.length != 2) {
							throw new RuntimeException("Invalid size '" + size + "'! Expected <width>x<height>!");
						}
						
						atlas.width = Integer.parseInt(size[0]);
						atlas.height = Integer.parseInt(size[1]);
					}else {
						String[] pos = line.substring(0, seperatorIndex).split(",");
						if(pos.length != 2) {
							throw new RuntimeException("Invalid position '" + key + "'! Expected <x>,<y>!");
						}
						
						int x = Integer.parseInt(pos[0]);
						int y = Integer.parseInt(pos[1]);
						
						map.put(AtlasTile.getIndex(x, y), new AtlasTile(value, x, y));
					}
				}catch (Exception e) {
					throw new RuntimeException("Reading line '" + line + "'", e);
				}
			}
		}catch (Exception e) {
			throw new RuntimeException("Loading atlas tiles", e);
		}finally {
			try {
				br.close();
			}catch (Exception e) {}
			try {
				in.close();
			}catch (Exception e) {}
		}
		
		atlas.tileList.addAll(map.values());
		atlas.tileList.sort(AtlasTile.sorter);
		
		if(atlas.width <= 0 || atlas.height <= 0) {
			atlas.setSizeAuto();
		}
		
		
		return atlas;
	}

}
