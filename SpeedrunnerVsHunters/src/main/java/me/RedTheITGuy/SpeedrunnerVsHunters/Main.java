package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    // Runs when the plugin starts
    public void onEnable() {
	// Creates the config if it doesn't exist 
	this.saveDefaultConfig();
    }
    
    // Runs when the plugin is disabled
    public void onDisable() {
	
    }
}
