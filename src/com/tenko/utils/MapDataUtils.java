package com.tenko.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import com.google.common.io.Files;
import com.tenko.ImgMap;
import com.tenko.rendering.ImageRenderer;
import com.tenko.test.MapListener;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

public class MapDataUtils {
	
	private static File mapList;

    private static TreeMap<Integer, String> maps;
	
	public static void init(){
		ImgMap.getInstance().getDataFolder().mkdir();
		
		mapList = new File(ImgMap.getInstance().getDataFolder(), "Maps.list");
		try {
			mapList.createNewFile();	
		} catch (IOException e){
			System.out.println("Failed to create Maps.list!");
			e.printStackTrace();
		}
		
		new File(ImgMap.getInstance().getDataFolder(), "SlideshowData").mkdir();
		new File(ImgMap.getInstance().getDataFolder(), "images").mkdir();

        maps = parseMapData(mapList);
        renderMaps(maps);

	}

	public static File getSlideshowFile(int id){
		return new File(ImgMap.getInstance().getDataFolder().getAbsolutePath() + "/SlideshowData/", String.valueOf(id + ".slideshow"));
	}
	
	public static File getList(){
		return mapList;
	}
	
	public static boolean add(int src, String dest){
		try {
			List<String> contents = Files.readLines(mapList, Charset.defaultCharset());
			BufferedWriter bw = new BufferedWriter(new FileWriter(mapList, false));
			
			boolean isAlreadyAdded = false;
			
			for(String s : contents){
				bw.append((isAlreadyAdded = s.startsWith(src+":")) ? src + ":" + dest : s);
				bw.newLine();
			}
			
			if(!isAlreadyAdded){
				bw.append(src + ":" + dest);
			}
			
			bw.flush();
			bw.close();
			
			MapListener.updateList();
			return true;
		} catch (IOException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean addArray(File f, Iterable<String> dest){
		try {
			BufferedWriter bw = Files.newWriter(f, Charset.defaultCharset());
			
			for(String l : dest){
				bw.append(l);
				bw.newLine();
			}

			bw.flush();
			bw.close();

			MapListener.updateList();
			return true;
		} catch (IOException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean deleteMapData(short target){
		try {
			List<String> contents = Files.readLines(mapList, Charset.defaultCharset());
			BufferedWriter bw = Files.newWriter(mapList, Charset.defaultCharset());
			
			for(String l : contents){
				if(!l.startsWith(String.valueOf(target)+":")){
					bw.append(l);
					bw.newLine();
				}
			}
			
			bw.flush();
			bw.close();
			
			MapListener.updateList();
			return true;
		} catch (IOException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean attemptDeleteSlideshow(int id){
		return getSlideshowFile(id).delete();
	}

    private static TreeMap<Integer, String> parseMapData(File mapList)
    {
        String buffer;
        Scanner reader;
        TreeMap<Integer, String> theMaps = new TreeMap<>();
        try
        {
            reader = new Scanner(mapList);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            return null;
        }
        for(int line = 1; reader.hasNextLine(); line++)
        {
            buffer = reader.nextLine();
            String[] thismap = buffer.split(":", 2);
            try
            {
                theMaps.put(Integer.parseInt(thismap[0]), thismap[1]);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                ImgMap.getInstance().getLogger().warning("Malformed configuration on line " + line + " - check maps.list!");
                ex.printStackTrace();
            }
        }
        return theMaps;
    }

    private static void renderMaps(Map<Integer, String> maps)
    {
        for(Map.Entry<Integer, String> map : maps.entrySet())
        {
            try
            {
                if(ImageUtils.isImageCompatible(map.getValue()))
                    Bukkit.getMap(map.getKey().shortValue()).addRenderer(new ImageRenderer(map.getValue()));
                else
                    ImgMap.getInstance().getLogger().warning("Incompatible image " + map.getValue() + " (map ID " + map.getKey() + ")");
            }
            catch(IOException ex)
            {
                ImgMap.getInstance().getLogger().warning("Error rendering map ID " + map.getKey());
            }
        }
    }
}
