package me.legofreak107.rollercoaster.api;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.legofreak107.rollercoaster.Main;
import me.legofreak107.rollercoaster.helpers.Base;
import me.legofreak107.rollercoaster.helpers.Config;
import me.legofreak107.rollercoaster.helpers.DataHolder;
import me.legofreak107.rollercoaster.libs.CustomPath;
import me.legofreak107.rollercoaster.libs.CustomPathBuilder;
import me.legofreak107.rollercoaster.objects.Cart;
import me.legofreak107.rollercoaster.objects.PathPoint;
import me.legofreak107.rollercoaster.objects.Seat;
import me.legofreak107.rollercoaster.objects.Track;
import me.legofreak107.rollercoaster.objects.Train;

public class API {

	public Main plugin = Main.getPlugin(Main.class);

	public Cart getCart(String name) {
		Cart c = new Cart();
		c.name = name;
		ItemStack item = new ItemStack(Material.AIR, 1, Short
				.parseShort(JavaPlugin.getPlugin(Main.class).getConfig().getString("Trains." + name + ".skin.materialdata")));
		item.setTypeId(JavaPlugin.getPlugin(Main.class).getConfig().getInt("Trains." + name + ".skin.materialid"));
		c.skin = item;
		ArrayList<Seat> seats = new ArrayList<Seat>();
		for (int i = 0; i < JavaPlugin.getPlugin(Main.class).getConfig().getInt("Trains." + name + ".seatcount"); i++) {
			Seat s = new Seat();
			s.fb = Double.parseDouble(JavaPlugin.getPlugin(Main.class).getConfig()
					.get("Trains." + name + ".seat" + i + ".offsetfb").toString());
			s.lr = Double.parseDouble(JavaPlugin.getPlugin(Main.class).getConfig()
					.get("Trains." + name + ".seat" + i + ".offsetlr").toString());
			if (JavaPlugin.getPlugin(Main.class).getConfig().contains("Trains." + name + ".seat" + i + ".offsetud")) {
				s.ud = Double.parseDouble(JavaPlugin.getPlugin(Main.class).getConfig()
						.get("Trains." + name + ".seat" + i + ".offsetud").toString());
			} else {
				s.ud = 0;
			}
			seats.add(s);
		}
		c.seats = seats;
		return c;
	}

