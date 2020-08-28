package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class EventListener implements Listener {
	// Runs when a player enters a portal
	@EventHandler
	public void onPortalTeleport(PlayerPortalEvent event) {
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		
		// Stores the player in the event
		Player player = event.getPlayer();
		
		// Runs if the board doesn't exist or a game isn't running
		if (infoBoard == null || !infoBoard.getScore(ChatColor.AQUA + "Runner: ").isScoreSet()) {
			// Sets the player's velocity
			player.setVelocity(player.getLocation().getDirection().multiply(-2));
			// Cancels the teleport event
			event.setCancelled(true);
			// Exits the method
			return;
		}
		
		// Creates a boolean to store if the player is the runner
		boolean isRunner = false;
		// Sets that boolean to true if the player is the runner
		if (scoreboard.getTeam("runnerName").getSuffix().equalsIgnoreCase(player.getDisplayName())) isRunner = true;
		
		// Stores the dimension the player is teleporting to
		Location goingTo = event.getTo();
		
		// Runs if the user is going through a nether portal
		if(goingTo.getWorld().getEnvironment().equals(Environment.NETHER)) {
			// Runs if the player is in the nether
			if(event.getFrom().getWorld().getEnvironment().equals(Environment.NETHER)) {
				// Changes the world to the game world
				goingTo.setWorld(Bukkit.getWorld("svh-overworld"));
				
				// Runs if the player is the runner
				if (isRunner) {
					// Runs if the location has not yet been revealed
					if (!infoBoard.getScore(ChatColor.DARK_AQUA + "Player Location:").isScoreSet()) {
						// Deletes the empty space for better spacing if it exists
						if (infoBoard.getScore(ChatColor.LIGHT_PURPLE.toString()).isScoreSet()) scoreboard.resetScores(ChatColor.LIGHT_PURPLE.toString());
					}
					
					// Deletes the portal location label if it exists
					if (infoBoard.getScore(ChatColor.DARK_AQUA + "Portal Location:").isScoreSet()) scoreboard.resetScores(ChatColor.DARK_AQUA + "Portal Location:");
					// Deletes the portal x if it exists
					if (infoBoard.getScore(ChatColor.AQUA + "Portal X: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Portal X: ");
					// Deletes the portal y if it exists
					if (infoBoard.getScore(ChatColor.AQUA + "Portal Y: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Portal Y: ");
					// Deletes the portal z if it exists
					if (infoBoard.getScore(ChatColor.AQUA + "Portal Z: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Portal Z: ");
					
					// Runs for every player
    				for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
    					// Sends an action bar to let the player know the player has left the nether
    					currentPlayer.sendActionBar(ChatColor.GOLD + "The runner has left the nether.");
    					// Plays a sound to draw attention to the dimension change
    					currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.VOICE, 10F, 1F);
    				}
				}
			}
			// Runs if the player is going to the nether
			else {
				// Changes the world to the game world
				goingTo.setWorld(Bukkit.getWorld("svh-nether"));
				
				// Runs if the player is the runner
				if (isRunner) {
		    		// Adds an empty entry for better spacing if there is none
    				if (!infoBoard.getScore(ChatColor.LIGHT_PURPLE.toString()).isScoreSet()) infoBoard.getScore(ChatColor.LIGHT_PURPLE.toString()).setScore(13);
					
    				// Adds the portal location label if it isn't there
    				if (!infoBoard.getScore(ChatColor.DARK_AQUA + "Portal Location:").isScoreSet()) infoBoard.getScore(ChatColor.DARK_AQUA + "Portal Location:").setScore(7);
    				
    				// Stores the portal location
    				Location portalLocation = event.getFrom();
    				
    				// Gets the portal x coord team
    				Team xCoordTeam = scoreboard.getTeam("portalXcoord");
    				// Creates the x coord team if it doesn't exist
    				if (xCoordTeam == null) xCoordTeam = scoreboard.registerNewTeam("portalXcoord");
    				// Adds the entry to the team
    				xCoordTeam.addEntry(ChatColor.AQUA + "Portal X: ");
    				// Adds the info for the entry
    				xCoordTeam.setSuffix(portalLocation.getBlockX() + "");
    				// Sets the score for the entry to display it in the objective
    				infoBoard.getScore(ChatColor.AQUA + "Portal X: ").setScore(6);

					// Gets the y coord team
    				Team yCoordTeam = scoreboard.getTeam("portalYcoord");
    				// Creates the y coord team if it doesn't exist
    				if (yCoordTeam == null) yCoordTeam = scoreboard.registerNewTeam("portalYcoord");
    				// Adds the entry to the team
    				yCoordTeam.addEntry(ChatColor.AQUA + "Portal Y: ");
    				// Adds the info for the entry
    				yCoordTeam.setSuffix(portalLocation.getBlockY() + "");
    				// Sets the score for the entry to display it in the objective
    				infoBoard.getScore(ChatColor.AQUA + "Portal Y: ").setScore(5);

					// Gets the z coord team
    				Team zCoordTeam = scoreboard.getTeam("portalZcoord");
    				// Creates the z coord team if it doesn't exist
    				if (zCoordTeam == null) zCoordTeam = scoreboard.registerNewTeam("portalZcoord");
    				// Adds the entry to the team
    				zCoordTeam.addEntry(ChatColor.AQUA + "Portal Z: ");
    				// Adds the info for the entry
    				zCoordTeam.setSuffix(portalLocation.getBlockZ() + "");
    				// Sets the score for the entry to display it in the objective
    				infoBoard.getScore(ChatColor.AQUA + "Portal Z: ").setScore(4);
					
					// Runs for every player
    				for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
    					// Sends an action bar to let the player know the player has entered the nether
    					currentPlayer.sendActionBar(ChatColor.GOLD + "The runner has entered the nether.");
    					// Plays a sound to draw attention to the dimension change
    					currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.VOICE, 10F, 1F);
    				}
				}
			}
		}
		// Runs if the user is going through a end portal
		else if(goingTo.getWorld().getEnvironment().equals(Environment.THE_END)) {
			// Runs if the player is in the end
			if(event.getFrom().getWorld().getEnvironment().equals(Environment.THE_END)) {
				// Sets the player to spectator mode
				player.setGameMode(GameMode.SPECTATOR);
				// Sets going to to the overworld spawn
				goingTo = Bukkit.getWorld("svh-overworld").getSpawnLocation();
				// Sets the y value to be 10 blocks above the ground
				goingTo.setY(goingTo.getWorld().getHighestBlockYAt(goingTo) + 10);
				// Teleports the player to that block
				player.teleportAsync(goingTo);
				
				// Runs if the player is the runner
				if (isRunner) {
					// Gets the game ender class
					GameEnder gameEnder = new GameEnder();
					// Runs the method to end the game
					gameEnder.endGame(true);
				}
				
				// Cancels the event
				event.setCancelled(true);
				// Exits the method
				return;
			}
			// Runs if the player is going to the end
			else {
				// Changes the world to the game world
				goingTo.setWorld(Bukkit.getWorld("svh-end"));
				
				// Runs if the player is the runner
				if (isRunner) {
		    		// Adds an empty entry for better spacing if there is none
    				if (!infoBoard.getScore(ChatColor.LIGHT_PURPLE.toString()).isScoreSet()) infoBoard.getScore(ChatColor.LIGHT_PURPLE.toString()).setScore(13);
    				
    				// Adds the player location label if it isn't there
    				if (!infoBoard.getScore(ChatColor.DARK_AQUA + "Player Location:").isScoreSet()) infoBoard.getScore(ChatColor.DARK_AQUA + "Player Location:").setScore(12);
					// Gets the world team
    				Team worldTeam = scoreboard.getTeam("world");
    				// Creates the world team if it doesn't exist
    				if (worldTeam == null) worldTeam = scoreboard.registerNewTeam("world");
    				// Adds the entry to the team
    				worldTeam.addEntry(ChatColor.AQUA + "World: ");
					// Sets the world to the end
					worldTeam.setSuffix("The End");
    				// Sets the score for the entry to display it in the objective
    				infoBoard.getScore(ChatColor.AQUA + "World: ").setScore(11);
    				
					// Deletes the x if it exists
					if (infoBoard.getScore(ChatColor.AQUA + "X: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "X: ");
					// Deletes the y if it exists
					if (infoBoard.getScore(ChatColor.AQUA + "Y: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Y: ");
					// Deletes the z if it exists
					if (infoBoard.getScore(ChatColor.AQUA + "Z: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Z: ");
					
    				// Adds the portal location label if it isn't there
    				if (!infoBoard.getScore(ChatColor.DARK_AQUA + "Portal Location:").isScoreSet()) infoBoard.getScore(ChatColor.DARK_AQUA + "Portal Location:").setScore(7);
    				
    				// Stores the portal location
    				Location portalLocation = event.getFrom();
    				
    				// Gets the portal x coord team
    				Team xCoordTeam = scoreboard.getTeam("portalXcoord");
    				// Creates the x coord team if it doesn't exist
    				if (xCoordTeam == null) xCoordTeam = scoreboard.registerNewTeam("portalXcoord");
    				// Adds the entry to the team
    				xCoordTeam.addEntry(ChatColor.AQUA + "Portal X: ");
    				// Adds the info for the entry
    				xCoordTeam.setSuffix(portalLocation.getBlockX() + "");
    				// Sets the score for the entry to display it in the objective
    				infoBoard.getScore(ChatColor.AQUA + "Portal X: ").setScore(6);

					// Gets the y coord team
    				Team yCoordTeam = scoreboard.getTeam("portalYcoord");
    				// Creates the y coord team if it doesn't exist
    				if (yCoordTeam == null) yCoordTeam = scoreboard.registerNewTeam("portalYcoord");
    				// Adds the entry to the team
    				yCoordTeam.addEntry(ChatColor.AQUA + "Portal Y: ");
    				// Adds the info for the entry
    				yCoordTeam.setSuffix(portalLocation.getBlockY() + "");
    				// Sets the score for the entry to display it in the objective
    				infoBoard.getScore(ChatColor.AQUA + "Portal Y: ").setScore(5);

					// Gets the z coord team
    				Team zCoordTeam = scoreboard.getTeam("portalZcoord");
    				// Creates the z coord team if it doesn't exist
    				if (zCoordTeam == null) zCoordTeam = scoreboard.registerNewTeam("portalZcoord");
    				// Adds the entry to the team
    				zCoordTeam.addEntry(ChatColor.AQUA + "Portal Z: ");
    				// Adds the info for the entry
    				zCoordTeam.setSuffix(portalLocation.getBlockZ() + "");
    				// Sets the score for the entry to display it in the objective
    				infoBoard.getScore(ChatColor.AQUA + "Portal Z: ").setScore(4);
					
    				// Creates the key for the boss bar
    				NamespacedKey barKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"), "svhBar");
    				// Gets the boss bar
    				KeyedBossBar bossBar = Bukkit.getServer().getBossBar(barKey);
    				// Removes the boss bar if it exists
    				if (bossBar != null) bossBar.setVisible(false);
    				
					// Runs for every player
    				for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
    					// Sends an action bar to let the player know the player has entered the end
    					currentPlayer.sendActionBar(ChatColor.GOLD + "The runner has entered the end.");
    					// Plays a sound to draw attention to the dimension change
    					currentPlayer.playSound(currentPlayer.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.VOICE, 10F, 1F);
    				}
				}
			}
		}
		
		// Sets the new location for the event
		event.setTo(goingTo);
	}
}
