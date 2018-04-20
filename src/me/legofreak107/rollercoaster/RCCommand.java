package me.legofreak107.rollercoaster;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import me.legofreak107.rollercoaster.api.TrainLockEvent;
import me.legofreak107.rollercoaster.api.TrainStopEvent;
import me.legofreak107.rollercoaster.api.TrainUnlockEvent;
import me.legofreak107.rollercoaster.helpers.Config;
import me.legofreak107.rollercoaster.helpers.DataHolder;
import me.legofreak107.rollercoaster.libs.CustomPath;
import me.legofreak107.rollercoaster.libs.CustomPathBuilder;
import me.legofreak107.rollercoaster.objects.Cart;
import me.legofreak107.rollercoaster.objects.PathPoint;
import me.legofreak107.rollercoaster.objects.Seat;
import me.legofreak107.rollercoaster.objects.Track;
import me.legofreak107.rollercoaster.objects.Train;
import me.legofreak107.rollercoaster.utils.LangUtil;
import me.legofreak107.rollercoaster.utils.NumberUtil;
import me.legofreak107.rollercoaster.utils.VectorUtils;

public class RCCommand implements CommandExecutor {

	private Location lastLoc;

	// On command
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Main check for /rc
		if (cmd.getName().equalsIgnoreCase("rc")) {
			// Check if arguments are more then 0
			if (args.length > 0) {
				// Check if first argument is spawntrain (/rc spawntrain)
				if (args[0].equalsIgnoreCase("spawntrain")) {
					// Check if user has permissions
					if (sender.hasPermission("rollercoaster.spawntrain")) {
						// Check if command has the correct amount of arguments
						if (args.length == 10) {
							String track = args[1];
							String train = args[2];
							// Check if all the arguments entered are valid
							if (NumberUtil.checkMe(args[3]) && NumberUtil.checkMe(args[4])
									&& NumberUtil.checkMe(args[5]) && NumberUtil.checkMe(args[6])
									&& NumberUtil.checkMe(args[7]) && NumberUtil.checkMe(args[8])
									&& NumberUtil.checkMe(args[9])) {
								if (Main.getAPI().isTrack(track)) {
									if (JavaPlugin.getPlugin(Main.class).getConfig().contains("Trains." + train + "cart")) {
										// Spawn in train using the given args
										Integer length = Integer.parseInt(args[3]);
										Integer loco = Integer.parseInt(args[4]);
										Integer small = Integer.parseInt(args[8]);
										Integer cartDownPos = Integer.parseInt(args[9]);
										Boolean tilt = false;
										Boolean hasLoc = false;
										Boolean isSmall = false;
										if (loco == 1)
											hasLoc = true;
										if (small == 1)
											isSmall = true;
										Train t = Main.getAPI().spawnTrain(train, length, hasLoc,
												((Player) sender).getLocation(), isSmall, Main.getAPI().getTrack(track),
												Integer.parseInt(args[6]), Integer.parseInt(args[7]),
												Integer.parseInt(args[5]), Integer.parseInt(args[9]));
										t.track = Main.getAPI().getTrackStorage(track);
										t.tilt = tilt;
										t.cartOffset = Integer.parseInt(args[5]);
										t.minSpeed = Integer.parseInt(args[6]);
										t.maxSpeed = Integer.parseInt(args[7]);
										Main.getData().trains.add(t);
										sender.sendMessage(LangUtil.getMessage("Message.trainSpawned"));
									} else {
										// Train is not valid
										sender.sendMessage(LangUtil.getMessage("Error.invalidTrain"));
									}
								} else {
									// Track is not valid
									sender.sendMessage(LangUtil.getMessage("Error.invalidTrack"));
								}
							} else {
								// Wrong command usage
								sender.sendMessage(LangUtil.getMessage("Usage.spawnTrain"));
							}
						} else {
							// Wrong command usage
							sender.sendMessage(LangUtil.getMessage("Usage.spawnTrain"));
						}
					} else {
						// User does not have permissions
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
					// Check if first argument is starttrain (/rc starttrain)
				} else if (args[0].equalsIgnoreCase("starttrain")) {
					// Check if user has permissions
					if (sender.hasPermission("rollercoaster.starttrain")) {
						// Check if command entered has correct amount of arguments
						if (args.length == 2) {
							String track = args[1];
							// Check if second argument is a track
							if (Main.getAPI().isTrack(track)) {
								// Start the ride
								Main.getAPI().startTrain(track);
								sender.sendMessage(LangUtil.getMessage("Message.trainStarted"));
							} else {
								// Entered track is not valid
								sender.sendMessage(LangUtil.getMessage("Error.invalidTrack"));
							}
						} else {
							// Entered command has wrong syntax
							sender.sendMessage(LangUtil.getMessage("Usage.startTrain"));
						}
					} else {
						// User does not have permissions
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
					// BETA (used for the bezier curve tracks) IGNORE THIS
				} else if (args[0].equalsIgnoreCase("loop")) {
					// Check if user has permissions
					if (sender.hasPermission("rollercoaster.loop")) {
						// Check if command has correct amount of args
						if (args.length == 3) {
							String track = args[1];
							// Check if given args are correct
							if (NumberUtil.checkMe(args[2])) {
								if (Main.getAPI().isTrack(track)) {
									// Set the loop for the given train
									Train train = Main.getAPI().getTrain(track);
									sender.sendMessage(LangUtil.getMessage("Message.loopSet")
											.replace("%seconds%", args[2]).replace("%track%", track));
									Main.getData();
									DataHolder.loop.put(train, Integer.parseInt(args[2]));
								} else {
									// Entered track is not valid
									sender.sendMessage(LangUtil.getMessage("Error.invalidTrack"));
								}
							} else {
								// Command syntax is invalid
								sender.sendMessage(LangUtil.getMessage("Usage.loop"));
							}
						} else {
							// Command syntax is invalid
							sender.sendMessage(LangUtil.getMessage("Usage.loop"));
						}
					} else {
						// User does not have permissions
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
					// Check if first argument is stoptrain (/rc stoptrain)
				} else if (args[0].equalsIgnoreCase("stoptrain")) {
					// Check if user has permissions
					if (sender.hasPermission("rollercoaster.stoptrain")) {
						// Check if command has the right amount of args
						if (args.length == 2) {
							String track = args[1];
							// Check if given args are correct
							if (Main.getAPI().isTrack(track)) {
								// Stop the given train
								Train train = Main.getAPI().getTrain(track);
								train.inStation = false;
								train.riding = false;
								TrainStopEvent event = new TrainStopEvent("TrainStopEvent", train);
								Bukkit.getServer().getPluginManager().callEvent(event);
								sender.sendMessage(LangUtil.getMessage("Message.trainStopped"));
							} else {
								// Given track is invalid
								sender.sendMessage(LangUtil.getMessage("Error.invalidTrack"));
							}
						} else {
							// Command has wrong syntax
							sender.sendMessage(LangUtil.getMessage("usage.stopTrain"));
						}
					} else {
						// User does not have permissions
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
				} else if (args[0].equalsIgnoreCase("addpoint")) {
					if (sender.hasPermission("rollercoaster.createtrack")) {
						if (CustomPathBuilder.vectorList.isEmpty()) {
							CustomPathBuilder.addPoint(((Player) sender).getLocation());
							Main.getData();
							if (!DataHolder.chunks.contains(((Player) sender).getLocation().getChunk())) {
								Main.getData();
								DataHolder.chunks.add(((Player) sender).getLocation().getChunk());
							}
							lastLoc = ((Player) sender).getLocation();
							ArmorStand ar = (ArmorStand) ((Player) sender).getLocation().getWorld()
									.spawnEntity(((Player) sender).getLocation(), EntityType.ARMOR_STAND);
							ar.setGravity(false);
							ar.setCustomName("Point");
							Main.getData().pointsVisible.add(ar);
						} else {
							Location prevPoint = lastLoc;
							Location newLoc = ((Player) sender).getLocation();
							Vector vector = VectorUtils.getDirectionBetweenLocations(prevPoint, newLoc);
							int i2 = 1;
							for (double i = 1; i <= prevPoint.distance(newLoc); i += 0.5) {
								vector.multiply(i);
								prevPoint.add(vector);
								if (i2 < 3) {
									i2++;
								} else {
									CustomPathBuilder.addPoint(prevPoint.clone());
									ArmorStand ar = (ArmorStand) prevPoint.getWorld().spawnEntity(prevPoint,
											EntityType.ARMOR_STAND);
									lastLoc = prevPoint.clone();
									ar.setGravity(false);
									ar.setCustomName("Point");
									Main.getData().pointsVisible.add(ar);
									i2 = 0;
								}
								prevPoint.subtract(vector);
								vector.normalize();
							}
						}
						sender.sendMessage(LangUtil.getMessage("Message.addPoint"));
					} else {
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
				} else if (args[0].equalsIgnoreCase("removepoint")) {
					if (sender.hasPermission("rollercoaster.createtrack")) {
						CustomPathBuilder.removePoint();
						sender.sendMessage(LangUtil.getMessage("Message.removePoint"));
					} else {
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
				} else if (args[0].equalsIgnoreCase("startpath")) {
					if (sender.hasPermission("rollercoaster.createtrack")) {
						CustomPathBuilder.origin = ((Player) sender).getLocation();
						CustomPathBuilder.vectorList.clear();
						sender.sendMessage(LangUtil.getMessage("Message.pathCreated1"));
						sender.sendMessage(LangUtil.getMessage("Message.pathCreated2"));
					} else {
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
				} else if (args[0].equalsIgnoreCase("train")) {
					if (sender.hasPermission("rollercoaster.createtrain")) {
						if (args.length > 1) {
							if (args[1].equalsIgnoreCase("create")) {
								if (args.length == 5 && NumberUtil.checkMe(args[3]) && NumberUtil.checkMe(args[4])) {
									Cart c = new Cart();
									c.name = args[2] + "cart";

									ArrayList<Seat> seats = new ArrayList<Seat>();
									for (int i = 0; i < Integer.parseInt(args[4]); i++) {
										Seat s = new Seat();
										s.fb = 0;
										s.lr = 0;
										s.ud = 0;
										seats.add(s);
									}
									c.seats = seats;
									c.skin = ((Player) sender).getInventory().getItemInMainHand();
									Main.getSAL().saveTrain(c);

									Cart c2 = new Cart();
									c2.name = args[2] + "loco";

									ArrayList<Seat> seats2 = new ArrayList<Seat>();
									for (int i = 0; i < Integer.parseInt(args[3]); i++) {
										Seat s = new Seat();
										s.fb = 0;
										s.lr = 0;
										s.ud = 0;
										seats2.add(s);
									}
									c2.seats = seats2;
									c2.skin = ((Player) sender).getInventory().getItemInMainHand();
									Main.getSAL().saveTrain(c2);
									sender.sendMessage(LangUtil.getMessage("Message.trainCreated"));
								} else {
									sender.sendMessage(LangUtil.getMessage("Usage.createTrain"));
								}
							} else if (args[1].equalsIgnoreCase("chairs")) {
								if (args[2].equalsIgnoreCase("pos")) {
									if (args.length == 9) {
										String type = args[8];
										String name = args[3];
										if (type.equalsIgnoreCase("loco")) {
											name = name + "loco";
										} else if (type.equalsIgnoreCase("cart")) {
											name = name + "cart";
										}
										if (Main.getAPI().isTrain(name)) {
											if (NumberUtil.checkMe(args[4])) {
												if (NumberUtil.checkMeb(args[5])) {
													if (NumberUtil.checkMeb(args[6])) {
														if (NumberUtil.checkMeb(args[7])) {
															Integer number = Integer.parseInt(args[4]);

															if (number <= JavaPlugin.getPlugin(Main.class).getConfig()
																	.getInt("Trains." + name + ".seatcount")
																	&& number > -1) {
																Double LR = Double.parseDouble(args[5]);
																Double FB = Double.parseDouble(args[6]);
																Double UD = Double.parseDouble(args[7]);
																JavaPlugin.getPlugin(Main.class).getConfig().set("Trains."
																		+ name + ".seat" + number + ".offsetlr", LR);
																JavaPlugin.getPlugin(Main.class).getConfig().set("Trains."
																		+ name + ".seat" + number + ".offsetfb", FB);
																JavaPlugin.getPlugin(Main.class).getConfig().set("Trains."
																		+ name + ".seat" + number + ".offsetud", UD);
																JavaPlugin.getPlugin(Main.class).saveConfig();
																sender.sendMessage(
																		LangUtil.getMessage("Message.trainEdited"));
															} else {
																sender.sendMessage(
																		LangUtil.getMessage("Error.invalidSeatNumber"));
															}
														} else {
															sender.sendMessage(LangUtil.getMessage("Error.noNumber"));
														}
													} else {
														sender.sendMessage(LangUtil.getMessage("Error.noNumber"));
													}
												} else {
													sender.sendMessage(LangUtil.getMessage("Error.noNumber"));
												}
											} else {
												sender.sendMessage(LangUtil.getMessage("Error.noNumber"));
											}
										} else {
											sender.sendMessage(LangUtil.getMessage("Error.invalidTrain"));
										}
									} else {
										sender.sendMessage(LangUtil.getMessage("Usage.trainChairPos"));
									}
								}
							} else if (args[1].equalsIgnoreCase("setskin")) {
								if (args.length == 4) {
									String name = args[2];
									String type = args[3];
									if (type.equalsIgnoreCase("Loco")) {
										if (Main.getAPI().isTrain(name + "loco")) {
											getConfig().set("Trains." + name + "loco.skin.materialid",
													((Player) sender).getInventory().getItemInMainHand().getTypeId());
											getConfig().set("Trains." + name + "loco.skin.materialdata",
													((Player) sender).getInventory().getItemInMainHand().getDurability()
															+ "");
											Config.saveConfig();
											sender.sendMessage(LangUtil.getMessage("Message.trainEdited"));
										} else {
											sender.sendMessage(LangUtil.getMessage("Error.invalidTrain"));
										}
									} else if (type.equalsIgnoreCase("Cart")) {
										if (Main.getAPI().isTrain(name + "cart")) {
											getConfig().set("Trains." + name + "cart.skin.materialid",
													((Player) sender).getInventory().getItemInMainHand().getTypeId());
											getConfig().set("Trains." + name + "cart.skin.materialdata",
													((Player) sender).getInventory().getItemInMainHand().getDurability()
															+ "");
											Config.saveConfig();
											sender.sendMessage(LangUtil.getMessage("Message.trainEdited"));
										} else {
											sender.sendMessage(LangUtil.getMessage("Error.invalidTrain"));
										}
									} else {
										sender.sendMessage(LangUtil.getMessage("Error.locoCart"));
									}
								}
							} else if (args[1].equalsIgnoreCase("list")) {
								sender.sendMessage("§3Train List:");
								if (getConfig().contains("Trains")) {
									for (String s : getConfig().getConfigurationSection("Trains").getKeys(false)) {
										if (s.contains("loco")) {
											sender.sendMessage("§2" + s.replace("loco", ""));
										}
									}
								} else {
									sender.sendMessage(LangUtil.getMessage("Message.noTrains"));
								}

							}
						} else {
							sender.sendMessage("§8============================================");
							sender.sendMessage("§6/rc train list");
							sender.sendMessage("§6/rc train create");
							sender.sendMessage("§6/rc train chairs");
							sender.sendMessage("§6/rc train setskin");
							sender.sendMessage("§8============================================");
						}
					} else {
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
				} else if (args[0].equalsIgnoreCase("build")) {
					if (sender.hasPermission("rollercoaster.createtrack")) {
						if (args.length == 2) {
							String trainname = args[1];
							Track t = new Track();
							t.name = trainname;
							CustomPath path = CustomPathBuilder.build();
							t.locstosave = CustomPathBuilder.vectorList;
							t.origin = CustomPathBuilder.origin;
							List<PathPoint> as = new ArrayList<PathPoint>();
							for (int i = 0; i <= path.getPathLenght(); i++) {
								Location loc = path.getPathPosition((double) i);
								loc.setPitch(0);
								loc.setYaw(0);
								as.add(new PathPoint(loc.getX(), loc.getY(), loc.getZ(), 0D));
							}
							for (ArmorStand ar : Main.getData().pointsVisible) {
								ar.remove();
							}
							t.locs = (ArrayList<PathPoint>) as;
							Main.getData().tracks.add(t);
							Main.getSAL().saveTrack(t);
							sender.sendMessage(LangUtil.getMessage("Message.pathBuild"));
						} else {
							sender.sendMessage(LangUtil.getMessage("Usage.pathBuild"));
						}
					} else {
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
				} else if (args[0].equalsIgnoreCase("removetrain")) {
					if (sender.hasPermission("rollercoaster.removetrain")) {
						if (args.length == 2) {
							String trainname = args[1];
							if (Main.getAPI().getTrain(trainname) != null) {
								Train t = Main.getAPI().getTrain(trainname);
								for (Cart c : t.carts) {
									c.holder.remove();
									for (Seat s : c.seats) {
										s.holder.remove();
									}
								}
								Main.getData().trains.remove(t);
								sender.sendMessage(LangUtil.getMessage("Message.trainRemoved"));
							}
						} else {
							sender.sendMessage(LangUtil.getMessage("Usage.removeTrain"));
						}
					} else {
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
				} else if (args[0].equalsIgnoreCase("tracklist")) {
					if (sender.hasPermission("rollercoaster.tracklist")) {
						sender.sendMessage("§3Track List:");

						if (getConfig().contains("Tracks")) {
							for (Track s : Main.getData().tracks) {
								sender.sendMessage("§2" + s.name);
							}
						} else {
							sender.sendMessage(LangUtil.getMessage("Message.noTracks"));
						}
					} else {
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
				} else if (args[0].equalsIgnoreCase("locktrain")) {
					if (sender.hasPermission("rollercoaster.lock")) {
						if (args.length == 2) {
							String st = args[1];
							if (Main.getAPI().getTrain(st) != null) {
								Train t = Main.getAPI().getTrain(st);
								t.locked = true;
								TrainLockEvent event = new TrainLockEvent("TrainLockEvent", t);
								Bukkit.getServer().getPluginManager().callEvent(event);
								for (Cart c : t.carts) {
									for (Seat s : c.seats) {
										s.locked = true;
									}
								}
								sender.sendMessage(LangUtil.getMessage("Message.trainLocked"));
							}
						} else {
							sender.sendMessage(LangUtil.getMessage("Usage.lockTrain"));
						}
					} else {
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (sender.hasPermission("rollercoaster.reload")) {

						for (Train t : Main.getData().trains) {
							for (Cart c : t.carts) {
								c.holder.remove();
								for (Seat s : c.seats) {
									s.holder.remove();
								}
							}
						}
						Main.getData().trains.clear();
						Main.getData().tracks.clear();
						if (getConfig().contains("Tracks")) {
							for (String track : getConfig().getConfigurationSection("Tracks").getKeys(false)) {
								Track t = Main.getAPI().getTrack(track);
								Main.getData().tracks.add(t);
							}
						}
						sender.sendMessage(LangUtil.getMessage("Message.configReloaded"));
					} else {
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
				} else if (args[0].equalsIgnoreCase("unlocktrain")) {
					if (sender.hasPermission("rollercoaster.lock")) {
						if (args.length == 2) {
							String st = args[1];
							if (Main.getAPI().getTrain(st) != null) {
								Train t = Main.getAPI().getTrain(st);
								t.locked = false;
								TrainUnlockEvent event = new TrainUnlockEvent("TrainUnlockEvent", t);
								Bukkit.getServer().getPluginManager().callEvent(event);
								for (Cart c : t.carts) {
									for (Seat s : c.seats) {
										s.locked = false;
									}
								}
								sender.sendMessage(LangUtil.getMessage("Message.trainUnlocked"));
							}
						} else {
							sender.sendMessage(LangUtil.getMessage("Usage.unlockTrain"));
						}
					} else {
						sender.sendMessage(LangUtil.getMessage("Error.noPermissions"));
					}
				} else if (args[0].equalsIgnoreCase("help")) {
					if (args.length == 2) {
						Integer page = Integer.parseInt(args[1]);
						if (page == 1) {
							sender.sendMessage("§2===============§8 RollerCoaster Help [1] §2===============");
							sender.sendMessage("§3/rc spawntrain <trainname> <traintype> <amount>");
							sender.sendMessage("§3/rc starttrain <trainname>");
							sender.sendMessage("§3/rc stoptrain <trainname>");
							sender.sendMessage("§3/rc startpath");
							sender.sendMessage("§3/rc addpoint");
							sender.sendMessage("§3/rc removepoint");
							sender.sendMessage("§3/rc build <name>");
							sender.sendMessage("§3/rc help 2");
							sender.sendMessage("§2===============§8 RollerCoaster Help [1] §2===============");
						} else if (page == 2) {
							sender.sendMessage("§2===============§8 RollerCoaster Help [2] §2===============");
							sender.sendMessage("§3/rc removetrain <trainname>");
							sender.sendMessage("§3/rc locktrain <trainname>");
							sender.sendMessage("§3/rc unlocktrain <trainname>");
							sender.sendMessage("§3/rc train create <trainname>");
							sender.sendMessage("§3/rc train chairs set <chairamount> <name> <Loco/Cart>");
							sender.sendMessage(
									"§3/rc train chairs pos <name> <chair#> <left/right> <front/back> <up/down> <Loco/Cart>");
							sender.sendMessage("§3/rc train setskin <trainname> <Loco/Cart>");
							sender.sendMessage("§3/rc train list");
							sender.sendMessage("§3/rc tracklist");
							sender.sendMessage("§2===============§8 RollerCoaster Help [2] §2===============");
						} else {
							sender.sendMessage("§2===============§8 RollerCoaster Help [1] §2===============");
							sender.sendMessage("§3/rc spawntrain <trainname> <traintype> <amount>");
							sender.sendMessage("§3/rc starttrain <trainname>");
							sender.sendMessage("§3/rc stoptrain <trainname>");
							sender.sendMessage("§3/rc startpath");
							sender.sendMessage("§3/rc addpoint");
							sender.sendMessage("§3/rc removepoint");
							sender.sendMessage("§3/rc build <name>");
							sender.sendMessage("§3/rc help 2");
							sender.sendMessage("§2===============§8 RollerCoaster Help [1] §2===============");
						}
					} else {
						sender.sendMessage(LangUtil.getMessage("Usage.rcHelp"));
					}
				} else {
					sender.sendMessage("§2===============§8 RollerCoaster Help [1] §2===============");
					sender.sendMessage("§3/rc spawntrain <trainname> <traintype> <amount>");
					sender.sendMessage("§3/rc starttrain <trainname>");
					sender.sendMessage("§3/rc stoptrain <trainname>");
					sender.sendMessage("§3/rc startpath");
					sender.sendMessage("§3/rc addpoint");
					sender.sendMessage("§3/rc removepoint");
					sender.sendMessage("§3/rc build <name>");
					sender.sendMessage("§3/rc help 2");
					sender.sendMessage("§2===============§8 RollerCoaster Help [1] §2===============");
				}
			} else {
				sender.sendMessage("§2===============§8 RollerCoaster Help [1] §2===============");
				sender.sendMessage("§3/rc spawntrain <trainname> <traintype> <amount>");
				sender.sendMessage("§3/rc removetrain <trainname>");
				sender.sendMessage("§3/rc starttrain <trainname>");
				sender.sendMessage("§3/rc stoptrain <trainname>");
				sender.sendMessage("§3/rc startpath");
				sender.sendMessage("§3/rc addpoint");
				sender.sendMessage("§3/rc build <name>");
				sender.sendMessage("§3/rc help 2");
				sender.sendMessage("§2===============§8 RollerCoaster Help [1] §2===============");
			}
		}
		return false;
	}

	private FileConfiguration getConfig() {
		return JavaPlugin.getPlugin(Main.class).getConfig();
	}

}
