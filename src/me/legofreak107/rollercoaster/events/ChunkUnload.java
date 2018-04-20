package me.legofreak107.rollercoaster.events;

import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import me.legofreak107.rollercoaster.Main;
import me.legofreak107.rollercoaster.helpers.DataHolder;

public class ChunkUnload implements Listener {

	private Main plugin;

	public ChunkUnload(Main pl) {
		plugin = pl;
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		if (e.getWorld().getEnvironment() == Environment.NORMAL) {
			if (DataHolder.chunks.contains(e.getChunk())) {
				e.setCancelled(true);
			}
		}
	}

}
