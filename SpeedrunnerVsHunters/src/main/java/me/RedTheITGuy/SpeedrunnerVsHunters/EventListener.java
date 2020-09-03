package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World.Environment;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class EventListener implements Listener {
	// Runs when a player is has joined the game
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Stores the joining player
		Player player = event.getPlayer();
		
		// Creates the key for the boss bar
		NamespacedKey barKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"), "svhBar");
		// Gets the boss bar
		KeyedBossBar bossBar = Bukkit.getServer().getBossBar(barKey);
		// Adds the player to the boss bar if it exists
		if (bossBar != null) bossBar.addPlayer(player);
		
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		
		// Returns if the info board doensn't exist
		if (infoBoard == null) return;
		

		// Gets the class to set the player count
		PlayerManager playerManager = new PlayerManager();
		// Sets the player count
		playerManager.setPlayerCount(0);
		
		// Runs if the player is joining a non game world
		if (!player.getLocation().getWorld().getName().contains("svh-")) {
			// Clears the player's inventory
			player.getInventory().clear();
			// Resets players xp
			player.setExp(0);
			// Heals player to full health
			player.setHealth(20);
			// Restores player's hunger
			player.setFoodLevel(20);
			// Restores players saturation
			player.setSaturation(5);
			// Restores players exhaustion
			player.setExhaustion(0);
			
			// Runs if a game is running and hunters have been released
			if (infoBoard.getScore(ChatColor.AQUA + "Kills: ").isScoreSet()) {
				// Gets the class for spawning the player
				SpawnManager spawner = new SpawnManager();
				// Sets the spawn location
				player.teleportAsync(spawner.getSpawnLocation());
			}
		}
		
		// Runs if a game is running
		if (infoBoard.getScore(ChatColor.AQUA + "Runner: ").isScoreSet()) {
			// Runs if the player is not the runner
			if (!scoreboard.getTeam("runnerName").getSuffix().equalsIgnoreCase(player.getDisplayName())) {
				// Gets the hunters team
				Team huntersTeam = scoreboard.getTeam("hunters");
				// Adds the player to the hunters team if it exists
				if (huntersTeam != null) huntersTeam.addEntry(player.getName());
			}
			// Runs if the player is the runner and there is a boss bar
			else if (bossBar != null) {
				// Loads the config
				FileConfiguration config = Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters").getConfig();
				// Gets info from the config
				final int headStart = config.getInt("game.headStart");
				final int locationRevealTime = config.getInt("game.locationRevealTime");
				
				// Runs if the player is in the end
				if (player.getWorld().getEnvironment().equals(Environment.THE_END)) {
					// Hides the boss bar
					bossBar.setVisible(false);
				}
				else {
					// Gets the current run time with minutes and seconds split
					String[] runTime = scoreboard.getTeam("runTime").getSuffix().split(":");
					// Creates variables to store the times
					int mins = 0;
					int secs = 0;
					if (runTime.length <= 2) {
						// Converts the minutes and seconds to integers
				    	mins = Integer.parseInt(runTime[0]);
				    	secs = Integer.parseInt(runTime[1]);
					}
					else {
						// Converts the minutes and seconds to integers
				    	mins = Integer.parseInt(runTime[1]) + (Integer.parseInt(runTime[0]) * 60);
				    	secs = Integer.parseInt(runTime[2]);
					}
					
					// Adds 1 to the minutes if the seconds are over 0
					if (secs > 0) mins++;
					// Removes the head start time from the minutes
					mins -= headStart;
					
					// Runs if minutes are less that 0 AKA hunters have not been released
					if (mins <= 0) {
						// Makes the minutes positive
						mins = mins * -1;
						// Inverts the seconds if needed
						if (secs > 0) secs = 60 - secs;
						
						// Sets the title of the boss bar
						bossBar.setTitle(ChatColor.GOLD + "Hunters released in " + mins + ":" + secs);
			    		// Calculates the bar progress
			    		double barProgress = (((double) mins * 60) + secs) / (headStart * 60);
				    	// Sets the bars progress
				    	bossBar.setProgress(barProgress);
					}
					// Runs if the bar is for a location reveal
					else {
						// Gets the time from the last location reveal
						mins = mins % locationRevealTime;
						// Gets the time to the next location reveal
						if (mins != 0) mins = locationRevealTime - mins;
						// Inverts the seconds if needed
						if (secs > 0) secs = 60 - secs;
						
						// Sets the title of the boss bar
						bossBar.setTitle(ChatColor.GOLD + "Location revealed in " + mins + ":" + secs);
			    		// Calculates the bar progress
			    		double barProgress = (((double) mins * 60) + secs) / (locationRevealTime * 60);
				    	// Sets the bars progress
				    	bossBar.setProgress(barProgress);
					}
				}
				
				// Runs for every player
				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					// Sends an action bar to let the player know the location has been revealed
					onlinePlayer.sendActionBar(ChatColor.GOLD + "The runner has rejoined the server.");
					// Plays a sound to draw attention to the location reveal
					onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, SoundCategory.VOICE, 10F, 1F);
				}
			}
		}
		
		// Runs if the game is over
		if (infoBoard.getScore(ChatColor.AQUA + "Winner: ").isScoreSet()) {
			// Sets the player to spectator mode
			player.setGameMode(GameMode.SPECTATOR);
		}
		else {
			// Sets the player to survival mode
			player.setGameMode(GameMode.SURVIVAL);
		}
	}
	
	// Runs when a player leaves the server
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		// Loads the config
		FileConfiguration config = Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters").getConfig();
		// Gets info from the config
		int resetTime = config.getInt("game.runnerLeaveTime");
		
		// Stores the joining player
		Player player = event.getPlayer();
		
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		

		// Gets the class to set the player count
		PlayerManager playerManager = new PlayerManager();
		// Runs if the player is in survival
		if (player.getGameMode().equals(GameMode.SURVIVAL)) {
			// Sets the player count
			playerManager.setPlayerCount(-1);
		}
		
		
		// Returns if a game isn't running
		if (infoBoard == null || !infoBoard.getScore(ChatColor.AQUA + "Runner: ").isScoreSet()) return;
		
		// Returns if the game is over
		if (infoBoard.getScore(ChatColor.AQUA + "Winner: ").isScoreSet()) return;
		
		// Returns if the player is not the runner
		if (!scoreboard.getTeam("runnerName").getSuffix().equalsIgnoreCase(player.getDisplayName())) return;
		
		// Creates the key for the boss bar
		NamespacedKey barKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"), "svhBar");
		// Gets the boss bar
		KeyedBossBar bossBar = Bukkit.getServer().getBossBar(barKey);
		// Runs if the bar doesn't exist
		if (bossBar == null) {
			// Creates the bar
			bossBar = Bukkit.getServer().createBossBar(barKey, "", BarColor.BLUE, BarStyle.SOLID);
		}
		
		// Sets the bar to visible
		bossBar.setVisible(true);
		// Sets the bar to full for the start
		bossBar.setProgress(1.0);
		// Sets the title of the boss bar
		bossBar.setTitle(ChatColor.GOLD + "Game ending in " + resetTime + ":00");
		
		// Runs for every player
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			// Sends an action bar to let the player know the location has been revealed
			onlinePlayer.sendActionBar(ChatColor.GOLD + "The runner has left the server.");
			// Plays a sound to draw attention to the location reveal
			onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_GHAST_HURT, SoundCategory.VOICE, 10F, 1F);
		}
		
	}
	
	// Runs when a player dies
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		// Stores the dead player
		Player player = event.getEntity();
		
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		
		// Exit method if the board doesn't exist or a game isn't running
		if (infoBoard == null || !infoBoard.getScore(ChatColor.AQUA + "Runner: ").isScoreSet()) return;
		
		// Runs if the dead player was the runner
		if (scoreboard.getTeam("runnerName").getSuffix().equalsIgnoreCase(player.getDisplayName())) {
			// Sets the player's gamemode to spectator
			player.setGameMode(GameMode.SPECTATOR);
			
			// Gets the game ender class
			GameEnder gameEnder = new GameEnder();
			// Runs the method to end the game
			gameEnder.endGame(false);
			
			// Cancels the event
			event.setCancelled(true);
			// Exits the method
			return;
		}
		
		// Runs if the player was killed by the runner
		if (event.getEntity().getKiller().equals(Bukkit.getPlayer(scoreboard.getTeam("runnerName").getSuffix())))  {
			// Creates a variable to store the kill count of the runner
			int runnerKillCount = 1;
			
			// Gets the kill count team
			Team killCountTeam = scoreboard.getTeam("killCount");
			// Creates the runner name team if it doesn't exist
			if (killCountTeam == null) killCountTeam = scoreboard.registerNewTeam("killCount");
			// Sets the kill count to the right value if it was set before
			if (killCountTeam.getSuffix() != null) runnerKillCount = Integer.parseInt(killCountTeam.getSuffix()) + 1;
			// Adds the entry to the team
			killCountTeam.addEntry(ChatColor.AQUA + "Kills: ");
			// Adds the info for the entry
			killCountTeam.setSuffix(runnerKillCount + "");
			// Sets the score for the entry to display it in the objective if it was not already set
			if (!infoBoard.getScore(ChatColor.AQUA + "Kills: ").isScoreSet()) infoBoard.getScore(ChatColor.AQUA + "Kills: ").setScore(2);
		}
	}
	
	// Runs when a player respawns
	@EventHandler
	public void onPlayerSpawn(PlayerRespawnEvent event) {
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		
		// Exits the method if the hunter release hasn't happened
		if (!infoBoard.getScore(ChatColor.AQUA + "Kills: ").isScoreSet()) return;
		
		// Runs if the player is not going to spawn in the game world
		if (!event.getRespawnLocation().getWorld().getName().contains("svh-")) {
			// Gets the class for spawning the player
			SpawnManager spawner = new SpawnManager();
			// Sets the spawn location
			event.setRespawnLocation(spawner.getSpawnLocation());
		}
	}
	
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
	
	// Runs when a player changes gamemode
	@EventHandler
	public void onGameModeChange(PlayerGameModeChangeEvent event) {
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		
		// Runs if player needs to be added to the player count on the scoreboard
		if (infoBoard !=null && infoBoard.getScore(ChatColor.AQUA + "Players: ").isScoreSet()) {
			// Returns if the player did not switch to or from survival
			if (!event.getPlayer().getGameMode().equals(GameMode.SURVIVAL) && !event.getNewGameMode().equals(GameMode.SURVIVAL)) return;
			// Gets the class to set the player count
			PlayerManager playerManager = new PlayerManager();
			// Runs if the player is switching to survival
			if (event.getNewGameMode().equals(GameMode.SURVIVAL)) {
				// Sets the player count
				playerManager.setPlayerCount(1);
			}
			// Runs if the player is switching away from survival
			else {
				// Sets the player count
				playerManager.setPlayerCount(-1);
			}
		}
	}
}
