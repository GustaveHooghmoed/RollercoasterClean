package me.legofreak107.rollercoaster.helpers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.legofreak107.rollercoaster.Main;

public class Config {

	public static FileConfiguration getConfig() {
		return JavaPlugin.getPlugin(Main.class).getConfig();
	}

	public static void saveConfig() {
		JavaPlugin.getPlugin(Main.class).saveConfig();
	}

}
