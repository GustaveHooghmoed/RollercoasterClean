package me.legofreak107.rollercoaster.helpers;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.entity.ArmorStand;

import me.legofreak107.rollercoaster.objects.Receiver;
import me.legofreak107.rollercoaster.objects.Seat;
import me.legofreak107.rollercoaster.objects.Track;
import me.legofreak107.rollercoaster.objects.Train;

public class DataHolder {

	// Seat info (Used to get the store the Seats to the connected ArmorStands)
	public HashMap<ArmorStand, Seat> seatInfo = new HashMap<ArmorStand, Seat>();

	// Stored Tracks
	public ArrayList<Track> tracks = new ArrayList<Track>();

	// List of chunks containing tracks
	public static ArrayList<Chunk> chunks = new ArrayList<Chunk>();

	// Stored Trains
	public ArrayList<Train> trains = new ArrayList<Train>();

	// Stored visible points (While creating a path)
	public ArrayList<ArmorStand> pointsVisible = new ArrayList<ArmorStand>();

	// Stored loops (Keep track of looped trains with amount of seconds)
	public static HashMap<Train, Integer> loop = new HashMap<Train, Integer>();

	// Enabled language file
	public static String langFile;

	// Are trains spawned on startup
	public Boolean trainsSpawned = false;

	// List of sign receivers to disable/enable
	public HashMap<Integer, Receiver> receivers = new HashMap<Integer, Receiver>();

	// Current id for receiver signs
	public Integer id = 0;

}
