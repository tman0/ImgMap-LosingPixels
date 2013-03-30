package com.tenko;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import com.tenko.cmdexe.CommanderCirno;
import com.tenko.rendering.ImageRenderer;
import com.tenko.rendering.SlideshowRenderer;
import com.tenko.threading.MapThreadGroup;
import com.tenko.threading.SlideshowThread;
import com.tenko.utils.DataUtils;

/**
 * ImgMap - Maps become picture frames!
 * @author Tsunko
 * @version 2 Alpha
 */
public class ImgMap extends JavaPlugin {

	/**
	 * Static plugin. Unsafe, probably.
	 */
	static ImgMap pl;

	/**
	 * Command handler for all the commands.
	 */
	private CommanderCirno cc = new CommanderCirno();

	/**
	 * Slideshow threading group.
	 */
	private final static MapThreadGroup group = new MapThreadGroup();

	/**
	 * Ignore this. I use this to test the utils and functions.
	 */
	public static void main(String[] args){

	}

	/**
	 * Let's start it!
	 * Get chance and luck!
	 */
	@Override
	public void onEnable(){
		pl = this;

		//Setting executors
		getCommand("map").setExecutor(cc);
		getCommand("smap").setExecutor(cc);

		getCommand("imap").setExecutor(cc);
		getCommand("imgmap").setExecutor(cc);

		getCommand("restoremap").setExecutor(cc);
		getCommand("rmap").setExecutor(cc);

		//Usage
		getCommand("map").setUsage(ChatColor.BLUE + "Usage: /map <url>");
		getCommand("smap").setUsage(ChatColor.BLUE + "Usage: /smap <time> <url1> [url2] [url3] and so on.");

		getCommand("imap").setUsage(ChatColor.BLUE + "Usage: /imap");
		getCommand("imgmap").setUsage(ChatColor.BLUE + "Usage: /imgmap");

		getCommand("restoremap").setUsage(ChatColor.BLUE + "Usage: /restoremap");
		getCommand("rmap").setUsage(ChatColor.BLUE + "Usage: /rmap");

		//Rewriting all of the IO map data loading. It was crappy as hell.
		try {
			DataUtils.checkDataFolder();
			DataUtils.checkFolder("SlideshowData");
			DataUtils.checkFile("Maps.list");

			for(String s : DataUtils.getLines(getList())){
				String url = s.substring(s.indexOf(":")+1, s.length());
				short id = Short.valueOf(s.substring(0, s.indexOf(":")));

				MapView viewport = Bukkit.getServer().getMap(id);

				for(MapRenderer mr : viewport.getRenderers()){
					viewport.removeRenderer(mr);
				}

				viewport.addRenderer(new ImageRenderer(url));
			}

			// This is rather suicidel.
			new Thread(){
				@Override
				public void run(){
					try {
						for(File f : new File(ImgMap.getPlugin().getDataFolder().getAbsolutePath() + "/SlideshowData/").listFiles()){
							short id = Short.valueOf(f.getName().substring(0, f.getName().indexOf(".")));
							MapView viewport = Bukkit.getServer().getMap(id);

							for(MapRenderer mr : viewport.getRenderers()){
								viewport.removeRenderer(mr);
							}
							
							List<String> lines = DataUtils.getLines(f);
							float waitTime = Float.valueOf(lines.remove(0));
							String[] urls = new String[lines.size()];
							lines.toArray(urls);
							
							viewport.addRenderer(new SlideshowRenderer(urls, waitTime));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void onDisable(){
		SlideshowThread[] activeThreads = new SlideshowThread[group.activeCount()];
		group.enumerate(activeThreads);
		for(SlideshowThread t : activeThreads){
			t.stopThread();
		}
	}

	/**
	 * Returns the plugin defined by "pl".
	 * @return The plugin.
	 */
	public static ImgMap getPlugin(){
		return pl;
	}

	/**
	 * Gets the file and returns a File object.
	 * @return The Maps.list file.
	 */
	public static File getList(){
		return new File(ImgMap.getPlugin().getDataFolder(), "Maps.list");
	}

	public static File getSlideshowFile(int id){
		return new File(ImgMap.getPlugin().getDataFolder().getAbsolutePath() + "/SlideshowData/", String.valueOf(id + ".slideshow"));
	}

	public static MapThreadGroup getThreadGroup(){
		return group;
	}
}