	public void startTrain(String track) {
		Train train = getTrain(track);
		train.inStation = false;
		train.riding = true;
		for (int i = 0; i < train.carts.size(); i++) {
			Cart c = train.carts.get(i);
			c.pos = (i + 1) * train.cartOffset;
		}
		Integer count = 0;
		for (Cart c : train.carts) {
			for (Seat s : c.seats) {
				for (Entity e : s.holder.getPassengers()) {
					if (e instanceof Player) {
						count++;
					}
				}
			}
		}
		if (JavaPlugin.getPlugin(Main.class).getConfig().contains("Ridecount." + train.track.name + ".count")) {
			JavaPlugin.getPlugin(Main.class).getConfig().set("Ridecount." + train.track.name + ".count",
					JavaPlugin.getPlugin(Main.class).getConfig().getInt("Ridecount." + train.track.name + ".count") + count);
		} else {
			JavaPlugin.getPlugin(Main.class).getConfig().set("Ridecount." + train.track.name + ".count", count);
		}
		Config.saveConfig();
		Base.setActive(train.track.name, false);
		TrainStartEvent event = new TrainStartEvent("TrainStartEvent", train);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	public void stopTrain(String track) {
		Train train = getTrain(track);
		Base.setActive(train.track.name, false);
		train.riding = false;
		TrainStopEvent event = new TrainStopEvent("TrainStopEvent", train);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	public void setLoop(String track, Integer time) {
		Train train = getTrain(track);
		Main.getData();
		DataHolder.loop.put(train, (time));
	}

	public void setSpeed(Train t, Integer speed) {
		t.speed = speed;
	}

	public void setMinSpeed(Train t, Integer speed) {
		t.minSpeed = speed;
	}

	public void setMaxSpeed(Train t, Integer speed) {
		t.maxSpeed = speed;
	}

	public void setAutorotation(Train t, Boolean rotation) {
		for (Cart c : t.carts) {
			c.autoRotation = rotation;
		}
	}

	public void setAutotilt(Train t, Boolean tilt) {
		for (Cart c : t.carts) {
			c.autoTilt = tilt;
		}
	}

	public void setOffset(Train t, Integer offset) {
		t.cartOffset = offset;
	}

	public void setTilt(Cart c, Double tilt) {
		c.tiltTarget = tilt;
	}

	public void setRotation(Cart c, Integer rotation) {
		c.rotationTarget = rotation;
	}

	public void setItem(Cart c, ItemStack item) {
		c.holder.setHelmet(item);
	}

	public void setLocked(Train t, Boolean locked) {
		t.locked = locked;
		if (t.locked) {
			TrainLockEvent event = new TrainLockEvent("TrainLockEvent", t);
			Bukkit.getServer().getPluginManager().callEvent(event);
		} else {
			TrainUnlockEvent event = new TrainUnlockEvent("TrainUnlockEvent", t);
			Bukkit.getServer().getPluginManager().callEvent(event);
		}
	}

	public void setUpsideDown(Cart c, Boolean upsidedown) {
		c.loop = upsidedown;
	}

	public void setRiding(Player p, Seat s) {
		s.holder.addPassenger(p);
	}

	public Cart getCart(ArmorStand armorstand) {
		for (Train t : Main.getData().trains) {
			for (Cart c : t.carts) {
				if (c.holder == armorstand) {
					return c;
				}
			}
		}
		return null;
	}

	public Track getTrack(String name) {
		Track t = new Track();
		ArrayList<Location> loc = new ArrayList<Location>();
		Location origin = new Location(
				Bukkit.getWorld(JavaPlugin.getPlugin(Main.class).getConfig().getString("Tracks." + name + ".origin.w")),
				(Double) JavaPlugin.getPlugin(Main.class).getConfig().get("Tracks." + name + ".origin.x"),
				(Double) JavaPlugin.getPlugin(Main.class).getConfig().get("Tracks." + name + ".origin.y"),
				(Double) JavaPlugin.getPlugin(Main.class).getConfig().get("Tracks." + name + ".origin.z"));
		for (int i = 0; i < JavaPlugin.getPlugin(Main.class).getConfig().getInt("Tracks." + name + ".locs"); i++) {
			Location l = new Location(
					Bukkit.getWorld(
							JavaPlugin.getPlugin(Main.class).getConfig().getString("Tracks." + name + ".loc" + i + ".w")),
					(Double) JavaPlugin.getPlugin(Main.class).getConfig().get("Tracks." + name + ".loc" + i + ".x"),
					(Double) JavaPlugin.getPlugin(Main.class).getConfig().get("Tracks." + name + ".loc" + i + ".y"),
					(Double) JavaPlugin.getPlugin(Main.class).getConfig().get("Tracks." + name + ".loc" + i + ".z"));
			loc.add(l);
		}
		t.locs = generateTrack(loc, origin);
		t.origin = origin;
		t.name = name;
		loc.clear();
		return t;
	}

	public Train getTrain(String name) {
		Train t = null;
		for (Train tr : Main.getData().trains) {
			if (tr.track.name.equalsIgnoreCase(name)) {
				t = tr;
			}
		}
		return t;
	}

	public Train getTrain(Cart name) {
		Train t = null;
		for (Train tr : Main.getData().trains) {
			if (tr.carts.contains(name)) {
				t = tr;
			}

		}
		return t;
	}

	public Track getTrackStorage(String name) {
		Track t = null;
		for (Track tr : Main.getData().tracks) {
			if (tr.name.equalsIgnoreCase(name)) {
				t = tr;
			}
		}
		return t;
	}

	public Boolean isTrack(String name) {
		Boolean t = false;
		for (String s : JavaPlugin.getPlugin(Main.class).getConfig().getConfigurationSection("Tracks").getKeys(false)) {
			if (s.equalsIgnoreCase(name)) {
				t = true;
			}
		}
		return t;
	}

	public Boolean isTrain(String name) {
		Boolean t = false;
		for (String s : JavaPlugin.getPlugin(Main.class).getConfig().getConfigurationSection("Trains").getKeys(false)) {
			if (s.equalsIgnoreCase(name)) {
				t = true;
			}
		}
		return t;
	}

	public ArrayList<PathPoint> generateTrack(ArrayList<Location> locs, Location origin) {
		CustomPathBuilder.origin = origin;
		CustomPathBuilder.vectorList = locs;
		CustomPath path = CustomPathBuilder.build();
		ArrayList<PathPoint> as = new ArrayList<PathPoint>();
		for (int i = 0; i <= path.getPathLenght(); i++) {
			Location loc = path.getPathPosition((double) i);
			loc.setPitch(0);
			loc.setYaw(0);
			as.add(new PathPoint(loc.getX(), loc.getY(), loc.getZ(), 0D));
			Main.getData();
			if (!DataHolder.chunks.contains(loc.getChunk())) {
				Main.getData();
				DataHolder.chunks.add(loc.getChunk());
			}
		}
		path.pathPoints = null;
		return as;
	}

	public Train spawnTrain(String trainname, Integer length, Boolean hasLoco, Location loc, Boolean small, Track track,
			Integer minSpeed, Integer maxSpeed, Integer offset, Integer downpos) {
		ArrayList<Cart> carts = new ArrayList<Cart>();
		Train train = new Train();
		train.cartOffset = offset;
		train.maxSpeed = maxSpeed;
		train.minSpeed = minSpeed;
		train.cartDownPos = downpos;
		train.track = track;
		ArmorStand loco = null;
		for (int i = 0; i < length; i++) {
			if (i == length - 1 && hasLoco) {
				Cart t = getCart(trainname + "loco");
				ArmorStand cart = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
				cart.setSmall(small);
				cart.setCollidable(false);
				cart.setAI(false);
				cart.setGravity(false);
				cart.setVisible(false);
				cart.setHelmet(t.skin);
				cart.setCustomName("RollerCoaster");
				t.holder = cart;
				t.place = i;
				loco = cart;
				ArrayList<Seat> seats = new ArrayList<Seat>();
				for (Seat s : t.seats) {
					ArmorStand se = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
					se.setSmall(small);
					se.setCollidable(false);
					se.setAI(false);
					se.setGravity(false);
					se.setVisible(false);
					s.holder = se;
					s.parent = cart;
					s.locked = false;
					s.train = train;
					s.cart = t;
					seats.add(s);
					Main.getData().seatInfo.put(se, s);
				}
				t.seats = seats;
				carts.add(t);
				t.train = train;
				train.loco = t;
			} else {
				Cart t = getCart(trainname + "cart");
				ArmorStand cart = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
				cart.setSmall(small);
				cart.setCollidable(false);
				cart.setAI(false);
				cart.setGravity(false);
				cart.setVisible(false);
				cart.setHelmet(t.skin);
				cart.setCustomName("RollerCoaster");
				t.holder = cart;
				t.place = i;
				t.loco = loco;
				ArrayList<Seat> seats = new ArrayList<Seat>();
				for (Seat s : t.seats) {
					ArmorStand se = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
					se.setSmall(small);
					se.setCollidable(false);
					se.setAI(false);
					se.setGravity(false);
					se.setVisible(false);
					s.holder = se;
					s.parent = cart;
					s.train = train;
					s.locked = false;
					s.cart = t;
					seats.add(s);
					Main.getData().seatInfo.put(se, s);
				}
				if (i == length - 1) {
					loco = cart;
					train.loco = t;
				}
				t.seats = seats;
				t.train = train;
				carts.add(t);
			}
		}
		train.carts = carts;
		train.inStation = true;
		train.hasLoco = hasLoco;
		train.trainName = trainname;
		train.riding = false;
		Main.getData().trains.add(train);
		return train;
	}

	public Boolean isSeat(ArmorStand a) {
		Boolean check = false;
		if (Main.getData().seatInfo.containsKey(a)) {
			check = true;
		}
		return check;
	}

	public Seat getSeat(ArmorStand a) {
		return Main.getData().seatInfo.get(a);
	}
}
