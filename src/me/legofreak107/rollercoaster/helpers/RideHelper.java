package me.legofreak107.rollercoaster.helpers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.legofreak107.rollercoaster.Main;
import me.legofreak107.rollercoaster.api.TrainLockEvent;
import me.legofreak107.rollercoaster.api.TrainStartEvent;
import me.legofreak107.rollercoaster.objects.Cart;
import me.legofreak107.rollercoaster.objects.Seat;
import me.legofreak107.rollercoaster.objects.Train;

public class RideHelper {

	// Wait x amount of seconds. After that resume the ride
	public static void wait(int waitTime, int oldSpeed, Train t) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Main.class), new Runnable() {
			@Override
			public void run() {
				t.speed = oldSpeed;
				t.riding = true;
				int count = 0;
				if (Config.getConfig().contains("Ridecount." + t.track.name + ".count")) {
					Config.getConfig().set("Ridecount." + t.track.name + ".count",
							Config.getConfig().getInt("Ridecount." + t.track.name + ".count") + count);
				} else {
					Config.getConfig().set("Ridecount." + t.track.name + ".count", count);
				}
				Config.saveConfig();
			}
		}, 20 * waitTime);
	}

	// Delay for the loop to restart
	public static void startLoop(Train t) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Main.class), new Runnable() {
			@Override
			public void run() {
				Base.setActive(t.track.name, false);
				for (int i = 0; i < t.carts.size(); i++) {
					Cart c = t.carts.get(i);
					c.pos = i * t.cartOffset;
				}
				t.riding = true;
				t.locked = true;
				TrainStartEvent event1 = new TrainStartEvent("TrainStartEvent", t);
				Bukkit.getServer().getPluginManager().callEvent(event1);
				TrainLockEvent event = new TrainLockEvent("TrainLockEvent", t);
				Bukkit.getServer().getPluginManager().callEvent(event);
				for (Cart c2 : t.carts) {
					for (Seat s : c2.seats) {
						s.locked = true;
					}
				}
			}
		}, 20L * DataHolder.loop.get(t));
	}
}
