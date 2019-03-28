package de.siphalor.tweed;

import net.fabricmc.loader.api.FabricLoader;
import org.hjson.HjsonOptions;

import java.io.File;

public class Core {
	public static final String MODID = "tweed";
	public static final char HJSON_PATH_DELIMITER = ':';
	public static final String mainConfigDirectory = FabricLoader.getInstance().getConfigDirectory().getAbsolutePath() + File.separator + MODID + File.separator;
	public static final HjsonOptions HJSON_OPTIONS = new HjsonOptions().setAllowCondense(false).setBracesSameLine(true).setOutputComments(true).setSpace("\t");

}
