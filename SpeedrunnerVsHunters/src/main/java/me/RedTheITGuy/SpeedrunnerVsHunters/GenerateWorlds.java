package me.RedTheITGuy.SpeedrunnerVsHunters;
import java.io.File;
import java.nio.file.Files;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

public class GenerateWorlds {
	public void generate(boolean override) {
		// Enables the whitelist
		Bukkit.setWhitelist(true);
		
		// Loads the config
		FileConfiguration config = Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters").getConfig();
		// Gets info from the config
		Double overworldBorderSize = config.getDouble("worldborder.overworld");
		Double netherBorderSize = config.getDouble("worldborder.nether");
		Double endBorderSize = config.getDouble("worldborder.end");
		
		// Stores the names of the worlds in variables
		String overworldName = "svh-overworld";
		String netherName = "svh-nether";
		String endName = "svh-end";

		// Runs if the worlds need to be deleted
		if(override) {
			// Gets the folder containing all the worlds
			String worldsFolderPath = Bukkit.getWorldContainer().getPath();

			// Logs that the overworld is being deleted to the console
			Bukkit.getLogger().info("Deleting overworld");
			// Gets the overworld
			World overworld = Bukkit.getServer().getWorld(overworldName);
			// Unloads the world if it was loaded
			if(overworld != null)
				Bukkit.unloadWorld(overworld, false);
			// Gets the world folder
			File overworldFolder = new File(worldsFolderPath + "/" + overworldName);
			// Deletes the world file
			deleteFile(overworldFolder);

			// Logs that the nether is being deleted to the console
			Bukkit.getLogger().info("Deleting nether");
			// Gets the nether
			World nether = Bukkit.getServer().getWorld(netherName);
			// Unloads the world if it was loaded
			if(nether != null)
				Bukkit.unloadWorld(nether, false);
			// Gets the world folder
			File netherFolder = new File(worldsFolderPath + "/" + netherName);
			// Deletes the world file
			deleteFile(netherFolder);

			// Logs that the end is being deleted to the console
			Bukkit.getLogger().info("Deleting end");
			// Gets the end
			World end = Bukkit.getServer().getWorld(endName);
			// Unloads the world if it was loaded
			if(end != null)
				Bukkit.unloadWorld(end, false);
			// Gets the world folder
			File endFolder = new File(worldsFolderPath + "/" + endName);
			// Deletes the world file
			deleteFile(endFolder);
		}

		// Logs that the overworld is being loaded to the console
		Bukkit.getLogger().info("Loading overworld");
		// Creates a generator for the overworld
		WorldCreator overworldCreator = WorldCreator.name(overworldName);
		// Sets the environment to overworld
		overworldCreator.environment(World.Environment.NORMAL);
		// Creates the world
		World overworld = overworldCreator.createWorld();
		// Sets the world border for the world
		overworld.getWorldBorder().setSize(overworldBorderSize);
		// Sets the game rules for the new world
		overworld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

		// Logs that the nether is being loaded to the console
		Bukkit.getLogger().info("Loading nether");
		// Creates a generator for the nether
		WorldCreator netherCreator = WorldCreator.name(netherName);
		// Sets the environment to nether
		netherCreator.environment(World.Environment.NETHER);
		// Creates the world
		World nether = netherCreator.createWorld();
		// Sets the world border for the nether
		nether.getWorldBorder().setSize(netherBorderSize);
		// Sets the game rules for the new world
		nether.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

		// Logs that the end is being loaded to the console
		Bukkit.getLogger().info("Loading end");
		// Creates a generator for the end
		WorldCreator endCreator = WorldCreator.name(endName);
		// Sets the environment to end
		endCreator.environment(World.Environment.THE_END);
		// Creates the world
		World end = endCreator.createWorld();
		// Sets the world border for the end
		end.getWorldBorder().setSize(endBorderSize);
		// Sets the game rules for the new world
		end.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		
		// Disables the whitelist
		Bukkit.setWhitelist(false);
	}

	// Creates the method for deleting files
	public static void deleteFile(File file) {
		// Gets a file array for all the files in the folder
		File[] contents = file.listFiles();
		// Runs if there is no files in the folder/file is not a folder
		if(contents != null) {
			// Runs for all the files in the folder
			for(File f : contents) {
				// Runs if the file is not a symlink
				if(!Files.isSymbolicLink(f.toPath())) {
					// Runs this again for the file
					deleteFile(f);
				}
			}
		}
		// Deletes the file
		file.delete();
	}
}
