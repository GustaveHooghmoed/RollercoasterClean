package me.legofreak107.rollercoaster.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.legofreak107.rollercoaster.Main;
import me.legofreak107.rollercoaster.helpers.DataHolder;
import me.legofreak107.rollercoaster.objects.Train;

public class PlayerJoin implements Listener {

	private Main plugin;

	public PlayerJoin(Main pl) {
		plugin = pl;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (!Main.getData().trainsSpawned) {
			if (Main.getSAL().getCustomSaveConfig().contains("Saved")) {
				for (String s : Main.getSAL().getCustomSaveConfig().getConfigurationSection("Saved").getKeys(false)) {
					Integer loopSeconds = Main.getSAL().getCustomSaveConfig().getInt("Saved." + s + ".loopSeconds");
					Integer cartOffset = Main.getSAL().getCustomSaveConfig().getInt("Saved." + s + ".cartOffset");
					Integer minSpeed = Main.getSAL().getCustomSaveConfig().getInt("Saved." + s + ".minSpeed");
					Integer maxSpeed = Main.getSAL().getCustomSaveConfig().getInt("Saved." + s + ".maxSpeed");
					Integer trainLength = Main.getSAL().getCustomSaveConfig().getInt("Saved." + s + ".trainLength");
					Boolean hasLoco = Main.getSAL().getCustomSaveConfig().getBoolean("Saved." + s + ".hasLoco");
					Boolean isSmall = Main.getSAL().getCustomSaveConfig().getBoolean("Saved." + s + ".isSmall");
					String trainName = Main.getSAL().getCustomSaveConfig().getString("Saved." + s + ".trainName");
					Integer cartDownPos = Main.getSAL().getCustomSaveConfig().getInt("Saved." + s + ".cartDownPos");
					Train t = Main.getAPI().spawnTrain(trainName, trainLength, hasLoco,
							Main.getAPI().getTrack(s).origin, isSmall, Main.getAPI().getTrack(s), minSpeed, maxSpeed,
							cartOffset, cartDownPos);
					Main.getData();
					DataHolder.loop.put(t, loopSeconds);
					Main.getAPI().startTrain(s);
					Main.getSAL().getCustomSaveConfig().set("Saved." + s, null);
				}
				Main.getSAL().getCustomSaveConfig().set("Saved", null);
				Main.getData().trainsSpawned = true;
			}
		}
	}

}
