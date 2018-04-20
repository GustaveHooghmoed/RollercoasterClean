package me.legofreak107.rollercoaster.libs;

import me.legofreak107.rollercoaster.Main;
import me.legofreak107.rollercoaster.helpers.DataHolder;

public class LangFile {

	public void generateLanguageFile(Main plugin) {
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.trainSpawned", "&2Train spawned!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Error.invalidTrain",
				"&cInvalid train type, try /rc train list for a list of trains.");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Error.invalidTrack",
				"&cInvalid track type, try /rc tracklist for a list of tracks.");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Usage.spawnTrain",
				"&cUsage: /rc spawntrain <trainname> <traintype> <cartamount> <haslocomotive> <cartoffset> <minspeed> <maxspeed> <small> <y offset>");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Error.noPermissions",
				"&cYou don't have permissions to excecute this command!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.trainStarted", "&2Train started!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Usage.startTrain",
				"&cUsage: /rc starttrain <trainname>");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Error.noNumber",
				"&cThe argument you entered isn't a valid number!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Usage.loop",
				"&2Usage: /rc loop <trackname> <loop seconds>");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.trainStopped", "&3Train stopped!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Usage.stopTrain",
				"&cUsage: /rc stoptrain <trainname>");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.addPoint", "&2Point added!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.pathCreated1",
				"&2Started the creation of a new path!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.pathCreated2",
				"&2You can now add points to the path by typing /rc addpoint");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.trainCreated", "&2Train created!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.trainEdited", "&2Train edited!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Usage.createTrain",
				"&cUsage: /rc train create <typename> <seatsloco> <seatscart>");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Error.invalidSeatNumber",
				"&cThis is not a valid seat number, please type a valid number!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Usage.setSkin",
				"&cUsage: /rc train setskin <cart/loco>");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Usage.trainChairPos",
				"&cUsage: /rc train chairs pos <name> <seatnumber> <lr> <fb> <ud> <cart/loco>");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Error.locoCart",
				"&cInvalid args, use Loco/Cart instead!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.noTrains",
				"&2You don't have any saved trains!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.pathBuild", "&2Path build!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.removePoint", "&2Point removed!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Usage.pathBuild", "&cUsage: /rc build <trainname>");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.trainRemoved", "&2Train removed!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Usage.removeTrain",
				"&cUsage: /rc removetrain <trainname>");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.noTracks",
				"&2You don't have any saved tracks! Please create one first!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.trainLocked", "&2Train locked!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Usage.lockTrain",
				"&cUsage: /rc locktrain <trainname>");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.configReloaded", "&2Config reloaded!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.trainUnlocked", "&2Train unlocked!");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Usage.unlockTrain",
				"&cUsage: /rc unlocktrain <trainname>");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Usage.rcHelp", "&cUsage: /rc help <page>");
		Main.getSAL().getCustomLangConfig(DataHolder.langFile).set("Message.loopSet",
				"&2Loop set to: %seconds% for: %track%");
		Main.getSAL().saveCustomLangConfig(DataHolder.langFile);
	}

}
