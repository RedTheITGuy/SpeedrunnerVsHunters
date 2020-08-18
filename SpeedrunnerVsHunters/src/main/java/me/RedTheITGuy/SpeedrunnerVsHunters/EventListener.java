package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class EventListener implements Listener {
	// Runs when a player enters a portal
	@EventHandler
	public void onPortalTeleport(PlayerPortalEvent event) {
		// Stores the dimension the player is teleporting to
		Location goingTo = event.getTo();
		
		// Runs if the user is going through a nether portal
		if(goingTo.getWorld().getEnvironment().equals(Environment.NETHER)) {
			// Runs if the player is in the nether
			if(event.getFrom().getWorld().getEnvironment().equals(Environment.NETHER)) {
				// Changes the world to the game world
				goingTo.setWorld(Bukkit.getWorld("svh-overworld"));
			}
			// Runs if the player is going to the nether
			else {
				// Changes the world to the game world
				goingTo.setWorld(Bukkit.getWorld("svh-nether"));
			}
		}
		// Runs if the user is going through a end portal
		else if(goingTo.getWorld().getEnvironment().equals(Environment.THE_END)) {
			// Runs if the player is in the end
			if(event.getFrom().getWorld().getEnvironment().equals(Environment.THE_END)) {
				// Changes the world to the game world
				goingTo.setWorld(Bukkit.getWorld("svh-overworld"));
			}
			// Runs if the player is going to the end
			else {
				// Changes the world to the game world
				goingTo.setWorld(Bukkit.getWorld("svh-end"));
			}
		}
		
		// Sets the new location for the event
		event.setTo(goingTo);
	}
}
