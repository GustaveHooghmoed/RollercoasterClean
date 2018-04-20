package me.legofreak107.rollercoaster;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.legofreak107.rollercoaster.api.API;
import me.legofreak107.rollercoaster.events.ChunkUnload;
import me.legofreak107.rollercoaster.events.EntityDismount;
import me.legofreak107.rollercoaster.events.PlayerInteractAtEntity;
import me.legofreak107.rollercoaster.events.PlayerJoin;
import me.legofreak107.rollercoaster.helpers.Base;
import me.legofreak107.rollercoaster.helpers.DataHolder;
import me.legofreak107.rollercoaster.libs.LangFile;

public class Main extends JavaPlugin implements Listener {
	// TODO: Train y double offset
	// TODO: Loop fix

	// Save and load file (Used for config management)

	public static SaveAndLoad sal = new SaveAndLoad();

	// Dataholders

	public static DataHolder data = new DataHolder();

	// Load listeners.. //TODO: Fix listenerhelper
	ChunkUnload CU = new ChunkUnload(this);
	EntityDismount ED = new EntityDismount(this);
	PlayerInteractAtEntity PIAE = new PlayerInteractAtEntity(this);
	PlayerJoin PJ = new PlayerJoin(this);

	// Start of the plugin
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(CU, this);
		Bukkit.getPluginManager().registerEvents(ED, this);
		Bukkit.getPluginManager().registerEvents(PIAE, this);
		Bukkit.getPluginManager().registerEvents(PJ, this);

		getCommand("rc").setExecutor(new RCCommand());

		sal.plugin = this;
		LangFile lm = new LangFile();
		DataHolder.langFile = "LANG_EN";
		lm.generateLanguageFile(this);
		getAPI().plugin = this;
		if (getConfig().contains("Settings.languageFile")) {
			DataHolder.langFile = (String) getConfig().get("Settings.languageFile");
		} else {
			getConfig().set("Settings.languageFile", "LANG_EN");
			saveConfig();
			DataHolder.langFile = "LANG_EN";
			lm.generateLanguageFile(this);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				Base.runStartup();
			}
		}, 5L);
	}

	// Disable plugin
	@Override
	public void onDisable() {
		Base.disable();
	}

	Location lastLoc;
	Integer Offset = 0;
	float previous = 0;
	Integer Kantel = 0;

	// API SECTION

	public static API getAPI() {
		API api = new API();
		return api;
	}

	public static DataHolder getData() {
		return data;
	}

	public static SaveAndLoad getSAL() {
		return sal;
	}
}
