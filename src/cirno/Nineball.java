package cirno;

import java.io.IOException;
import org.bukkit.plugin.java.JavaPlugin;


public class Nineball extends JavaPlugin {

	/*
	 * Good shall prevail. Evil sucks.
	 */
	
	protected DataSaver ds;
	protected CommanderCirno cc;
	protected CirnoThreadGroup tg = new CirnoThreadGroup("Cirno Group");

	public void onEnable(){
		try {
			saveDefaultConfig();
			ds = new DataSaver(this);
			ds.initializeFile();
			ds.setGlobalMaps();
			cc = new CommanderCirno(this);
			getCommand("map").setExecutor(cc);
			getCommand("restoremap").setExecutor(cc);
			getCommand("imgmap").setExecutor(cc);
			getCommand("imap").setExecutor(cc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onDisable(){
		saveConfig();
		tg.stopRunning();
	}
}
