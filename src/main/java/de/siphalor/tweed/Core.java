package de.siphalor.tweed;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.hjson.HjsonOptions;

import java.io.File;

public class Core {
	public static final String MODID = "tweed";
	public static final Identifier CONFIG_SYNC_PACKET = new Identifier(MODID, "sync_config");

	public static final char HJSON_PATH_DELIMITER = '.';
	public static final String mainConfigDirectory = FabricLoader.getInstance().getConfigDirectory().getAbsolutePath() + File.separator;
	public static final HjsonOptions HJSON_OPTIONS = new HjsonOptions().setAllowCondense(false).setBracesSameLine(true).setOutputComments(true).setSpace("\t");

	private static MinecraftServer minecraftServer;

	public static boolean isMinecraftServerReady() {
		return minecraftServer != null;
	}

	public static MinecraftServer getMinecraftServer() {
		return minecraftServer;
	}

	public static void setMinecraftServer(MinecraftServer minecraftServer) {
		Core.minecraftServer = minecraftServer;
	}
}
