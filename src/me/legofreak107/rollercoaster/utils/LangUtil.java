package me.legofreak107.rollercoaster.utils;

import me.legofreak107.rollercoaster.Main;
import me.legofreak107.rollercoaster.helpers.DataHolder;

public class LangUtil {

	// Get message from the language file
	public static String getMessage(String path) {
		return ((String) Main.getSAL().getCustomLangConfig(DataHolder.langFile).get(path)).replace("&", "§");
	}

}
