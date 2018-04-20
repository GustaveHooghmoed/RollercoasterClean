package me.legofreak107.rollercoaster.helpers;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

import me.legofreak107.rollercoaster.Main;
import me.legofreak107.rollercoaster.MoveCoaster;
import me.legofreak107.rollercoaster.SaveAndLoad;
import me.legofreak107.rollercoaster.objects.Cart;
import me.legofreak107.rollercoaster.objects.Receiver;
import me.legofreak107.rollercoaster.objects.Seat;
import me.legofreak107.rollercoaster.objects.Track;
import me.legofreak107.rollercoaster.objects.Train;

public class Base {

	public static DataHolder data = Main.getData();

	// Startup (Start the timer to run the trains)
	public static void runStartup() {
		MoveCoaster mc = new MoveCoaster();
		mc.plugin = JavaPlugin.getPlugin(Main.class);
		if (mc.plugin.getConfig().contains("Tracks")) {
			for (String track : mc.plugin.getConfig().getConfigurationSection("Tracks").getKeys(false)) {
				Track t = Main.getAPI().getTrack(track);
				Main.getData().tracks.add(t);
			}
		}
		Bukkit.getScheduler().scheduleSyncRepeatingTask(mc.plugin, new Runnable() {
			@Override
			public void run() {
				mc.updateMovement();
			}
		}, 0L, 1L);
		Bukkit.getScheduler().scheduleSyncDelayedTask(mc.plugin, new Runnable() {
			@Override
			public void run() {
				if (Bukkit.getOnlinePlayers().size() > 0) {
					if (Main.getSAL().getCustomSaveConfig().contains("Saved")) {
						for (String s : Main.getSAL().getCustomSaveConfig().getConfigurationSection("Saved")
								.getKeys(false)) {
							Integer loopSeconds = Main.getSAL().getCustomSaveConfig()
									.getInt("Saved." + s + ".loopSeconds");
							Integer cartOffset = Main.getSAL().getCustomSaveConfig()
									.getInt("Saved." + s + ".cartOffset");
							Integer minSpeed = Main.getSAL().getCustomSaveConfig().getInt("Saved." + s + ".minSpeed");
							Integer maxSpeed = Main.getSAL().getCustomSaveConfig().getInt("Saved." + s + ".maxSpeed");
							Integer trainLength = Main.getSAL().getCustomSaveConfig()
									.getInt("Saved." + s + ".trainLength");
							Integer cartDownPos = Main.getSAL().getCustomSaveConfig()
									.getInt("Saved." + s + ".cartDownPos");
							Boolean hasLoco = Main.getSAL().getCustomSaveConfig().getBoolean("Saved." + s + ".hasLoco");
							Boolean isSmall = Main.getSAL().getCustomSaveConfig().getBoolean("Saved." + s + ".isSmall");
							String trainName = Main.getSAL().getCustomSaveConfig()
									.getString("Saved." + s + ".trainName");
							Train t = Main.getAPI().spawnTrain(trainName, trainLength, hasLoco,
									Main.getAPI().getTrack(s).origin, isSmall, Main.getAPI().getTrack(s), minSpeed,
									maxSpeed, cartOffset, cartDownPos);
							DataHolder.loop.put(t, loopSeconds);
							Main.getAPI().startTrain(s);
							Main.getSAL().getCustomSaveConfig().set("Saved." + s, null);
						}
						Main.getSAL().getCustomSaveConfig().set("Saved", null);
						data.trainsSpawned = true;
					}
				}
			}
		}, 20L);
	}

	// Enable/Disable receiver signs
	public static void setActive(String name, Boolean active) {
		if (active) {
			for (World w : Bukkit.getWorlds()) {
				for (Chunk c : w.getLoadedChunks()) {
					for (BlockState b : c.getTileEntities()) {
						if (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) {
							Sign s = (Sign) b;
							if (s.getLine(0).equalsIgnoreCase("[rc]")) {
								if (s.getLine(1).equalsIgnoreCase("receiver")) {
									if (s.getLine(2).equalsIgnoreCase(name)) {
										Receiver r = new Receiver();
										r.active = active;
										r.name = name;
										r.loc = b.getLocation();
										r.id = data.id + 1;
										data.receivers.put(data.id + 1, r);
										data.id++;
										b.getLocation().getBlock().setType(Material.REDSTONE_TORCH_ON);
									}
								}
							}
						}
					}
				}
			}
		} else {
			for (int i = 1; i < data.id + 1; i++) {
				Receiver r = data.receivers.get(i);
				if (r.name.equalsIgnoreCase(name)) {
					r.loc.getBlock().setType(Material.SIGN_POST);
					Sign s = (Sign) r.loc.getBlock().getState();
					s.setLine(0, "[RC]");
					s.setLine(1, "receiver");
					s.setLine(2, name);
					s.update();
					r.active = active;
				}
			}
		}
	}

	public static void disable() {
		SaveAndLoad sal = Main.getSAL();
		Iterator<?> it = DataHolder.loop.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Train t = (Train) pair.getKey();
			Integer time = (Integer) pair.getValue();
			String name = t.track.name;
			sal.getCustomSaveConfig().set("Saved." + name + ".loopSeconds", time);//
			sal.getCustomSaveConfig().set("Saved." + name + ".cartOffset", t.cartOffset);//
			sal.getCustomSaveConfig().set("Saved." + name + ".minSpeed", t.minSpeed);//
			sal.getCustomSaveConfig().set("Saved." + name + ".maxSpeed", t.maxSpeed);//
			sal.getCustomSaveConfig().set("Saved." + name + ".trainLength", t.carts.size());//
			sal.getCustomSaveConfig().set("Saved." + name + ".hasLoco", t.hasLoco);
			sal.getCustomSaveConfig().set("Saved." + name + ".trainName", t.trainName);
			sal.getCustomSaveConfig().set("Saved." + name + ".cartDownPos", t.cartDownPos);//
			sal.getCustomSaveConfig().set("Saved." + name + ".isSmall", t.carts.get(0).holder.isSmall());//
			sal.saveCustomSaveConfig();
			it.remove(); // avoids a ConcurrentModificationException
		}
		for (ArmorStand ar : data.pointsVisible) {
			ar.remove();
		}
		for (int i = 1; i < data.id + 1; i++) {
			Receiver r = data.receivers.get(i);
			setActive(r.name, false);
		}
		for (Train t : data.trains) {
			for (Cart c : t.carts) {
				c.holder.remove();
				for (Seat s : c.seats) {
					s.holder.remove();
				}
			}
		}
		data.trains.clear();
	}

}
