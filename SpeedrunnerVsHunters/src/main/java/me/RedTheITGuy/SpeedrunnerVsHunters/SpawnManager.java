package me.RedTheITGuy.SpeedrunnerVsHunters;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SpawnManager {
	public void spawnPlayer(Player player) {
		// Teleports the player to the location
		player.teleportAsync(getSpawnLocation());
	}
	
	public Location getSpawnLocation() {
		// Creates the random for random numbers
		Random random = new Random();
		
		// Gets the spawn location for the overworld
		Location teleportLocation = Bukkit.getWorld("svh-overworld").getSpawnLocation();
		// Sets the location to a random x and z between -25 and 25 for better spacing
		teleportLocation.setX(teleportLocation.getX() + ((random.nextDouble() * 50.0) - 25.0));
		teleportLocation.setZ(teleportLocation.getY() + ((random.nextDouble() * 50.0) - 25.0));
		// Set the y to the highest empty block
		teleportLocation.setY(teleportLocation.getWorld().getHighestBlockYAt(teleportLocation));
		
		// Gets the location for below the spawn location
		Location belowSpawn = teleportLocation;
		belowSpawn.setY(teleportLocation.getY() - 1);
		// Gets the block below the spawn location
		Block spawnBlock = Bukkit.getWorld("svh-overworld").getBlockAt(belowSpawn);
		
		// Runs while the spawn block cannot be collided with
		while (spawnBlock.isPassable()) {
			//Drop the spawn block down
			belowSpawn.setY(belowSpawn.getY() - 1);
			// Resets the spawn block
			spawnBlock = Bukkit.getWorld("svh-overworld").getBlockAt(belowSpawn);
		}
		
		// Sets the spawn location to above the spawn block
		teleportLocation.setY(belowSpawn.getY() + 1);
		
		// Runs method again if user is spawning in a liquid
		if (Bukkit.getWorld("svh-overworld").getBlockAt(teleportLocation).isLiquid()) return getSpawnLocation();
		
		
		// Returns the location
		return teleportLocation;
	}
}
