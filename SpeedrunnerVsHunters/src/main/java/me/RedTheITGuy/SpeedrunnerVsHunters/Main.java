package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	// Runs when the plugin starts
	public void onEnable() {
		// Creates the config if it doesn't exist
		this.saveDefaultConfig();

		// Loads the config
		FileConfiguration config = this.getConfig();
		// Gets info from the config
		boolean loadPrevious = config.getBoolean("persistantSave");

		// Generates the worlds needed (and deletes old versions if necessary)
		GenerateWorlds.generate(!loadPrevious);	
		
		// Gets the class to create the scoreboard
		manageScoreboard manageScoreboard = new manageScoreboard();
		// Runs the method to create the scoreboard
		manageScoreboard.createBoard();
		
		// Registers the listener
		Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this);
	}
}
