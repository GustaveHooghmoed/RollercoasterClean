package me.legofreak107.rollercoaster.utils;

public class NumberUtil {

	// Round up to nearest multiple of x
	public static int round(double i, int v) {
		if (i < 0) {
			return (int) (Math.ceil(i / v) * v);
		} else {
			return (int) (Math.floor(i / v) * v);
		}
	}

	// Check if input string is a number
	public static boolean checkMe(String s) {
		boolean amIValid = false;
		try {
			Integer.parseInt(s);
			// s is a valid integer!
			amIValid = true;
		} catch (NumberFormatException e) {
			// sorry, not an integer
			// we just move on, but you could have code here
		}
		return amIValid;
	}

	// Check if input string is a double
	public static boolean checkMeb(String s) {
		boolean amIValid = false;
		try {
			Double.parseDouble(s);
			// s is a valid integer!
			amIValid = true;
		} catch (NumberFormatException e) {
			// sorry, not an integer
			// we just move on, but you could have code here
		}
		return amIValid;
	}
}
