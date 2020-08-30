package me.RedTheITGuy.SpeedrunnerVsHunters;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class PlayerManager {
	public void setPlayerCount(int countAlt) {
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		
		// Exits if the player count isn't needed
		if (infoBoard == null || !infoBoard.getScore(ChatColor.AQUA + "Players: ").isScoreSet()) return;
		
		// Loads the config
		FileConfiguration config = Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters").getConfig();
		// Gets info from the config
		int minPlayers = config.getInt("minPlayers");
		boolean autostart = config.getBoolean("doAutostart");
		final int gameStartTime = config.getInt("gameStartTime");
		
		// Creates the variable to store the online player count
		int onlinePlayers = countAlt;
		// Runs for all online players
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			// Adds to the online player count if the player is in survival
			if (player.getGameMode().equals(GameMode.SURVIVAL)) onlinePlayers++;
		}
		
		// Gets the gamemode team 
		Team playersTeam = scoreboard.getTeam("players");
		// Creates the team if it doesn't exist
		if (playersTeam == null) playersTeam = scoreboard.registerNewTeam("players");
		// Adds the players entry
		playersTeam.addEntry(ChatColor.AQUA + "Players: ");
		// Sets the info in the players entry
		playersTeam.setSuffix(onlinePlayers + "/" + minPlayers);
		
		// Exits if autostart is diabled
		if (!autostart) return;
		
		// Creates the key for the boss bar
		final NamespacedKey barKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"), "svhBar");
		// Gets the boss bar
		KeyedBossBar bossBar = Bukkit.getServer().getBossBar(barKey);
		
		// Runs if the game should start the count down for autostart
		if (onlinePlayers >= minPlayers) {
			// Exits if the count down is already happening
			if (bossBar != null && bossBar.isVisible()) return;
			
			// Runs if the bar doesn't exist
			if (bossBar == null) {
				// Creates the bar
				bossBar = Bukkit.getServer().createBossBar(barKey, "", BarColor.BLUE, BarStyle.SOLID);
			}
			else {
				// Sets the bar style
				bossBar.setStyle(BarStyle.SOLID);
				// Sets the bar colour
				bossBar.setColor(BarColor.BLUE);
			}
			// Sets the bar to visible
			bossBar.setVisible(true);
			// Sets the bar to full for the start
			bossBar.setProgress(1.0);
			// Sets the title of the boss bar
			bossBar.setTitle(ChatColor.GOLD + "Game starts in " + gameStartTime + ":00");
			
			// Runs for every player
			for (Player player : Bukkit.getOnlinePlayers()) {
				// Displays the bar to the player
				bossBar.addPlayer(player);
			}
			
			// Gets the bukkit scheduler
			final BukkitScheduler scheduler = Bukkit.getScheduler();
			// Runs every 20 ticks (second)
			scheduler.runTaskTimer(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"), new Runnable() {
				public void run() {
					// Gets the boss bar
					KeyedBossBar bossBar = Bukkit.getServer().getBossBar(barKey);
					// Runs if the bar is gone
					if (bossBar == null || !bossBar.isVisible()) {
						// Stops all tasks scheduled by this plugin
			    		scheduler.cancelTasks(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"));
			    		// Exits the runnable
			    		return;
					}
					
					// Gets the boss bar title
					String barTitle = bossBar.getTitle();
					// Strips the colour so it doesn't mess up the code
					barTitle = ChatColor.stripColor(barTitle);
					// Gets the time from the boss bar
					String[] barTime = barTitle.replaceAll("[^:\\d]", "").split(":");
					// Converts the minutes and seconds to integers
			    	int minsLeft = Integer.parseInt(barTime[0]);
			    	int secsLeft = Integer.parseInt(barTime[1]);
			    	
			    	// Subtracts 1 from the seconds
			    	secsLeft--;
			    	// Runs if the seconds left has reached 0
			    	if (secsLeft < 0) {
			    		// Resets the seconds to 59
			    		secsLeft = 59;
			    		// Subtracts 1 from the minutes
			    		minsLeft--;
			    		// Runs if the minutes left has reached 0
			    		if (minsLeft < 0) {
			    			// Stops all tasks scheduled by this plugin
				    		scheduler.cancelTasks(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"));
				    		// Removes all players from the boss bar
							bossBar.removeAll();
							// Removes the bar
							Bukkit.getServer().removeBossBar(barKey);
							
							// Creates the array list to store the online players
							ArrayList<Player> onlinePlayers = new ArrayList<Player>();
							// Runs for all online players
							for (Player player : Bukkit.getServer().getOnlinePlayers()) {
								// Adds to the online players array if the player is in survival
								if (player.getGameMode().equals(GameMode.SURVIVAL)) onlinePlayers.add(player);
							}
							// Creates the random for random numbers
							Random random = new Random();
							// Creates an variable to store the number used to get the player
							int playerNum = 0;
							// Generates a random player number is there is more than 1 player
							if (onlinePlayers.size() > 1) playerNum = random.nextInt(onlinePlayers.size() - 1);
							// Loads the game logic class
							GameLogic gameLogic = new GameLogic();
							// Runs the method to play the game with a random player
							gameLogic.playGame(onlinePlayers.get(playerNum));
							// Exits the runnable
							return;
			    		}
			    	}
			    	// Changes the title string to what the title will be
			    	barTitle = barTitle.replaceAll("[:\\d]", "") + minsLeft + ":" + String.format("%02d", secsLeft);
			    	// Sets the bars title
			    	bossBar.setTitle(ChatColor.GOLD + barTitle);
			    	
			    	// Calculates the bar progress
		    		double barProgress = (((double) minsLeft * 60) + secsLeft) / (gameStartTime * 60);
		    		// Sets the bars progress
			    	bossBar.setProgress(barProgress);
				}
			}, 20, 20);
		}
		// Runs if the autostart count down shouldn't be running
		else {
			// Exits if the countdown isn't happening
			if (bossBar == null || !bossBar.isVisible()) return;
			
			// Removes all players from the boss bar
			bossBar.removeAll();
			// Removes the bar
			Bukkit.getServer().removeBossBar(barKey);
		}
	}
}
