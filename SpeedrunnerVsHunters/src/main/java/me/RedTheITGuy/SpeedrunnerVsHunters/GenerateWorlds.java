package me.RedTheITGuy.SpeedrunnerVsHunters;
import java.io.File;
import java.nio.file.Files;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class GenerateWorlds {
    public static void generate(boolean override) {
	// Stores the names of the worlds in variables
	String overworldName = "svh-overworld";
	String netherName = "svh-nether";
	String endName = "svh-end";
	
	// Runs if the worlds need to be deleted
	if (override) {
	    // Gets the folder containing all the worlds
	    String worldsFolderPath = Bukkit.getWorldContainer().getPath();
	    
	    // Logs that the overworld is being deleted to the console
	    Bukkit.getLogger().info("Deleting overworld");
	    // Gets the overworld
	    World overworld = Bukkit.getServer().getWorld(overworldName);
	    // Unloads the world if it was loaded
	    if (overworld != null) Bukkit.unloadWorld(overworld, false);
	    // Gets the world folder
	    File overworldFolder = new File(worldsFolderPath + "/" + overworldName);
	    // Deletes the world file
	    deleteFile(overworldFolder);
	    
	    // Logs that the nether is being deleted to the console
	    Bukkit.getLogger().info("Deleting nether");
	    // Gets the nether
	    World nether = Bukkit.getServer().getWorld(netherName);
	    // Unloads the world if it was loaded
	    if (nether != null) Bukkit.unloadWorld(nether, false);
	    // Gets the world folder
	    File netherFolder = new File(worldsFolderPath + "/" + netherName);
	    // Deletes the world file
	    deleteFile(netherFolder);
	    
	    // Logs that the end is being deleted to the console
	    Bukkit.getLogger().info("Deleting end");
	    // Gets the end
	    World end = Bukkit.getServer().getWorld(endName);
	    // Unloads the world if it was loaded
	    if (end != null) Bukkit.unloadWorld(end, false);
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
	overworldCreator.createWorld();

	// Logs that the nether is being loaded to the console
	Bukkit.getLogger().info("Loading nether");
	// Creates a generator for the nether
	WorldCreator netherCreator = WorldCreator.name(netherName);
	// Sets the environment to nether
	netherCreator.environment(World.Environment.NETHER);
	// Creates the world
	netherCreator.createWorld();
	
	// Logs that the end is being loaded to the console
	Bukkit.getLogger().info("Loading end");
	// Creates a generator for the end
	WorldCreator endCreator = WorldCreator.name(endName);
	// Sets the environment to end
	endCreator.environment(World.Environment.THE_END);
	// Creates the world
	endCreator.createWorld();
    }
    
    // Creates the method for deleting files
    public static void deleteFile(File file) {
	// Gets a file array for all the files in the folder
	File[] contents = file.listFiles();
	// Runs if there is no files in the folder/file is not a folder
	if (contents != null) {
	    // Runs for all the files in the folder
	    for (File f : contents) {
		// Runs if the file is not a symlink
		if (!Files.isSymbolicLink(f.toPath())) {
		    // Runs this again for the file
		    deleteFile(f);
		}
	    }
	}
	// Deletes the file
	file.delete();
    }
}
